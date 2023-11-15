package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.SingleRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.Gender;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SingleRelationshipValidatorTest {

    @Mock
    private SingleRelationshipRepository singleRelationshipRepository;

    @Mock
    private Validator beanValidator;
    @Mock
    private StudentValidator studentValidator;
    @InjectMocks
    private SingleRelationshipValidator singleRelationshipValidator;

    private SingleRelationship singleRelationship;
    private Student student;
    private Student student1;

    @BeforeEach
    public void setUp() {
        singleRelationship = new SingleRelationship();
        student = new Student();
        student.setId(1L);
        student.setEmail("e12122106@tuwien.ac.at");
        student.setPassword("password");
        student.setAdmin(false);
        student.setDateOfBirth(LocalDate.now().minusDays(1));
        student.setEnabled(true);
        student.setGender(Gender.OTHER);
        student.setFirstName("erstername");
        student.setLastName("letzername");
        student.setMeetsIrl(true);

        student1 = new Student();
        student1.setId(2L);
        student1.setEmail("e12122106@tuwien.ac.at");
        student1.setPassword("password");
        student1.setAdmin(false);
        student1.setDateOfBirth(LocalDate.now().minusDays(1));
        student1.setEnabled(true);
        student1.setGender(Gender.OTHER);
        student1.setFirstName("erstername");
        student1.setLastName("letzername");
        student1.setMeetsIrl(true);

        singleRelationship.setUser(student);
        singleRelationship.setRecommended(student1);
        singleRelationship.setStatus(RelStatus.LIKED);
    }

    @Test
    void validate_success() throws ValidationException {
        when(singleRelationshipRepository.existsByUserAndRecommended(any(), any())).thenReturn(false);

        singleRelationshipValidator.validateRelationshipForCreate(singleRelationship);

        verify(singleRelationshipRepository, times(1)).existsByUserAndRecommended(any(), any());
        singleRelationshipValidator.validateRelationshipForCreate(singleRelationship);
    }

    @Test
    void validate_sameUserAndRecommended_failure() {
        singleRelationship.setUser(student);
        singleRelationship.setRecommended(student);

        assertThrows(ValidationException.class, () -> singleRelationshipValidator.validateRelationshipForCreate(singleRelationship));
    }

    @Test
    void validate_userIsNull_failure() {
        singleRelationship.setUser(null);
        singleRelationship.setRecommended(student);

        assertThrows(ValidationException.class, () -> singleRelationshipValidator.validateRelationshipForCreate(singleRelationship));
    }

    @Test
    void validate_recommendedIsNull_failure() {
        singleRelationship.setUser(student);
        singleRelationship.setRecommended(null);

        assertThrows(ValidationException.class, () -> singleRelationshipValidator.validateRelationshipForCreate(singleRelationship));
    }

    @Test
    void validate_relationshipExists_failure() {
        when(singleRelationshipRepository.existsByUserAndRecommended(any(), any())).thenReturn(true);

        assertThrows(ValidationException.class, () -> singleRelationshipValidator.validateRelationshipForCreate(singleRelationship));
    }
}

