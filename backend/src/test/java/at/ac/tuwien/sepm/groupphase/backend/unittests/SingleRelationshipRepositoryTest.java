package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class SingleRelationshipRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

     @Autowired
     private SingleRelationshipRepository singleRelationshipRepository;

     @Test
     public void save_addsAndReturnsCorrectRelationship() {
         var student1 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test1@email.com").build());
         var student2 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test2@email.com").build());
         SingleRelationship relationship = singleRelationshipRepository.save(
             new SingleRelationship(
                 student1,
                 student2,
                 RelStatus.MATCHED));
         SingleRelationship queried = singleRelationshipRepository.getSingleRelationshipById(relationship.getId());
         assertEquals(relationship, queried);
     }


    @Test
    public void findRelationshipByIdsAndStatus_shouldReturnRelationship() {
        var student3 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test3@email.com").build());
        var student4 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test4@email.com").build());
        SingleRelationship relationship = singleRelationshipRepository.save(
            new SingleRelationship(student3, student4, RelStatus.MATCHED));

        var find = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, student3, student4);
        assertEquals(relationship, find);
    }

    @Test
    public void findRelationshipByIdsAndStatus_shouldReturnRelationship2() {
        var student5 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test5@email.com").build());
        var student6 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test6@email.com").build());
        SingleRelationship relationship = singleRelationshipRepository.save(
            new SingleRelationship(student5, student6, RelStatus.MATCHED));

        var find = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, student6, student5);
        assertEquals(relationship, find);
    }


    @Test
    public void findRelationshipByIdsAndStatus_shouldReturnNull() {
        var student7 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test7@email.com").build());
        var student8 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test8@email.com").build());
        var find = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, student7, student8);
        assertNull(find);
    }

    @Test
    public void findRelationshipByIdsAndStatus_withSameStudent_shouldReturnNull() {
        var student9 = studentRepository.save(Student.StudentBuilder.aStudent().withEmail("test9@email.com").build());
        var find = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, student9, student9);
        assertNull(find);
    }

    @Test
    public void findRelationshipByIdsAndStatus_withNonexistentStudent_shouldReturnNull() {
        var student10 = Student.StudentBuilder.aStudent().withId(10L).build();
        var find = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, student10, student10);
        assertNull(find);
    }

    @Test
    public void existsByUserAndRecommended_whenRelationshipExists_returnsTrue() {
        var user = studentRepository.save(Student.StudentBuilder.aStudent()
            .withDateOfBirth(LocalDate.of(2000, 1, 1)).withEmail("user@student.tuwien.ac.at").build());
        var recommended = studentRepository.save(Student.StudentBuilder.aStudent()
            .withDateOfBirth(LocalDate.of(2001, 1, 1)).withEmail("recommended@student.tuwien.ac.at").build());

        singleRelationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withRecommended(recommended).withUser(user).build());

        var result = singleRelationshipRepository.existsByUserAndRecommended(user, recommended);
        assertTrue(result);
    }

    @Test
    public void existsByUserAndRecommended_whenNoRelationshipExists_returnsFalse() {
        var user = studentRepository.save(Student.StudentBuilder.aStudent()
            .withDateOfBirth(LocalDate.of(2000, 1, 1)).withEmail("user@student.tuwien.ac.at").build());
        var recommended = studentRepository.save(Student.StudentBuilder.aStudent()
            .withDateOfBirth(LocalDate.of(2001, 1, 1)).withEmail("recommended@student.tuwien.ac.at").build());

        var result = singleRelationshipRepository.existsByUserAndRecommended(user, recommended);
        assertFalse(result);
    }

    @Test
    public void save_whenValidSingleRelationship_returnsSavedRelationship() {
        var user = studentRepository.save(Student.StudentBuilder.aStudent()
            .withDateOfBirth(LocalDate.of(2000, 1, 1)).withEmail("user@student.tuwien.ac.at").build());
        var recommended = studentRepository.save(Student.StudentBuilder.aStudent()
            .withDateOfBirth(LocalDate.of(2001, 1, 1)).withEmail("recommended@student.tuwien.ac.at").build());

        var relationship = SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withRecommended(recommended).withUser(user).build();

        var result = singleRelationshipRepository.save(relationship);

        assertNotNull(result.getId());
        assertEquals(user, result.getUser());
        assertEquals(recommended, result.getRecommended());
    }

    @Test
    public void save_whenSavingNull_throwsIllegalArgumentException() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> singleRelationshipRepository.save(null));
    }
}
