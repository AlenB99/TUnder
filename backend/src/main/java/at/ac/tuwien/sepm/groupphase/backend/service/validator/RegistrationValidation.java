package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangeAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation class for registration.
 */
public class RegistrationValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String NAME_PATTERN = "^[\\p{L}][\\p{L}]{0,24}$";
    private static final String EMAIL_REGEX = "e[0-9]{8}@student\\.tuwien\\.ac\\.at";  //validates a tuwien student email.
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$"; //validates password, so that it has at least 8 characters, one upper -, one lower-case character as well as one special.

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static boolean isPasswordTheSame(String password, String repeatedPassword) {
        LOGGER.trace("is isValidPassword called with {} {}", password, repeatedPassword);
        return Objects.equals(password, repeatedPassword);
    }

    private static boolean doesAccountWithEmailAlreadyExist(StudentRepository studentRepository, String email) {
        LOGGER.trace("is doesAccountWithEmailAlreadyExist called with {}", email);
        return studentRepository.findStudentByEmail(email) != null;
    }

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        return pattern.matcher(password).matches();
    }

    public static void validateRegistrationDto(RegistrationDto registrationDto, StudentRepository studentRepository) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (!isValidEmail(registrationDto.email())) {
            errors.add("Email did not validate.\n");
        }

        if (!validatePassword(registrationDto.password())) {
            errors.add("Password did not validate\n");
        }

        if (!isPasswordTheSame(registrationDto.password(), registrationDto.repeatedPassword())) {
            errors.add("Password and the repeated password are not the same.");
        }

        if (doesAccountWithEmailAlreadyExist(studentRepository, registrationDto.email())) {
            errors.add("Account with provided email already exists.");
        }

        if (!validateBirthdate(registrationDto)) {
            errors.add("Birthday can not be in the future");
        }

        if (!validateName(registrationDto)) {
            errors.add("Names did not validate make sure they start with a capital letter, contains max 25 characters and only has A-Z.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    public static boolean validateBirthdate(RegistrationDto registrationDto) {
        return registrationDto.dateOfBirth().isBefore(ChronoLocalDate.from(LocalDateTime.now()));
    }

    public static boolean validateName(RegistrationDto registrationDto) {
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher firstNameMatcher = pattern.matcher(registrationDto.firstName());
        Matcher lastNameMatcher = pattern.matcher(registrationDto.lastName());

        return firstNameMatcher.matches() && lastNameMatcher.matches();
    }

    public static void validateChangeAccountData(Student student, ChangeAccountDataDto changeAccountDataDto, PasswordEncoder passwordEncoder) throws ValidationException {
        List<String> errors = new ArrayList<>();
        if (student == null) {
            errors.add("Account with provided email, does not exist.");
        } else {
            if (isPasswordTheSameForChange(passwordEncoder, student.getPassword(), changeAccountDataDto.newPassword())) {
                errors.add("Provided old password does not match current password.");
            }
        }
        if (!validatePassword(changeAccountDataDto.newPassword())) {
            errors.add("Provided new password does not validate.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    private static boolean isPasswordTheSameForChange(PasswordEncoder passwordEncoder, String oldPassword, String newPassword) {
        return passwordEncoder.matches(newPassword, oldPassword);
    }
}
