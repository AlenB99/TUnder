package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupPreference;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupWeight;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Preference;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleWeight;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupPreferenceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupWeightRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PreferenceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleWeightRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.MatchServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.RankServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.StudentDetailServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.type.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RankServiceImplTest {

    @InjectMocks
    RankServiceImpl rankService;

    private SingleWeightRepository weightRepository = Mockito.mock(SingleWeightRepository.class);
    private GroupWeightRepository groupWeightRepository = Mockito.mock(GroupWeightRepository.class);

    private PreferenceRepository preferenceRepository = Mockito.mock(PreferenceRepository.class);
    private GroupPreferenceRepository groupPreferenceRepository = Mockito.mock(GroupPreferenceRepository.class);

    private MatchService matchService = Mockito.mock(MatchServiceImpl.class);
    private StudentService studentService = mock(StudentDetailServiceImpl.class);

    @Test
    public void rankStudents_ranksCorrect() {
        List<Student> students = new ArrayList<Student>();
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);

        var baseStudent = generateRandomStudent(lvas, languages);
        var otherStudent = generateRandomStudent(lvas, languages);
        var semiSimilarStudent = Student.StudentBuilder.aStudent().withId(3L)
            .withDateOfBirth(baseStudent.getDateOfBirth())
            .withGender(otherStudent.getGender())
            .withMeetsIrl(otherStudent.getMeetsIrl())
            .withCurrentLvas(otherStudent.getCurrentLvas())
            .withCompletedLvas(otherStudent.getCompletedLvas())
            .withPrefLanguage(otherStudent.getPrefLanguage())
            .build();
        baseStudent.setId(1L);
        otherStudent.setId(2L);
        students.add(baseStudent);
        students.add(otherStudent);
        students.add(semiSimilarStudent);

        given(studentService.findStudentById(any())).willReturn(baseStudent);

        var result = rankService.rankStudents(baseStudent.getId(), students);

        assertAll(
            () -> assertEquals(baseStudent, result.get(0)),
            () -> assertEquals(semiSimilarStudent, result.get(1)),
            () -> assertEquals(otherStudent, result.get(2))
        );
    }

    @Test
    public void rankStudents_emptyList_ReturnsEmptyList() {
        var baseStudent = generateRandomStudent(generateLvas(1), generateLanguages(1));
        baseStudent.setId(1L);
        given(studentService.findStudentById(any())).willReturn(baseStudent);

        var result = rankService.rankStudents(baseStudent.getId(), new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    public void rankGroups_ranksCorrect() {
        var languages = generateLanguages(1);
        var lvas = generateLvas(1);

        var baseStudent = Student.StudentBuilder.aStudent().withId(1L)
            .withMeetsIrl(true).withPrefLanguage(languages
                .get(0)).withCurrentLvas(lvas).build();

        var members = generateGroupMembers(8);
        var group1 = Group.GroupBuilder.aGroup().withId(1L)
            .withLvas(lvas).withPrefLanguage(baseStudent.getPrefLanguage())
            .withMeetsIrl(baseStudent.getMeetsIrl()).build();
        var group2 = Group.GroupBuilder.aGroup().withId(2L)
            .withLvas(lvas).withPrefLanguage(baseStudent.getPrefLanguage())
            .withMeetsIrl(baseStudent.getMeetsIrl()).build();
        var group3 = Group.GroupBuilder.aGroup().withId(3L)
            .withLvas(lvas).withPrefLanguage(baseStudent.getPrefLanguage())
            .withMeetsIrl(baseStudent.getMeetsIrl()).build();

        for (int i = 0; i < members.size(); i++) {
            if (i < 6) {
                if (i < 4) {
                    addMemberToGroup(members.get(i), group1);
                }
                addMemberToGroup(members.get(i), group3);
            }
            addMemberToGroup(members.get(i), group2);
        }

        given(studentService.findStudentById(any())).willReturn(baseStudent);

        var result = rankService.rankGroups(baseStudent.getId(), List.of(group1, group2, group3));

        assertAll(
            () -> assertEquals(group2, result.get(0)),
            () -> assertEquals(group3, result.get(1)),
            () -> assertEquals(group1, result.get(2))
        );
    }

    @Test
    public void rankStudentsDemo_sameResultAsRankStudents() {
        List<Student> students = new ArrayList<Student>();
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);

        var baseStudent = generateRandomStudent(lvas, languages);
        var otherStudent = generateRandomStudent(lvas, languages);
        var semiSimilarStudent = Student.StudentBuilder.aStudent().withId(3L)
            .withDateOfBirth(baseStudent.getDateOfBirth())
            .withGender(otherStudent.getGender())
            .withMeetsIrl(otherStudent.getMeetsIrl())
            .withCurrentLvas(otherStudent.getCurrentLvas())
            .withCompletedLvas(otherStudent.getCompletedLvas())
            .withPrefLanguage(otherStudent.getPrefLanguage())
            .build();
        baseStudent.setId(1L);
        otherStudent.setId(2L);
        students.add(baseStudent);
        students.add(otherStudent);
        students.add(semiSimilarStudent);

        given(studentService.findStudentById(any())).willReturn(baseStudent);

        var resultDemo = rankService.rankStudentsDemo(baseStudent.getId(), students);
        var result = rankService.rankStudents(baseStudent.getId(), students);

        assertAll(
            () -> assertEquals(result.get(0), resultDemo.get(0).getKey()),
            () -> assertEquals(result.get(1), resultDemo.get(1).getKey()),
            () -> assertEquals(result.get(2), resultDemo.get(2).getKey())
        );
    }

    @Test
    public void rankGroupsDemo_sameResultAsRankGroups() {
        var languages = generateLanguages(1);
        var lvas = generateLvas(1);

        var baseStudent = Student.StudentBuilder.aStudent().withId(1L)
            .withMeetsIrl(true).withPrefLanguage(languages
                .get(0)).withCurrentLvas(lvas).build();

        var members = generateGroupMembers(8);
        var group1 = Group.GroupBuilder.aGroup().withId(1L)
            .withLvas(lvas).withPrefLanguage(baseStudent.getPrefLanguage())
            .withMeetsIrl(baseStudent.getMeetsIrl()).build();
        var group2 = Group.GroupBuilder.aGroup().withId(2L)
            .withLvas(lvas).withPrefLanguage(baseStudent.getPrefLanguage())
            .withMeetsIrl(baseStudent.getMeetsIrl()).build();
        var group3 = Group.GroupBuilder.aGroup().withId(3L)
            .withLvas(lvas).withPrefLanguage(baseStudent.getPrefLanguage())
            .withMeetsIrl(baseStudent.getMeetsIrl()).build();

        for (int i = 0; i < members.size(); i++) {
            if (i < 6) {
                if (i < 4) {
                    addMemberToGroup(members.get(i), group1);
                }
                addMemberToGroup(members.get(i), group3);
            }
            addMemberToGroup(members.get(i), group2);
        }

        given(studentService.findStudentById(any())).willReturn(baseStudent);

        var result = rankService.rankGroups(baseStudent.getId(), List.of(group1, group2, group3));
        var resultDemo = rankService.rankGroupsDemo(baseStudent.getId(), List.of(group1, group2, group3));

        assertAll(
            () -> assertEquals(result.get(0), resultDemo.get(0).getKey()),
            () -> assertEquals(result.get(1), resultDemo.get(1).getKey()),
            () -> assertEquals(result.get(2), resultDemo.get(2).getKey())
        );
    }

    @Test
    public void getWeight_noWeightStored_returnsDefaultWeight() {
        var weight = rankService.getWeight(1L);
        assertEquals(6, Arrays.stream(weight.getVector()).sum());
    }

    @Test
    public void getWeight_returnStoredWeight() {
        Student student = generateRandomStudent(generateLvas(5), generateLanguages(5));
        student.setId(1L);
        SingleWeight weight = new SingleWeight(student, 5);
        given(weightRepository.findByStudent(student)).willReturn(Optional.of(weight));
        given(studentService.findStudentById(student.getId())).willReturn(student);

        var result = rankService.getWeight(student.getId());

        assertEquals(weight, result);
    }

    @Test
    public void getGroupWeight_noWeightStored_returnsDefaultWeight() {
        var weight = rankService.getGroupWeight(1L);
        assertEquals(4, Arrays.stream(weight.getVector()).sum());
    }

    @Test
    public void getGroupWeight_returnStoredWeight() {
        Student student = generateRandomStudent(generateLvas(5), generateLanguages(5));
        student.setId(1L);
        GroupWeight groupWeight = new GroupWeight(student, 5);
        given(groupWeightRepository.findByStudent(student)).willReturn(Optional.of(groupWeight));
        given(studentService.findStudentById(student.getId())).willReturn(student);

        var result = rankService.getGroupWeight(student.getId());

        assertEquals(groupWeight, result);
    }

    @Test
    public void getPreference_noPreferenceStored_returnsDefaultPreference() {
        Student student = generateRandomStudent(generateLvas(5), generateLanguages(5));
        student.setId(1L);
        given(studentService.findStudentById(student.getId())).willReturn(student);

        var pref = rankService.getPreference(1L);

        assertNotNull(pref);
    }

    @Test
    public void getPreference_returnStoredPreference() {
        Student student = generateRandomStudent(generateLvas(5), generateLanguages(5));
        student.setId(1L);
        Preference pref = new Preference(student, 5);
        given(preferenceRepository.findByStudent(student)).willReturn(Optional.of(pref));
        given(studentService.findStudentById(student.getId())).willReturn(student);

        var result = rankService.getPreference(student.getId());

        assertEquals(pref, result);
    }

    @Test
    public void getGroupPreference_noPreferenceStored_returnsDefaultPreference() {
        Student student = generateRandomStudent(generateLvas(5), generateLanguages(5));
        student.setId(1L);
        given(studentService.findStudentById(student.getId())).willReturn(student);

        var groupPref = rankService.getGroupPreference(1L);
        assertNotNull(groupPref);
    }

    @Test
    public void getGroupPreference_returnStoredPreference() {
        Student student = generateRandomStudent(generateLvas(5), generateLanguages(5));
        student.setId(1L);
        GroupPreference groupPreference = new GroupPreference(student, 5);
        given(groupPreferenceRepository.findByStudent(student)).willReturn(Optional.of(groupPreference));
        given(studentService.findStudentById(student.getId())).willReturn(student);

        var result = rankService.getGroupPreference(student.getId());

        assertEquals(groupPreference, result);
    }

    @Test
    public void getDistancesForDemo_distanceEqualsRankDemo() {
        Student base = Student.StudentBuilder.aStudent().withId(0L).withMeetsIrl(false).withDateOfBirth(LocalDate.now().minusYears(20)).build();
        Student recommendee = Student.StudentBuilder.aStudent().withId(0L).withMeetsIrl(true).withDateOfBirth(LocalDate.now().minusYears(22)).build();

        given(studentService.findStudentById(any())).willReturn(base);

        var distanceResult = rankService.getDistancesForDemo(base.getId(), List.of(recommendee));
        var rankResult = rankService.rankStudentsDemo(base.getId(), List.of(recommendee));

        assertEquals(1, distanceResult.size());
        assertEquals(rankResult.get(0).getValue(), distanceResult.get(rankResult.get(0).getKey())[6]);
    }

    @Test
    public void getGroupDistancesForDemo_distanceEqualsRankDemo() {
        Student base = Student.StudentBuilder.aStudent().withId(0L).withMeetsIrl(false).withDateOfBirth(LocalDate.now().minusYears(20)).build();
        Group recommendee = generateRandomGroup(generateLvas(1), generateLanguages(1), generateGroupMembers(1));

        given(studentService.findStudentById(any())).willReturn(base);

        var distanceResult = rankService.getGroupDistancesForDemo(base.getId(), List.of(recommendee));
        var rankResult = rankService.rankGroupsDemo(base.getId(), List.of(recommendee));

        assertEquals(1, distanceResult.size());
        assertEquals(rankResult.get(0).getValue(), distanceResult.get(rankResult.get(0).getKey())[4]);
    }

    @Test
    public void updateWeight_notEnoughData_returnOldWeight() {
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);
        Student baseStudent = generateRandomStudent(lvas, languages);
        baseStudent.setId(1L);
        List<Student> likes = new ArrayList<>();
        List<Student> dislikes = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            var temp = generateRandomStudent(lvas, languages);
            temp.setId((long) (i * 2));
            dislikes.add(temp);
            likes.add(Student.StudentBuilder.aStudent().withId((long) ((i * 2) + 1))
                .withDateOfBirth(baseStudent.getDateOfBirth())
                .withCompletedLvas(temp.getCompletedLvas())
                .withCurrentLvas(temp.getCurrentLvas())
                .withPrefLanguage(temp.getPrefLanguage())
                .withGender(temp.getGender())
                .withMeetsIrl(temp.getMeetsIrl())
                .build());
        }

        SingleWeight weight = new SingleWeight();

        given(studentService.findStudentById(any())).willReturn(baseStudent);
        given(matchService.getLikedAndMatchedStudents(any(), eq(10))).willReturn(likes);
        given(matchService.getLikedAndMatchedStudents(any(), eq(30))).willReturn(likes);
        given(matchService.getLikedAndMatchedStudents(any())).willReturn(likes);
        given(matchService.getDislikedStudents(any(), eq(10))).willReturn(dislikes);
        given(weightRepository.findByStudent(baseStudent)).willReturn(Optional.of(weight));

        var result = this.rankService.updateWeights(baseStudent.getId());

        assertEquals(weight, result);
    }

    @Test
    public void updateWeight_allLikesSameAge_setAgeWeightHighest() {
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);
        Student baseStudent = generateRandomStudent(lvas, languages);
        baseStudent.setId(1L);
        List<Student> likes = new ArrayList<>();
        List<Student> dislikes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            var temp = generateRandomStudent(lvas, languages);
            temp.setId((long) (i * 2));
            dislikes.add(temp);
            likes.add(Student.StudentBuilder.aStudent().withId((long) ((i * 2) + 1))
                .withDateOfBirth(baseStudent.getDateOfBirth())
                .withCompletedLvas(temp.getCompletedLvas())
                .withCurrentLvas(temp.getCurrentLvas())
                .withPrefLanguage(temp.getPrefLanguage())
                .withGender(temp.getGender())
                .withMeetsIrl(temp.getMeetsIrl())
                .build());
        }

        given(studentService.findStudentById(any())).willReturn(baseStudent);
        given(matchService.getLikedAndMatchedStudents(any(), eq(10))).willReturn(likes);
        given(matchService.getLikedAndMatchedStudents(any(), eq(30))).willReturn(likes);
        given(matchService.getLikedAndMatchedStudents(any())).willReturn(likes);
        given(matchService.getDislikedStudents(any(), eq(10))).willReturn(dislikes);
        given(weightRepository.save(any(SingleWeight.class))).willAnswer((r) -> r.getArgument(0));

        var result = this.rankService.updateWeights(baseStudent.getId());

        assertEquals(Arrays.stream(result.getVector()).max().orElse(0), result.getAge());
    }

    @Test
    public void updateWeight_highAgeDifferenceWithLowDeviation_triggerUpdatePreference() {
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);
        Student baseStudent = generateRandomStudent(lvas, languages);
        baseStudent.setId(1L);
        List<Student> likes = new ArrayList<>();
        List<Student> dislikes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            var temp = generateRandomStudent(lvas, languages);
            temp.setId((long) (i * 2));
            dislikes.add(temp);
            likes.add(Student.StudentBuilder.aStudent().withId((long) ((i * 2) + 1))
                .withDateOfBirth(baseStudent.getDateOfBirth().plusYears(7))
                .withCompletedLvas(temp.getCompletedLvas())
                .withCurrentLvas(temp.getCurrentLvas())
                .withPrefLanguage(temp.getPrefLanguage())
                .withGender(temp.getGender())
                .withMeetsIrl(temp.getMeetsIrl())
                .build());
        }

        given(studentService.findStudentById(any())).willReturn(baseStudent);
        given(matchService.getLikedAndMatchedStudents(any(), eq(10))).willReturn(likes);
        given(matchService.getLikedAndMatchedStudents(any(), eq(30))).willReturn(likes);
        given(matchService.getLikedAndMatchedStudents(any())).willReturn(likes);
        given(matchService.getDislikedStudents(any(), eq(10))).willReturn(dislikes);

        this.rankService.updateWeights(baseStudent.getId());

        verify(preferenceRepository, times(1)).save(any(Preference.class));
    }

    @Test
    public void updateGroupWeight_allLikesSameGroupSize_setGroupSizeWeightHighest() {
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);
        Student baseStudent = generateRandomStudent(lvas, languages);
        baseStudent.setId(1L);
        List<Group> likes = new ArrayList<>();
        List<Group> dislikes = new ArrayList<>();
        List<GroupMember> groupMembers = generateGroupMembers(8);
        for (int i = 1; i <= 10; i++) {
            var tempDisliked = generateRandomGroup(lvas, languages, groupMembers);
            tempDisliked.setId((long) (i * 2));
            dislikes.add(tempDisliked);
            var tempLiked = generateRandomGroupWithFixedMembers(lvas, languages, groupMembers);
            tempLiked.setId((long) ((i * 2) + 1));
            likes.add(tempLiked);
        }

        given(studentService.findStudentById(any())).willReturn(baseStudent);
        given(matchService.getLikedAndMatchedGroups(any(), eq(10))).willReturn(likes);
        given(matchService.getLikedAndMatchedGroups(any(), eq(30))).willReturn(likes);
        given(matchService.getLikedAndMatchedGroups(any())).willReturn(likes);
        given(matchService.getDislikedGroups(any(), eq(10))).willReturn(dislikes);
        given(groupWeightRepository.save(any(GroupWeight.class))).willAnswer((r) -> r.getArgument(0));

        var result = this.rankService.updateGroupWeights(baseStudent.getId());

        assertEquals(Arrays.stream(result.getVector()).max().orElse(0), result.getGroupSize());
    }

    @Test
    public void updateGroupWeight_allLikesSameGroupSizeWithBigDifference_callUpdatePreference() {
        var lvas = generateLvas(10);
        var languages = generateLanguages(10);
        Student baseStudent = generateRandomStudent(lvas, languages);
        baseStudent.setId(1L);
        List<Group> likes = new ArrayList<>();
        List<Group> dislikes = new ArrayList<>();
        List<GroupMember> groupMembers = generateGroupMembers(3);
        for (int i = 1; i <= 10; i++) {
            var tempDisliked = generateRandomGroup(lvas, languages, groupMembers);
            tempDisliked.setId((long) (i * 2));
            dislikes.add(tempDisliked);
            var tempLiked = generateRandomGroupWithFixedMembers(lvas, languages, groupMembers);
            tempLiked.setId((long) ((i * 2) + 1));
            likes.add(tempLiked);
        }

        given(studentService.findStudentById(any())).willReturn(baseStudent);
        given(matchService.getLikedAndMatchedGroups(any(), eq(10))).willReturn(likes);
        given(matchService.getLikedAndMatchedGroups(any(), eq(30))).willReturn(likes);
        given(matchService.getLikedAndMatchedGroups(any())).willReturn(likes);
        given(matchService.getDislikedGroups(any(), eq(10))).willReturn(dislikes);
        given(groupWeightRepository.save(any(GroupWeight.class))).willAnswer((r) -> r.getArgument(0));

        this.rankService.updateGroupWeights(baseStudent.getId());

        verify(groupPreferenceRepository, times(1)).save(any(GroupPreference.class));

    }

    private Group generateRandomGroupWithFixedMembers(List<Lva> lvas, List<Language> languages, List<GroupMember> groupMembers) {
        Random random = new Random();
        Group group = new Group();
        int lvaMax = random.nextInt(2, lvas.size());
        int lvaMin = random.nextInt(lvaMax - 1);
        group.setLvas(lvas.subList(lvaMin, lvaMax));
        group.setMeetsIrl(random.nextBoolean());
        group.setPrefLanguage(languages.get(random.nextInt(languages.size())));
        for (int i = 0; i < groupMembers.size(); i++) {
            addMemberToGroup(groupMembers.get(i), group);
        }
        return group;
    }

    private List<GroupMember> generateGroupMembers(int count) {
        var members = new ArrayList<GroupMember>();
        for (int i = 0; i < count; i++) {
            members.add(GroupMember.GroupMemberBuilder.aGroupMember().withStudent(generateRandomStudent(null, null)).build());
        }
        return members;
    }

    private List<Student> generateStudents(int count) {
        List<Student> students = new ArrayList<Student>();
        List<Lva> lvas = List.of(Lva.LvaBuilder.aLvaBuilder().withId("1L").build(), Lva.LvaBuilder.aLvaBuilder().withId("2L").build(), Lva.LvaBuilder.aLvaBuilder().withId("3L").build());
        for (int i = 0; i < count; i++) {
            var start = Math.min(i % 3, (int) (Math.pow(i, 2) % 2));
            var end = Math.max(i % 3, (int) (Math.pow(i, 2) % 2));
            students.add(Student.StudentBuilder.aStudent().withId((long) i + 1).withCurrentLvas(lvas.subList(start, end)).withDateOfBirth(LocalDate.now().minusYears(i % 10 + 17)).build());
        }
        return students;
    }

    private Student generateRandomStudent(List<Lva> lvas, List<Language> languages) {
        Random random = new Random();
        Student student = new Student();
        student.setDateOfBirth(LocalDate.now().minusYears(random.nextInt(16, 40)));
        if (lvas != null && lvas.size() > 0) {
            int lvaMin = random.nextInt(lvas.size());
            int lvaMax = random.nextInt(lvaMin, lvas.size());
            student.setCompletedLvas(lvas.subList(lvaMin, lvaMax));
        }
        Gender gender;
        switch (random.nextInt(3)) {
            case 0:
                gender = Gender.MALE;
                break;
            case 1:
                gender = Gender.FEMALE;
                break;
            default:
                gender = Gender.OTHER;
                break;
        }
        student.setGender(gender);
        student.setMeetsIrl(random.nextBoolean());
        if (languages != null) {
            student.setPrefLanguage(languages.get(random.nextInt(languages.size())));
        }
        return student;
    }

    private Group generateRandomGroup(List<Lva> lvas, List<Language> languages, List<GroupMember> groupMembers) {
        Random random = new Random();
        Group group = new Group();
        if (lvas != null && lvas.size() > 0) {
            int lvaMin = random.nextInt(lvas.size());
            int lvaMax = random.nextInt(lvaMin, lvas.size());
            group.setLvas(lvas.subList(lvaMin, lvaMax));
        }
        group.setMeetsIrl(random.nextBoolean());
        if (languages != null) {
            group.setPrefLanguage(languages.get(random.nextInt(languages.size())));
        }
        for (int i = 0; i < random.nextInt(groupMembers.size()); i++) {
            addMemberToGroup(groupMembers.get(i), group);
        }
        return group;
    }

    private Group addMemberToGroup(GroupMember member, Group group) {
        member.setStudyGroup(group);
        var members = group.getMembers();
        members.add(member);
        group.setMembers(members);
        return group;
    }

    private List<Lva> generateLvas(int count) {
        List<Lva> lvas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lvas.add(Lva.LvaBuilder.aLvaBuilder().withId("Lva" + i).withName("TestLva" + i).build());
        }
        return lvas;
    }

    private List<Language> generateLanguages(int count) {
        List<Language> languages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Language lang = new Language();
            lang.setId("lang" + i);
            lang.setName("testLanguage" + i);
            languages.add(lang);
        }
        return languages;
    }
}
