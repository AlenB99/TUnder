package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupPreference;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupWeight;
import at.ac.tuwien.sepm.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleWeight;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface RankService {
    /**
     * Gets a list of students and ranks them based on personalized preferences and weights.
     *
     * @param baseStudentId id of student to rank recommendations for.
     * @param recommendees  list of students for recommendation.
     * @return ordered list of students for recommendation where the first fits best.
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore.
     */
    List<Student> rankStudents(Long baseStudentId, List<Student> recommendees) throws NotFoundException;

    /**
     * Gets a list of groups and ranks them based on personalized preferences and weights.
     *
     * @param baseStudentId id of student to rank recommendations for.
     * @param recommendees  list of groups for recommendation.
     * @return ordered list of groups for recommendation where the first fits best.
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    List<Group> rankGroups(Long baseStudentId, List<Group> recommendees) throws NotFoundException;

    /**
     * Gets a list of students and ranks them based on personalized preferences and weights. Includes distances for demo mode.
     *
     * @param baseStudentId id of student to rank recommendations for.
     * @param recommendees  list of students for recommendation.
     * @return ordered list of students for recommendation and the distance to base student.
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore.
     */
    List<Pair<Student, Double>> rankStudentsDemo(Long baseStudentId, List<Student> recommendees) throws NotFoundException;

    /**
     * Get distance vectors for all users given in recommendees.
     *
     * @param baseStudentId id of student to get distances for.
     * @param recommendees  list of students to get distances for.
     * @return A 2D array where each inner array represents a student and contains their distance values. The outer array represents all recommendees.
     * @throws NotFoundException if the base student is not found.
     */
    Map<Student, double[]> getDistancesForDemo(Long baseStudentId, List<Student> recommendees) throws NotFoundException;

    /**
     * Gets a list of groups and ranks them based on personalized preferences and weights. Includes distances for demo mode.
     *
     * @param baseStudentId id of student to rank recommendations for.
     * @param recommendees  list of groups for recommendation.
     * @return ordered list of groups for recommendation and the distance to base student, where the first fits best.
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    List<Pair<Group, Double>> rankGroupsDemo(Long baseStudentId, List<Group> recommendees);

    /**
     * Get distance vectors for all users in the given groups.
     *
     * @param baseStudentId ID of the base student to get distances for.
     * @param recommendees  List of groups to get distances for.
     * @return A 2D array where each inner array represents a group and contains the distance values for its members. The outer array represents all the groups in the recommendees list.
     * @throws NotFoundException if the base student is not found.
     */
    Map<Group, double[]> getGroupDistancesForDemo(Long baseStudentId, List<Group> recommendees) throws NotFoundException;

    /**
     * Get current weight for student.
     *
     * @param studentId id of student to get weight for
     * @return weight of student (if not set, the base weight will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    SingleWeight getWeight(Long studentId) throws NotFoundException;

    /**
     * Get current group weight for student.
     *
     * @param studentId id of student to get group weight for
     * @return group weight of student (if not set, the base group weight will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    GroupWeight getGroupWeight(Long studentId) throws NotFoundException;

    /**
     * Get current preferences for student.
     *
     * @param studentId id of student to get preferences for
     * @return preference of student (if not set, the base preference will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    Preference getPreference(Long studentId) throws NotFoundException;

    /**
     * Get current group preferences for student.
     *
     * @param studentId id of student to get group preferences for
     * @return group preference of student (if not set, the base group preference will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    GroupPreference getGroupPreference(Long studentId) throws NotFoundException;

    /**
     * update weights of student based on swipe behaviour.
     *
     * @param studentId id of student to update weights for
     * @return updated weights (if update failed, current weights will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    SingleWeight updateWeights(Long studentId) throws NotFoundException;

    /**
     * update weights of student based on swipe behaviour.
     *
     * @param studentId id of student to update weights for
     * @param minData   minimum count of likes and dislikes (combined)
     * @param maxData   maximum count of likes and dislikes that will be used for calculation
     * @return updated weights (if update failed, current weights will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    SingleWeight updateWeights(Long studentId, int minData, int maxData) throws NotFoundException;

    /**
     * update group weights of student based on swipe behaviour.
     *
     * @param studentId id of student to update group weights for
     * @return updated group weights (if update failed, current group weights will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    GroupWeight updateGroupWeights(Long studentId) throws NotFoundException;

    /**
     * update group weights of student based on swipe behaviour.
     *
     * @param studentId id of student to update group weights for
     * @param minData   minimum count of likes and dislikes (combined)
     * @param maxData   maximum count of likes and dislikes that will be used for calculation
     * @return updated group weights (if update failed, current group weights will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    GroupWeight updateGroupWeights(Long studentId, int minData, int maxData) throws NotFoundException;

    /**
     * update preferences of student based on swipe behaviour, if enough data is available. Otherwise, current preferences are retained.
     *
     * @param studentId id of student to update preferences for
     * @return updated preferences (if update failed, current preferences will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    Preference updatePreference(Long studentId) throws NotFoundException;

    /**
     * update preferences of student based on swipe behaviour, if enough data is available. Otherwise, current preferences are retained.
     *
     * @param studentId id of student to update preferences for
     * @param minData   minimum count of likes
     * @param maxData   maximum count of likes that will be used for calculation (last maxData amount will be used)
     * @return updated preferences (if update failed, current preferences will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    Preference updatePreference(Long studentId, int minData, int maxData) throws NotFoundException;

    /**
     * update group preferences of student based on swipe behaviour, if enough data is available. Otherwise, current group preferences are retained.
     *
     * @param studentId id of student to update group preferences for
     * @return updated group preferences (if update failed, current group preferences will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    GroupPreference updateGroupPreference(Long studentId) throws NotFoundException;

    /**
     * update group preferences of student based on swipe behaviour, if enough data is available. Otherwise, current group preferences are retained.
     *
     * @param studentId id of student to update group preferences for
     * @param minData   minimum count of likes
     * @param maxData   maximum count of likes that will be used for calculation (last maxData amount will be used)
     * @return updated group preferences (if update failed, current group preferences will be returned)
     * @throws NotFoundException if student with {@code baseStudentId} is not found in datastore
     */
    GroupPreference updateGroupPreference(Long studentId, int minData, int maxData) throws NotFoundException;

}
