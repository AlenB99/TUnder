package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.FilterSpecifications;
import at.ac.tuwien.sepm.groupphase.backend.repository.LanguageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SettingsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class StudentRepositoryTest implements TestData {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SingleRelationshipRepository relationshipRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LvaRepository lvaRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Test
    public void save_addsAndReturnsCorrectStudent() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at").build());
        var result = studentRepository.findStudentByEmail("e12122106@student.tuwien.ac.at");
        assertEquals(student, result);

    }

    @Test
    public void findStudentByEmail_returnsCorrectStudent() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at").build());
        studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2001, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at 0").build());
        studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2002, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at 1").build());
        var result = studentRepository.findStudentByEmail("e12122106@student.tuwien.ac.at");
        assertEquals(student, result);
    }

    @Test
    public void findStudentByEmail_withBadDataReturnsNull() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at").build());
        var result = studentRepository.findStudentByEmail("e12122106@student.tuwien.ac.at 0");
        assertNull(result);
    }

    @Test
    public void findAll_returnsAllStudents() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at").build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2001, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at 0").build());
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2002, 1, 1))
            .withEmail("e12122106@student.tuwien.ac.at 1").build());
        List<Student> studentList = new LinkedList<>();
        studentList.add(student);
        studentList.add(student2);
        studentList.add(student3);

        var result = studentRepository.findAll();
        assertEquals(studentList, result);
    }

    @Test
    public void save_withFaultyDataThrowsDataIntegrityViolationException() {
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1))
                .withEmail("e12122106@student.tuwien.ac.at").build());
            var student1 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1))
                .withEmail("e12122106@student.tuwien.ac.at").build());
        });
    }


    @Test
    public void getFiltered_returnAgeFilteredStudents() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2001, 1, 1)).build());
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2002, 1, 1)).build());
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().withMinAge(21).withMaxAge(21).build(), student.getId()));
        assertEquals(1, result.size());
        assertTrue(result.contains(student3));
    }

    @Test
    public void getFiltered_returnAllIfFilterValuesNotSet() {
        Language lang = languageRepository.save(new Language("TestLanguage","TestLanguage"));
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        studentRepository.save(
            Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2001, 1, 1)).withMeetsIrl(true).withPrefLanguage(lang).withCurrentLvas(List.of(Lva.LvaBuilder.aLvaBuilder().withId("112.12").build()))
                .build());
        studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2002, 1, 1)).build());
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertEquals(2, result.size());
    }

    @Test
    public void getFiltered_returnEmptyList() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().withMinAge(10).withMaxAge(19).build(), student.getId()));
        assertTrue(result.isEmpty());
    }

    @Test
    public void getFiltered_doNotReturnAlreadyRecommended() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2006, 1, 1)).build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2002, 1, 1)).build());
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var student4 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var resultBefore = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));

        relationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withRecommended(student).withUser(student2).withStatus(RelStatus.MATCHED).build());
        relationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship().
            withRecommended(student3).withUser(student).build());

        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertEquals(resultBefore.size() - 2, result.size());
    }

    @Test
    public void getFiltered_returnStudentsLikedStudent() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2006, 1, 1)).build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2002, 1, 1)).build());
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var student4 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var resultBefore = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));

        relationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withRecommended(student).withUser(student2).withStatus(RelStatus.LIKED).build());
        relationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship().
            withRecommended(student3).withUser(student).build());

        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertEquals(resultBefore.size() - 1, result.size());
    }

    @Test
    public void getFiltered_filterLvas() {
        var lva = lvaRepository.save(Lva.LvaBuilder.aLvaBuilder().withId("1").withName("Lva1").build());
        var lva2 = lvaRepository.save(Lva.LvaBuilder.aLvaBuilder().withId("2").withName("Lva2").build());
        List<Lva> lvaSet1 = new ArrayList<>();
        List<Lva> lvaSet2 = new ArrayList<>();
        lvaSet1.add(lva);
        lvaSet1.add(lva2);

        lvaSet2.add(lva);
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true)
            .withCurrentLvas(lvaSet1.stream().toList()).withDateOfBirth(LocalDate.of(2003, 1, 1)).build());
        var student1 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true)
            .withCurrentLvas(lvaSet2.stream().toList()).withDateOfBirth(LocalDate.of(2001, 1, 1)).build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true)
            .withDateOfBirth(LocalDate.of(2002, 1, 1)).build());

        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().withLvas(lvaSet2).build(), student2.getId()));

        assertEquals(2, result.size());
        assertTrue(result.contains(student));
    }

    @Test
    public void getFiltered_includeStudentsWithMoreLvas() {
        var lva = lvaRepository.save(Lva.LvaBuilder.aLvaBuilder().withId("1").withName("Lva1").build());
        var lva2 = lvaRepository.save(Lva.LvaBuilder.aLvaBuilder().withId("2").withName("Lva2").build());
        List<Lva> lvaSet = new ArrayList<>();
        List<Lva> lvaSetForFilter = new ArrayList<>();
        lvaSetForFilter.add(lva);
        lvaSet.add(lva);
        lvaSet.add(lva2);
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var student1 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withCurrentLvas(lvaSet.stream().toList()).build());

        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().withLvas(lvaSetForFilter).build(), student.getId()));

        assertTrue(result.contains(student1));
    }

    @Test
    public void getFiltered_filterLanguage() {
        Language language = languageRepository.save(new Language("TestLanguage", "TestLanguage"));
        Language language2 = languageRepository.save(new Language("TestLanguage2", "TestLanguage2"));

        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var germanStudent = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withPrefLanguage(language).build());
        var englishStudent = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withPrefLanguage(language2).build());
        var nullStudent = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().withPrefLanguage(language).build(), student.getId()));
        assertEquals(1, result.size());
        assertTrue(result.contains(germanStudent));
    }

    @Test
    public void getFiltered_filterMeetsIrl() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var meetStudent = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withMeetsIrl(true).build());
        var notMeetStudent = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).withMeetsIrl(false).build());
        var nullStudent = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().withMeetsIrl(true).build(), student.getId()));
        assertEquals(1, result.size());
        assertTrue(result.contains(meetStudent));
    }

    @Test
    public void getFiltered_onlyReturnEnabled() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().build());
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));
        assertEquals(1, result.size());
        assertTrue(result.contains(student3));
    }

    @Test
    public void getFiltered_excludeSleeping() {
        var student = studentRepository.save(Student.StudentBuilder.aStudent().withDateOfBirth(LocalDate.of(2000, 1, 1)).build());
        var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        var student4 = studentRepository.save(Student.StudentBuilder.aStudent().withEnabled(true).build());
        Settings settingsSleeping = new Settings();
        settingsSleeping.setStudent(student3);
        settingsSleeping.setSleeping(true);
        settingsSleeping = settingsRepository.save(settingsSleeping);
        student3.setSettings(settingsSleeping);
        student3 = studentRepository.save(student3);
        Settings settingsNotSleeping = new Settings();
        settingsNotSleeping.setStudent(student4);
        settingsNotSleeping.setSleeping(false);
        settingsNotSleeping = settingsRepository.save(settingsNotSleeping);
        student4.setSettings(settingsNotSleeping);
        student4 = studentRepository.save(student4);
        var result = studentRepository.findAll(FilterSpecifications.
            filterSpecificationsStudent(Filter.FilterBuilder.aFilter().build(), student.getId()));
        Student finalStudent4 = student4;
        assertAll(
            () -> assertEquals(2, result.size()),
            () -> assertTrue(result.contains(student2)),
            () -> assertTrue(result.contains(finalStudent4))
        );
    }

}
