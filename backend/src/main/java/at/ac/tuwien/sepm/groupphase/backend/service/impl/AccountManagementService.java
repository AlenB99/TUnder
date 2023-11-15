package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.repository.SettingsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.RegistrationValidation;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangeAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenExpiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.entity.StudentConfirmation;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentConfirmationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RegistrationService where a new user/student can be registered.
 */
@Service
public class AccountManagementService implements at.ac.tuwien.sepm.groupphase.backend.service.RegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String MAIL_SUBJECT_SIGNUP = "Welcome to TUnder! \nComplete your registration now!";
    private static final String MAIL_TEXT_SIGNUP = "To confirm your account, please click here (expires in 15 minutes) \n http://localhost:8080/api/v1/registration/confirm-account?token=";
    private static final String MAIL_TEXT_PASSWORD_RESET = "Dear User, click this link to change your password(expires in 15 minutes): http://localhost:4200/#/resetPassword/";
    private static final String MAIL_SUBJECT_PASSWORD_RESET = "TUnder Password Reset";
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderServiceImpl emailSenderService;
    private final StudentConfirmationRepository studentConfirmationRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final SettingsRepository settingsRepository;

    public AccountManagementService(StudentRepository studentRepository, PasswordEncoder passwordEncoder,
                                    StudentConfirmationRepository studentConfirmationRepository,
                                    EmailSenderServiceImpl emailSenderService,
                                    PasswordResetRepository passwordResetRepository, SettingsRepository settingsRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentConfirmationRepository = studentConfirmationRepository;
        this.emailSenderService = emailSenderService;
        this.passwordResetRepository = passwordResetRepository;
        this.settingsRepository = settingsRepository;
    }

    public void signUpUser(RegistrationDto registrationDto) throws ValidationException {
        LOGGER.trace("signUpUser called with {}", registrationDto);
        //validate the data registered.
        RegistrationValidation.validateRegistrationDto(registrationDto, studentRepository);

        //create a basic student with the data from the registrationDto
        Student basicStudent = createBasicStudentAccount(registrationDto);
        studentRepository.save(basicStudent);

        //create a studentConfirmation for the provided student.
        StudentConfirmation studentConfirmation = createBasicStudentConfirmation(basicStudent);
        studentConfirmationRepository.save(studentConfirmation);

        //send the confirmation mail.
        sendConfirmationMail(basicStudent, studentConfirmation.getConfirmationToken());
    }

    public void confirmStudent(String confirmationToken) {
        LOGGER.trace("confirmStudent called with {}", confirmationToken);
        StudentConfirmation confirmation = studentConfirmationRepository.findByConfirmationToken(confirmationToken);
        if (confirmation == null) {
            throw new NotFoundException("Student confirmation not found.");
        }
        if (confirmation.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("The token " + confirmation.getConfirmationToken() + " is already expired. Please register again to receive a new email.");
        }
        Student student = confirmation.getStudent();
        enableStudent(student);

        studentConfirmationRepository.delete(confirmation);
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        LOGGER.trace("resetPassword called with {}", resetPasswordDto);
        PasswordReset passwordReset = passwordResetRepository.findByConfirmationToken(resetPasswordDto.confirmationToken());
        if (passwordReset == null) {
            throw new NotFoundException("Password Reset not permitted.");
        }
        if (passwordReset.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("The token " + passwordReset.getConfirmationToken() + " is already expired. Please register again to receive a new email.");
        }
        Student student = passwordReset.getStudent();
        student.setPassword(passwordEncoder.encode(resetPasswordDto.password()));
        studentRepository.save(student);
        passwordResetRepository.delete(passwordReset);
    }

    @Override
    public void changeAccountData(ChangeAccountDataDto changeAccountDataDto) throws ValidationException {
        LOGGER.trace("sendConfirmationMail called with {}", changeAccountDataDto);
        var student = studentRepository.findStudentByEmail(changeAccountDataDto.email());
        RegistrationValidation.validateChangeAccountData(student, changeAccountDataDto, passwordEncoder);
        student.setPassword(passwordEncoder.encode(changeAccountDataDto.newPassword()));
        studentRepository.save(student);
    }

    @Override
    public void forgotPassword(String email) {
        LOGGER.trace("forgotPassword called with email {}", email);
        Optional<Student> student = studentRepository.findByEmail(email);
        if (!student.isPresent()) {
            throw new NotFoundException("Account with email " + email + " not found.");
        }
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setConfirmationToken(UUID.randomUUID().toString());
        passwordReset.setStudent(student.get());
        passwordReset.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        passwordResetRepository.save(passwordReset);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(student.get().getEmail());
        simpleMailMessage.setSubject(MAIL_SUBJECT_PASSWORD_RESET);
        simpleMailMessage.setText(MAIL_TEXT_PASSWORD_RESET + passwordReset.getConfirmationToken());
        emailSenderService.sendEmail(simpleMailMessage);
    }

    public void sendConfirmationMail(Student student, String confirmationToken) {
        LOGGER.trace("sendConfirmationMail called with {} {}", student, confirmationToken);
        emailSenderService.sendEmail(createSimpleMailMessage(student, confirmationToken));
    }

    private SimpleMailMessage createSimpleMailMessage(Student student, String confirmationToken) {
        LOGGER.trace("createSimpleMailMessage");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(student.getEmail());
        mailMessage.setSubject(MAIL_SUBJECT_SIGNUP);
        mailMessage.setText(MAIL_TEXT_SIGNUP + confirmationToken);
        return mailMessage;
    }

    /**
     * Method called every 15 minutes, clears all expired token and the associated user accounts.
     */
    @Scheduled(fixedDelay = 900000) // 15 minutes in milliseconds
    private void clearExpiredConfirmations() {
        LOGGER.info("clearExpiredConfirmations called.");
        LocalDateTime now = LocalDateTime.now();
        List<StudentConfirmation> expiredConfirmations = studentConfirmationRepository.findAllByExpirationTimeBefore(now);

        for (StudentConfirmation confirmation : expiredConfirmations) {
            Student student = confirmation.getStudent();
            studentRepository.delete(student);
        }
        studentConfirmationRepository.deleteAll(expiredConfirmations);
    }

    /**
     * Enable the student account. Called after confirmation.
     *
     * @param student student which is enabled.
     */
    private void enableStudent(Student student) {
        student.setEnabled(true);
        studentRepository.save(student);
    }

    private Student createBasicStudentAccount(RegistrationDto registrationDto) {
        LOGGER.trace("called createBasicStudentAccount with {}", registrationDto);
        Student student = new Student();
        student.setEmail(registrationDto.email());
        student.setPassword(passwordEncoder.encode(registrationDto.password()));
        student.setEnabled(false);
        student.setAdmin(false);
        student.setFirstName(registrationDto.firstName());
        student.setLastName(registrationDto.lastName());
        student.setDateOfBirth(registrationDto.dateOfBirth());
        Settings settings = new Settings();
        settings.setSubscribing(true);
        settings.setSleeping(false);
        settings.setStudent(student);
        student.setSettings(settings);
        return student;
    }

    private StudentConfirmation createBasicStudentConfirmation(Student student) {
        LOGGER.trace("called createBasicStudentConfirmation with {}", student);
        StudentConfirmation studentConfirmation = new StudentConfirmation();
        studentConfirmation.setConfirmationToken(UUID.randomUUID().toString());
        studentConfirmation.setStudent(student);
        studentConfirmation.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        return studentConfirmation;
    }
}
