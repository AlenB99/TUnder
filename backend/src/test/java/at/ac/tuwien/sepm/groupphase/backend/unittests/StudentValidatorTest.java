package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentValidatorTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private jakarta.validation.Validator beanValidator;

    @InjectMocks
    private StudentValidator studentValidator;

    private Student student;

    @BeforeEach
    public void setUp() {
        student = new Student();
        student.setId(1L);
        student.setEmail("e12122106@student.tuwien.ac.at");
        student.setFirstName("Test");
        student.setLastName("User");
        student.setPassword("testPassword");
        student.setDateOfBirth(LocalDate.now().minusYears(20));

        student = new Student();
        student.setId(2L);
        student.setEmail("e12122106@student.tuwien.ac.at");
        student.setFirstName("Test");
        student.setLastName("User");
        student.setPassword("testPassword");
        student.setDateOfBirth(LocalDate.now().minusYears(20));
        // TODO: set the rest of the properties of the student object
    }

    @Test
    void validate_success() throws ValidationException {
        when(beanValidator.validate(any())).thenReturn(new HashSet<ConstraintViolation<Object>>());
        when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());

        studentValidator.validate(student);

        verify(studentRepository, times(1)).findByEmail(any());
        verify(beanValidator, times(1)).validate(any());
    }

    @Test
    void validate_invalidEmail_failure() {
        student.setEmail("invalidEmail");

        assertThrows(ValidationException.class, () -> studentValidator.validate(student));
    }

    @Test
    void validate_emailAlreadyExists_failure() {
        Student existingStudent = new Student();
        existingStudent.setId(1L);
        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(existingStudent));

        assertThrows(ValidationException.class, () -> studentValidator.validate(student));
    }

    @Test
    void validate_passwordTooShort_failure() {
        student.setPassword("test");

        assertThrows(ValidationException.class, () -> studentValidator.validate(student));
    }

    @Test
    void validate_firstNameEmpty_failure() {
        student.setFirstName("");

        assertThrows(ValidationException.class, () -> studentValidator.validate(student));
    }

    @Test
    void validate_lastNameEmpty_failure() {
        student.setLastName("");

        assertThrows(ValidationException.class, () -> studentValidator.validate(student));
    }

    @Test
    void validate_dateOfBirthInTheFuture_failure() {
        student.setDateOfBirth(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> studentValidator.validate(student));
    }

}

