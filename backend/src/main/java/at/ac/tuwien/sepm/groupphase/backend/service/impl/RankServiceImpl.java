package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupPreference;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupWeight;
import at.ac.tuwien.sepm.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleWeight;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.entity.Weight;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupPreferenceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupWeightRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleWeightRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.RankService;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RankServiceImpl implements RankService {
    static final int FEATURE_COUNT = 6;
    static final double MIN_DISTANCE = 2;
    static final double MAX_DISTANCE = 7;
    static final double MIN_SD = 1.5;
    static final double MIN_CV = 0.1;
    static final double WEIGHT_ADJUSTMENT = 5;
    private static final double MIN_WEIGHT = 0.1;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int GROUP_FEATURE_COUNT = 4;
    private final SingleWeightRepository weightRepository;
    private final PreferenceRepository preferenceRepository;
    private final GroupWeightRepository groupWeightRepository;
    private final GroupPreferenceRepository groupPreferenceRepository;
    private final MatchService singleMatchService;
    private final StudentService studentService;

    public RankServiceImpl(SingleWeightRepository weightRepository, PreferenceRepository preferenceRepository, GroupWeightRepository groupWeightRepository, GroupPreferenceRepository groupPreferenceRepository, MatchService matchService,
                           StudentService studentService) {
        this.weightRepository = weightRepository;
        this.preferenceRepository = preferenceRepository;
        this.groupWeightRepository = groupWeightRepository;
        this.groupPreferenceRepository = groupPreferenceRepository;
        this.singleMatchService = matchService;
        this.studentService = studentService;
    }

    private static double[] getCoefficientOfVariation(double[] meanPerFeature, double[] sdPerFeature) {
        double[] cv = new double[sdPerFeature.length];

        for (int i = 0; i < sdPerFeature.length; i++) {
            if (meanPerFeature[i] > 0.01) {
                cv[i] = sdPerFeature[i] / meanPerFeature[i];
            } else {
                if (sdPerFeature[i] < 0.0001) {
                    cv[i] = sdPerFeature[i];
                }
                cv[i] = -1;
            }
        }

        return cv;
    }

    @Override
    public List<Student> rankStudents(Long baseStudentId, List<Student> recommendees) throws NotFoundException {
        LOGGER.trace("Rank students {} for student with id {}", recommendees, baseStudentId);
        var distancePerStudent = getDistances(baseStudentId, recommendees);

        Integer[] indices = getSortedIndices(distancePerStudent);

        // Create a new list for sorted recommendees
        List<Student> rankedStudents = new ArrayList<>();
        for (int index : indices) {
            rankedStudents.add(recommendees.get(index));
        }
        return rankedStudents;
    }

    @Override
    public List<Group> rankGroups(Long baseStudentId, List<Group> recommendees) throws NotFoundException {
        LOGGER.trace("Rank groups {} for student with id {}", recommendees, baseStudentId);
        var distancePerGroup = getGroupDistances(baseStudentId, recommendees);

        Integer[] indices = getSortedIndices(distancePerGroup);

        List<Group> rankedGroups = new ArrayList<>();
        for (int index : indices) {
            rankedGroups.add(recommendees.get(index));
        }
        return rankedGroups;
    }

    @Override
    public List<Pair<Student, Double>> rankStudentsDemo(Long baseStudentId, List<Student> recommendees) throws NotFoundException {
        LOGGER.trace("Rank groups {} for student with id {} for demo mode", recommendees, baseStudentId);

        var distancePerStudent = getDistances(baseStudentId, recommendees);

        Integer[] indices = getSortedIndices(distancePerStudent);

        // Create a new list for sorted recommendees
        List<Pair<Student, Double>> rankedStudents = new ArrayList<>();
        for (int index : indices) {
            rankedStudents.add(Pair.of(recommendees.get(index), distancePerStudent[index]));
        }
        return rankedStudents;
    }

    @Override
    public Map<Student, double[]> getDistancesForDemo(Long baseStudentId, List<Student> recommendees) throws NotFoundException {
        LOGGER.trace("Get distances for {} and base student {}", recommendees, baseStudentId);
        Student baseStudent = studentService.findStudentById(baseStudentId);
        var allStudents = new ArrayList<Student>(recommendees);
        allStudents.add(allStudents.size(), baseStudent);

        //get normalized feature vectors
        var normalizedStudentFeatureMatrix = studentsToNormalizedVectors(allStudents, baseStudent);

        //calculate distance
        double[] weights = getWeight(baseStudentId).getVector();
        Map<Student, double[]> studentFeatureDistancesMap = new HashMap<>();

        for (int i = 0; i < recommendees.size(); i++) {
            double[] distancesPerFeature = new double[FEATURE_COUNT + 1];
            double distanceSum = 0;
            for (int j = 0; j < FEATURE_COUNT; j++) {
                distancesPerFeature[j] =
                    calcDistance(normalizedStudentFeatureMatrix[i][j],
                        normalizedStudentFeatureMatrix[normalizedStudentFeatureMatrix.length - 1][j], weights[j]);
                distanceSum += distancesPerFeature[j];
            }
            distanceSum = Math.sqrt(distanceSum);
            distancesPerFeature[distancesPerFeature.length - 1] = distanceSum;
            studentFeatureDistancesMap.put(recommendees.get(i), distancesPerFeature);
        }
        return studentFeatureDistancesMap;

    }

    @Override
    public List<Pair<Group, Double>> rankGroupsDemo(Long baseStudentId, List<Group> recommendees) {
        LOGGER.trace("Rank groups {} for student with id {} for demo mode", recommendees, baseStudentId);
        var distancePerGroup = getGroupDistances(baseStudentId, recommendees);

        Integer[] indices = getSortedIndices(distancePerGroup);

        List<Pair<Group, Double>> rankedGroups = new ArrayList<>();
        for (int index : indices) {
            rankedGroups.add(Pair.of(recommendees.get(index), distancePerGroup[index]));
        }
        return rankedGroups;
    }

    @Override
    public Map<Group, double[]> getGroupDistancesForDemo(Long baseStudentId, List<Group> recommendees) throws NotFoundException {
        LOGGER.trace("Get group distances for {} and base student {}", recommendees, baseStudentId);

        Student baseStudent = studentService.findStudentById(baseStudentId);
        double[][] normalizedGroupFeatureMatrix = groupsToNormalizedVectors(recommendees, baseStudent);

        double[] weights = getGroupWeight(baseStudentId).getVector();
        Map<Group, double[]> groupFeatureDistancesMap = new HashMap<>();

        for (int i = 0; i < recommendees.size(); i++) {
            double[] distancesPerFeature = new double[GROUP_FEATURE_COUNT + 1];
            double distanceSum = 0;
            for (int j = 0; j < GROUP_FEATURE_COUNT; j++) {
                distancesPerFeature[j] =
                    calcDistance(normalizedGroupFeatureMatrix[i][j],
                        normalizedGroupFeatureMatrix[normalizedGroupFeatureMatrix.length - 1][j], weights[j]);
                distanceSum += distancesPerFeature[j];
            }
            distanceSum = Math.sqrt(distanceSum);
            distancesPerFeature[distancesPerFeature.length - 1] = distanceSum;
            groupFeatureDistancesMap.put(recommendees.get(i), distancesPerFeature);
        }

        return groupFeatureDistancesMap;
    }

    @Override
    public SingleWeight getWeight(Long studentId) throws NotFoundException {
        LOGGER.trace("Get weight for student with id {}", studentId);

        var student = studentService.findStudentById(studentId);
        SingleWeight weight = new SingleWeight(student, 1);
        var weightOptional = weightRepository.findByStudent(student);
        if (weightOptional.isPresent()) {
            weight = weightOptional.get();
        }
        return weight;
    }

    @Override
    public GroupWeight getGroupWeight(Long studentId) throws NotFoundException {
        LOGGER.trace("Get group weight for student with id {}", studentId);

        var student = studentService.findStudentById(studentId);
        GroupWeight weight = new GroupWeight(student, 1);
        var weightOptional = groupWeightRepository.findByStudent(student);
        if (weightOptional.isPresent()) {
            weight = weightOptional.get();
        }
        return weight;
    }

    @Override
    public Preference getPreference(Long studentId) {
        LOGGER.trace("Get preference for student with id {}", studentId);

        var student = studentService.findStudentById(studentId);
        Preference preference = new Preference(student);
        var preferenceOptional = preferenceRepository.findByStudent(student);
        if (preferenceOptional.isPresent()) {
            preference = preferenceOptional.get();
        }
        return preference;
    }

    @Override
    public GroupPreference getGroupPreference(Long studentId) throws NotFoundException {
        LOGGER.trace("Get group preference for student with id {}", studentId);

        var student = studentService.findStudentById(studentId);
        GroupPreference preference = new GroupPreference(student);
        var preferenceOptional = groupPreferenceRepository.findByStudent(student);
        if (preferenceOptional.isPresent()) {
            preference = preferenceOptional.get();
        }
        return preference;
    }

    @Override
    public SingleWeight updateWeights(Long studentId) throws NotFoundException {
        LOGGER.trace("Update weights for student with id {}", studentId);
        return updateWeights(studentId, 10, 10);
    }

    @Override
    public SingleWeight updateWeights(Long studentId, int minData, int maxData) throws NotFoundException {
        LOGGER.trace("Update weights for student with id {} using min: {} and max: {} data", studentId, minData, maxData);

        var student = studentService.findStudentById(studentId);

        SingleWeight weight = getWeight(student.getId());

        var positives = singleMatchService.getLikedAndMatchedStudents(student.getId(), maxData);
        var negatives = singleMatchService.getDislikedStudents(student.getId(), maxData);

        if (negatives.size() + positives.size() < minData) {
            LOGGER.debug("Weight update for {} failed (not enough data available)", student);
            return weight;
        }


        if (positives.size() >= Math.max(1, minData / 3)) {
            double[] cv = getCoefficientsOfVariation(student, positives);
            var positivesInclBase = new ArrayList<Student>(positives);
            positivesInclBase.add(positivesInclBase.size(), student);
            double[][] normalizedPositiveStudentFeatureMatrix = studentsToNormalizedVectors(positivesInclBase, student);
            double[][] featuresStudentsDistanceVectorsPositives = new double[FEATURE_COUNT][positives.size()];
            double[] distancePerFeaturePositives = getDistancePerFeature(weight.getVector(), normalizedPositiveStudentFeatureMatrix, featuresStudentsDistanceVectorsPositives);
            double maxDistancePositives = Arrays.stream(distancePerFeaturePositives).max().orElse(0);

            if (maxDistancePositives > 0) {
                for (int feature = 0; feature < FEATURE_COUNT; feature++) {
                    double adjustmentFactorPositives = WEIGHT_ADJUSTMENT + MIN_WEIGHT - (WEIGHT_ADJUSTMENT) * (distancePerFeaturePositives[feature] / maxDistancePositives);

                    if (distancePerFeaturePositives[feature] < MIN_DISTANCE) {
                        LOGGER.debug("manipulate weight based on likes for feature {} by {} (increase)", feature, adjustmentFactorPositives);
                        manipulateWeight(weight, feature, adjustmentFactorPositives);
                    } else if (distancePerFeaturePositives[feature] > MAX_DISTANCE) {
                        if (cv[feature] < MIN_CV && cv[feature] != -1 && positives.size() >= 10 && Preference.isUpdatableFeature(feature)) {
                            LOGGER.debug("trigger update preferences due to feature {} (cv: {}; distance: {}", feature, cv[feature], distancePerFeaturePositives[feature]);
                            updatePreference(studentId);
                            return getWeight(studentId);
                        } else {
                            LOGGER.debug("manipulate weight based on likes for feature {} by {} (decrease)", feature, adjustmentFactorPositives);
                            manipulateWeight(weight, feature, adjustmentFactorPositives);
                        }
                    }
                }
            }
        }

        if (negatives.size() >= Math.max(1, minData / 3)) {
            var negativesInclBase = new ArrayList<Student>(negatives);
            negativesInclBase.add(negativesInclBase.size(), student);
            double[][] normalizedNegativeStudentFeatureMatrix = studentsToNormalizedVectors(negativesInclBase, student);
            double[][] featuresStudentsDistanceVectorsNegatives = new double[FEATURE_COUNT][negatives.size()];
            double[] distancePerFeatureNegatives = getDistancePerFeature(weight.getVector(), normalizedNegativeStudentFeatureMatrix, featuresStudentsDistanceVectorsNegatives);
            double maxDistanceNegatives = Arrays.stream(distancePerFeatureNegatives).max().orElse(0);
            if (maxDistanceNegatives > 0) {
                for (int feature = 0; feature < FEATURE_COUNT; feature++) {
                    double adjustmentFactorNegatives = WEIGHT_ADJUSTMENT * (distancePerFeatureNegatives[feature] / maxDistanceNegatives) + MIN_WEIGHT;

                    if (distancePerFeatureNegatives[feature] < MIN_DISTANCE) {
                        LOGGER.debug("manipulate weight based on dislikes for feature {} by {} (decrease)", feature, adjustmentFactorNegatives);
                        manipulateWeight(weight, feature, adjustmentFactorNegatives);
                    } else if (distancePerFeatureNegatives[feature] > MAX_DISTANCE) {
                        LOGGER.debug("manipulate weight based on dislikes for feature {} by {} (increase)", feature, adjustmentFactorNegatives);
                        manipulateWeight(weight, feature, adjustmentFactorNegatives);
                    }
                }
            }
        }

        return weightRepository.save(weight);
    }

    @Override
    public GroupWeight updateGroupWeights(Long studentId) throws NotFoundException {
        LOGGER.trace("Update weights for student with id {}", studentId);
        return updateGroupWeights(studentId, 10, 10);
    }

    @Override
    public GroupWeight updateGroupWeights(Long studentId, int minData, int maxData) throws NotFoundException {
        LOGGER.trace("Get group weights for student with id {} using min: {} and max: {} data", studentId, minData, maxData);

        var student = studentService.findStudentById(studentId);

        GroupWeight weight = getGroupWeight(student.getId());

        var positives = singleMatchService.getLikedAndMatchedGroups(student.getId(), maxData);
        var negatives = singleMatchService.getDislikedGroups(student.getId(), maxData);

        if (negatives.size() + positives.size() < minData) {
            LOGGER.debug("Group weight update for {} failed (not enough data available)", student);
            return weight;
        }

        if (positives.size() >= Math.max(1, minData / 3)) {
            double[] cv = getCoefficientsOfVariationGroups(student, positives);
            double[][] normalizedPositiveStudentFeatureMatrix = groupsToNormalizedVectors(positives, student);
            double[][] featuresStudentsDistanceVectorsPositives = new double[GROUP_FEATURE_COUNT][positives.size()];
            double[] distancePerFeaturePositives = getDistancePerFeature(weight.getVector(), normalizedPositiveStudentFeatureMatrix, featuresStudentsDistanceVectorsPositives);
            double maxDistancePositives = Arrays.stream(distancePerFeaturePositives).max().orElse(0);

            if (maxDistancePositives > 0) {
                for (int feature = 0; feature < GROUP_FEATURE_COUNT; feature++) {
                    double adjustmentFactorPositives = WEIGHT_ADJUSTMENT + MIN_WEIGHT - (WEIGHT_ADJUSTMENT) * (distancePerFeaturePositives[feature] / maxDistancePositives);

                    if (distancePerFeaturePositives[feature] < MIN_DISTANCE) {
                        LOGGER.debug("manipulate weight based on likes for feature {} by {} (increase)", feature, adjustmentFactorPositives);
                        manipulateWeight(weight, feature, adjustmentFactorPositives);
                    } else if (distancePerFeaturePositives[feature] > MAX_DISTANCE) {
                        if (cv[feature] < MIN_CV && positives.size() >= 10 && GroupPreference.isUpdatableFeature(feature)) {
                            LOGGER.debug("trigger update preferences due to feature {} (cv: {}; distance: {}", feature, cv[feature], distancePerFeaturePositives[feature]);
                            updateGroupPreference(studentId);
                            return getGroupWeight(studentId);
                        } else {
                            LOGGER.debug("manipulate weight based on likes for feature {} by {} (decrease)", feature, adjustmentFactorPositives);
                            manipulateWeight(weight, feature, adjustmentFactorPositives);
                        }
                    }
                }
            }
        }

        if (negatives.size() >= Math.max(1, minData / 3)) {
            double[][] normalizedNegativeStudentFeatureMatrix = groupsToNormalizedVectors(negatives, student);
            double[][] featuresStudentsDistanceVectorsNegatives = new double[FEATURE_COUNT][negatives.size()];
            double[] distancePerFeatureNegatives = getDistancePerFeature(weight.getVector(), normalizedNegativeStudentFeatureMatrix, featuresStudentsDistanceVectorsNegatives);
            double maxDistanceNegatives = Arrays.stream(distancePerFeatureNegatives).max().orElse(0);
            if (maxDistanceNegatives > 0) {
                for (int feature = 0; feature < GROUP_FEATURE_COUNT; feature++) {
                    double adjustmentFactorNegatives = WEIGHT_ADJUSTMENT * (distancePerFeatureNegatives[feature] / maxDistanceNegatives) + MIN_WEIGHT;

                    if (distancePerFeatureNegatives[feature] < MIN_DISTANCE) {
                        LOGGER.debug("manipulate weight based on dislikes for feature {} by {} (decrease)", feature, adjustmentFactorNegatives);
                        manipulateWeight(weight, feature, adjustmentFactorNegatives);
                    } else if (distancePerFeatureNegatives[feature] > MAX_DISTANCE) {
                        LOGGER.debug("manipulate weight based on dislikes for feature {} by {} (increase)", feature, adjustmentFactorNegatives);
                        manipulateWeight(weight, feature, adjustmentFactorNegatives);
                    }
                }
            }
        }

        return groupWeightRepository.save(weight);
    }

    @Override
    public Preference updatePreference(Long studentId) throws NotFoundException {
        LOGGER.trace("Update preference for with id {}", studentId);
        return updatePreference(studentId, 10, 30);
    }

    @Override
    public Preference updatePreference(Long studentId, int minData, int maxData) throws NotFoundException {
        LOGGER.trace("Update preference for student with id {} using min: {} and max: {} data", studentId, minData, maxData);

        var student = studentService.findStudentById(studentId);

        Preference preference = getPreference(student.getId());

        var likedStudents = singleMatchService.getLikedAndMatchedStudents(student.getId(), maxData);

        if (likedStudents.size() < minData) {
            LOGGER.debug("Preference update for {} failed (not enough data available)", student);
            return preference;
        }

        double[][] studentFeatureMatrix = transformToFeatureVector(likedStudents, student);
        double[] meanPerFeature = calcColMeans(studentFeatureMatrix);
        double[] sdPerFeature = calcColSd(studentFeatureMatrix, meanPerFeature);

        for (int i = 0; i < FEATURE_COUNT; i++) {
            preference.set(i, meanPerFeature[i]);
        }

        //Adjust weight based on normalized standard deviation
        LOGGER.trace("Adjusting weight for {} for new preferences {}", student, preference);
        SingleWeight weight = getWeight(studentId);

        double[] cv = getCoefficientOfVariation(meanPerFeature, sdPerFeature);
        double maxCv = Arrays.stream(cv).max().orElse(0);

        for (int i = 0; i < FEATURE_COUNT; i++) {
            double factor = 0;
            if (maxCv != 0) {
                factor = cv[i] / maxCv;
            }
            if (factor < 0) {
                if (preference.get(i) < 0.05) {
                    factor = sdPerFeature[i];
                } else {
                    weight.set(i, 0);
                    continue;
                }
            }
            weight.set(i, WEIGHT_ADJUSTMENT + MIN_WEIGHT - WEIGHT_ADJUSTMENT * factor);
        }

        weightRepository.save(weight);

        return preferenceRepository.save(preference);
    }

    @Override
    public GroupPreference updateGroupPreference(Long studentId) throws NotFoundException {
        LOGGER.trace("Update group preference for with id {}", studentId);
        return updateGroupPreference(studentId, 10, 30);
    }

    @Override
    public GroupPreference updateGroupPreference(Long studentId, int minData, int maxData) throws NotFoundException {
        LOGGER.trace("Update group preference for with id {} using min: {} and max: {} data", studentId, minData, maxData);
        var student = studentService.findStudentById(studentId);

        GroupPreference preference = getGroupPreference(student.getId());

        var likedGroups = singleMatchService.getLikedAndMatchedGroups(student.getId(), maxData);

        if (likedGroups.size() < minData) {
            LOGGER.debug("Group preference update for {} failed (not enough data available)", student);
            return preference;
        }

        double[][] groupFeatureMatrix = transformToGroupFeatureVector(likedGroups, student, false);
        double[] meanPerFeature = calcColMeans(groupFeatureMatrix);
        double[] sdPerFeature = calcColSd(groupFeatureMatrix, meanPerFeature);

        for (int i = 0; i < GROUP_FEATURE_COUNT; i++) {
            preference.set(i, meanPerFeature[i]);
        }

        //Adjust weight based on normalized standard deviation
        LOGGER.trace("Adjusting weight for {} for new preferences {}", student, preference);
        GroupWeight weight = getGroupWeight(studentId);

        double[] cv = getCoefficientOfVariation(meanPerFeature, sdPerFeature);
        double maxCv = Arrays.stream(cv).max().orElse(0);

        for (int i = 0; i < GROUP_FEATURE_COUNT; i++) {
            double factor = 0;
            if (maxCv != 0) {
                factor = cv[i] / maxCv;
            }
            if (factor < 0) {
                if (preference.get(i) < 0.05) {
                    factor = sdPerFeature[i];
                } else {
                    weight.set(i, 0);
                    continue;
                }
            }
            weight.set(i, WEIGHT_ADJUSTMENT + MIN_WEIGHT - WEIGHT_ADJUSTMENT * factor);
        }

        groupWeightRepository.save(weight);

        return groupPreferenceRepository.save(preference);
    }

    private double[] getCoefficientsOfVariationGroups(Student student, List<Group> positives) {
        double[][] groupFeatureMatrix = transformToGroupFeatureVector(positives, student, false);
        double[] meanPerFeature = calcColMeans(groupFeatureMatrix);
        double[] sdPerFeature = calcColSd(groupFeatureMatrix, meanPerFeature);

        double[] cv = getCoefficientOfVariation(meanPerFeature, sdPerFeature);
        return cv;
    }

    private double[] getCoefficientsOfVariation(Student student, List<Student> positives) {
        double[][] studentFeatureMatrix = transformToFeatureVector(positives, student);
        double[] meanPerFeature = calcColMeans(studentFeatureMatrix);
        double[] sdPerFeature = calcColSd(studentFeatureMatrix, meanPerFeature);

        double[] cv = getCoefficientOfVariation(meanPerFeature, sdPerFeature);
        return cv;
    }

    private double[] getGroupDistances(Long baseStudentId, List<Group> recommendees) {
        LOGGER.trace("Get group distance for student with id {} and recommendees {}", baseStudentId, recommendees);

        Student baseStudent = studentService.findStudentById(baseStudentId);
        double[][] normalizedGroupFeatureMatrix = groupsToNormalizedVectors(recommendees, baseStudent);

        double[] weights = getGroupWeight(baseStudentId).getVector();
        double[] distancePerGroup = new double[recommendees.size()];
        for (int i = 0; i < recommendees.size(); i++) {
            distancePerGroup[i] = calcDistance(normalizedGroupFeatureMatrix[normalizedGroupFeatureMatrix.length - 1], normalizedGroupFeatureMatrix[i], weights);
        }
        return distancePerGroup;
    }

    /**
     * Transforms list of groups to array of normalized feature vectors for each group. Last element in array is the normalized preference vector of base student
     *
     * @param recommendees groups to transform to feature vectors
     * @param baseStudent  reference student.
     * @return array of feature vectors for each group. last element is the normalized preference vector of base student
     */
    private double[][] groupsToNormalizedVectors(List<Group> recommendees, Student baseStudent) {
        LOGGER.trace("Transform groups {} to normalized vectors with reference student {}", recommendees, baseStudent);

        double[][] groupVectors = transformToGroupFeatureVector(recommendees, baseStudent, true);
        var mean = calcColMeans(groupVectors);
        var sd = calcColSd(groupVectors, mean);
        return getNormalizedFeatureVectors(groupVectors, mean, sd);
    }

    /**
     * Transforms list of groups to array of feature vectors for each group. If include base student is set, last element in array is the preference vector of base student
     *
     * @param groups      groups to transform to feature vectors
     * @param baseStudent reference student.
     * @return array of feature vectors for each group. last element is the preference vector of base student
     */
    private double[][] transformToGroupFeatureVector(List<Group> groups, Student baseStudent, boolean includeBaseStudent) {
        LOGGER.trace("Transform groups {} to feature vectors with reference student {}; include base student: {}", groups, baseStudent, includeBaseStudent);

        double[][] groupVectors = new double[includeBaseStudent ? groups.size() + 1 : groups.size()][GROUP_FEATURE_COUNT];
        for (int i = 0; i < groups.size(); i++) {
            groupVectors[i] = getFeatureVector(groups.get(i), baseStudent);
        }
        if (includeBaseStudent) {
            groupVectors[groupVectors.length - 1] = getGroupPreference(baseStudent.getId()).getVector();
        }
        return groupVectors;
    }

    private double[] getFeatureVector(Group recommendee, Student baseStudent) {
        LOGGER.trace("get feature vector for {} and base student {}", recommendee, baseStudent);

        int lvaDifference = getListMatching(recommendee.getLvas(), baseStudent.getCurrentLvas());
        int language = Objects.equals(recommendee.getPrefLanguage(), baseStudent.getPrefLanguage()) ? 1 : 0;
        int meetsIrl = Objects.equals(recommendee.getMeetsIrl(), baseStudent.getMeetsIrl()) ? 1 : 0;
        int groupMembers = recommendee.getMembers().size();
        return new double[] {lvaDifference, language, meetsIrl, groupMembers};
    }

    private double[] getFeatureVector(Student recommendee, Student baseStudent) {
        LOGGER.trace("get feature vector for {} and base student {}", recommendee, baseStudent);

        if (recommendee == baseStudent) {
            return getPreference(baseStudent.getId()).getVector();
        }

        int age = Period.between(recommendee.getDateOfBirth(), LocalDate.now()).getYears();
        int currentLvaDifference = getListMatching(recommendee.getCurrentLvas(), baseStudent.getCurrentLvas());
        int completedLvaDifference = getListMatching(recommendee.getCompletedLvas(), baseStudent.getCompletedLvas());
        int language = Objects.equals(recommendee.getPrefLanguage(), baseStudent.getPrefLanguage()) ? 1 : 0;
        int gender = Objects.equals(recommendee.getGender(), baseStudent.getGender()) ? 1 : 0;
        int meetsIrl = Objects.equals(recommendee.getMeetsIrl(), baseStudent.getMeetsIrl()) ? 1 : 0;

        return new double[] {age, currentLvaDifference, completedLvaDifference, language, gender, meetsIrl};
    }

    private double[] getDistances(Long baseStudentId, List<Student> recommendees) {
        LOGGER.trace("Get distance for student with id {} and recommendees {}", baseStudentId, recommendees);

        Student baseStudent = studentService.findStudentById(baseStudentId);
        var allStudents = new ArrayList<Student>(recommendees);
        allStudents.add(allStudents.size(), baseStudent);

        //get normalized feature vectors
        var normalizedStudentFeatureMatrix = studentsToNormalizedVectors(allStudents, baseStudent);

        //calculate distance
        double[] weights = getWeight(baseStudentId).getVector();
        double[] distancePerStudent = new double[recommendees.size()];
        for (int idxStudent = 0; idxStudent < recommendees.size(); idxStudent++) {
            distancePerStudent[idxStudent] = calcDistance(normalizedStudentFeatureMatrix[normalizedStudentFeatureMatrix.length - 1], normalizedStudentFeatureMatrix[idxStudent], weights);
        }
        return distancePerStudent;
    }

    private double[][] studentsToNormalizedVectors(List<Student> students, Student baseStudent) {
        LOGGER.trace("Transform students {} to normalized vectors with reference student {}", students, baseStudent);

        var studentFeatureVectors = transformToFeatureVector(students, baseStudent);
        var mean = calcColMeans(studentFeatureVectors);
        var sd = calcColSd(studentFeatureVectors, mean);
        return getNormalizedFeatureVectors(studentFeatureVectors, mean, sd);
    }

    private double[][] transformToFeatureVector(List<Student> students, Student baseStudent) {
        LOGGER.trace("Transform students {} to feature vectors with reference student {}; include base student: {}", students, baseStudent);

        double[][] featureVectors = new double[students.size()][FEATURE_COUNT];
        for (int i = 0; i < students.size(); i++) {
            var recommendee = students.get(i);
            featureVectors[i] = getFeatureVector(recommendee, baseStudent);
        }
        return featureVectors;
    }


    private <T> int getListMatching(List<T> list1, List<T> list2) {
        LOGGER.trace("get list matching for v1 {} and v2 {}", list1, list2);

        if (list1 == null || list2 == null) {
            return 0;
        }
        var count = 0;
        for (var item : list1) {
            if (list2.contains(item)) {
                count++;
            }
        }
        return count;
    }

    private double[] calcColMeans(double[][] vectors) {
        LOGGER.trace("calculate column means for matrix {}", vectors);

        double[] colMeans = new double[vectors[0].length];
        for (int col = 0; col < vectors[0].length; col++) {
            for (int row = 0; row < vectors.length; row++) {
                colMeans[col] += vectors[row][col];
            }
            colMeans[col] = colMeans[col] / vectors.length;
        }
        return colMeans;
    }

    private double[] calcColSd(double[][] vectors, double[] mean) {
        LOGGER.trace("calculate column standard deviation for matrix {} and means {}", vectors, mean);

        double[] sd = new double[mean.length];
        for (int feature = 0; feature < mean.length; feature++) {
            for (int student = 0; student < vectors.length; student++) {
                sd[feature] += Math.pow(mean[feature] - vectors[student][feature], 2);
            }
            sd[feature] = Math.sqrt(sd[feature] / vectors.length);
        }
        return sd;
    }

    private double[][] getNormalizedFeatureVectors(double[][] featureVectors, double[] mean, double[] sd) {
        LOGGER.trace("Normalize vectors {} with means {} and sd {}", featureVectors, mean, sd);

        double[][] normalizedFeatureVectors = new double[featureVectors.length][FEATURE_COUNT];
        for (int i = 0; i < featureVectors.length; i++) {
            normalizedFeatureVectors[i] = getNormalizedFeatureVector(featureVectors[i], mean, sd);
        }
        return normalizedFeatureVectors;
    }

    private double[] getNormalizedFeatureVector(double[] featureVector, double[] mean, double[] sd) {
        LOGGER.trace("Normalize vector {} with means {} and sd {}", featureVector, mean, sd);

        if (featureVector.length != mean.length || featureVector.length != sd.length) {
            throw new IllegalArgumentException();
        }
        double[] normalizedVector = new double[featureVector.length];
        for (int i = 0; i < featureVector.length; i++) {
            normalizedVector[i] = (featureVector[i] - mean[i]);
            if (sd[i] != 0) {
                normalizedVector[i] /= sd[i];
            }
        }
        return normalizedVector;
    }

    private double calcDistance(double[] v1, double[] v2, double[] weight) {
        LOGGER.trace("Calculate euclidean distance between v1 {} and v2 {} with weights {}", v1, v2, weight);

        double distance = 0;
        for (int idxFeature = 0; idxFeature < v1.length; idxFeature++) {
            distance += calcDistance(v1[idxFeature], v2[idxFeature], weight[idxFeature]);
        }

        return Math.sqrt(distance);
    }

    private double calcDistance(double p1, double p2, double weight) {
        LOGGER.trace("Calculate distance between p1 {} and p2 {} with weight {}", p1, p2, weight);

        return weight * Math.pow(p1 - p2, 2);
    }

    private Integer[] getSortedIndices(double[] arrayToSort) {
        LOGGER.trace("Get sorted indices for {}", arrayToSort);

        Integer[] indices = new Integer[arrayToSort.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // Sort indices array based on corresponding values in distances array
        Arrays.sort(indices, Comparator.comparingDouble(i -> arrayToSort[i]));
        return indices;
    }

    private void manipulateWeight(Weight weight, int feature, double adjustmentFactor) {
        LOGGER.trace("Manipulate weight {} at {} by {}", weight, feature, adjustmentFactor);

        weight.set(feature, weight.get(feature) * adjustmentFactor);
    }

    private double[] getDistancePerFeature(double[] weights, double[][] normalizedPositiveStudentFeatureMatrix, double[][] featuresStudentsDistanceVectorsPositives) {
        LOGGER.trace("Get euclidean distance per feature for {} with weights {}", normalizedPositiveStudentFeatureMatrix, weights);

        double[] distancePerFeaturePositives = new double[weights.length];
        for (int featureIdx = 0; featureIdx < weights.length; featureIdx++) {
            for (int studentIdx = 0; studentIdx < normalizedPositiveStudentFeatureMatrix.length - 1; studentIdx++) {
                double currentDistance =
                    calcDistance(normalizedPositiveStudentFeatureMatrix[studentIdx][featureIdx], normalizedPositiveStudentFeatureMatrix[normalizedPositiveStudentFeatureMatrix.length - 1][featureIdx], weights[featureIdx]);
                featuresStudentsDistanceVectorsPositives[featureIdx][studentIdx] = currentDistance;
                distancePerFeaturePositives[featureIdx] += currentDistance;
            }
            distancePerFeaturePositives[featureIdx] = Math.sqrt(distancePerFeaturePositives[featureIdx]);
        }
        return distancePerFeaturePositives;
    }

}
