package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangeAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.entity.StudentConfirmation;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenExpiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentConfirmationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.AccountManagementService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.EmailSenderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class AccountManagementServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailSenderServiceImpl emailSenderService;

    @Mock
    private StudentConfirmationRepository studentConfirmationRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @InjectMocks
    private AccountManagementService accountManagementService;

    private Student testStudent;
    private String testToken;

    @BeforeEach
    public void setup() {
        testStudent = new Student();
        testStudent.setEmail("test@test.com");
        testStudent.setPassword("testPassword");
        testToken = UUID.randomUUID().toString();
    }

    @Test
    public void signUpUser_validRegistration_savesStudent() throws ValidationException {
        RegistrationDto registrationDto = new RegistrationDto("e12022507@student.tuwien.ac.at","Password!", "Password!", "This", "Guy", LocalDate.now().minusDays(1));

        accountManagementService.signUpUser(registrationDto);

        verify(studentRepository, times(1)).save(any(Student.class));
        verify(emailSenderService, times(1)).sendEmail(any(SimpleMailMessage.class));
    }

    @Test
    public void confirmStudent_validToken_enablesStudent() {
        StudentConfirmation studentConfirmation = new StudentConfirmation();
        studentConfirmation.setConfirmationToken(testToken);
        studentConfirmation.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        studentConfirmation.setStudent(testStudent);
        when(studentConfirmationRepository.findByConfirmationToken(testToken)).thenReturn(studentConfirmation);

        accountManagementService.confirmStudent(testToken);

        assertTrue(testStudent.isEnabled());
        verify(studentConfirmationRepository, times(1)).delete(studentConfirmation);
    }

    @Test
    public void confirmStudent_expiredToken_throwsTokenExpiredException() {
        StudentConfirmation studentConfirmation = new StudentConfirmation();
        studentConfirmation.setConfirmationToken(testToken);
        studentConfirmation.setExpirationTime(LocalDateTime.now().minusMinutes(1));
        studentConfirmation.setStudent(testStudent);
        when(studentConfirmationRepository.findByConfirmationToken(testToken)).thenReturn(studentConfirmation);

        assertThrows(TokenExpiredException.class, () -> accountManagementService.confirmStudent(testToken));
    }

    @Test
    public void resetPassword_validToken_resetsPassword() {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setConfirmationToken(testToken);
        passwordReset.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        passwordReset.setStudent(testStudent);
        when(passwordResetRepository.findByConfirmationToken(testToken)).thenReturn(passwordReset);
        ResetPasswordDto resetPasswordDto =  new ResetPasswordDto(testToken,"Password!");
        accountManagementService.resetPassword(resetPasswordDto);

        verify(passwordEncoder, times(1)).encode("Password!");
        verify(studentRepository, times(1)).save(testStudent);
        verify(passwordResetRepository, times(1)).delete(passwordReset);
    }

    @Test
    public void changeAccountData_validData_changesData() throws ValidationException {
        ChangeAccountDataDto changeAccountDataDto = new ChangeAccountDataDto("new@test.com","testPassword","newPassword!");

        // Here, we mock the result of the call to findStudentByEmail.
        when(studentRepository.findStudentByEmail("new@test.com")).thenReturn(testStudent);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        accountManagementService.changeAccountData(changeAccountDataDto);

        assertEquals("test@test.com", testStudent.getEmail());
        verify(passwordEncoder, times(1)).encode("newPassword!");
        verify(studentRepository, times(1)).save(testStudent);
    }
    @Test
    public void forgotPassword_validEmail_sendsEmail() {
        when(studentRepository.findByEmail(testStudent.getEmail())).thenReturn(Optional.of(testStudent));

        accountManagementService.forgotPassword(testStudent.getEmail());

        verify(emailSenderService, times(1)).sendEmail(any(SimpleMailMessage.class));
    }

    @Test
    public void forgotPassword_invalidEmail_doesNotSendEmail() {
        when(studentRepository.findByEmail("invalid@test.com")).thenReturn(Optional.empty());

        try {
            accountManagementService.forgotPassword("invalid@test.com");
        } catch (NotFoundException e) {
            // Expected exception
        }

        verify(emailSenderService, times(0)).sendEmail(any(SimpleMailMessage.class));
    }
}