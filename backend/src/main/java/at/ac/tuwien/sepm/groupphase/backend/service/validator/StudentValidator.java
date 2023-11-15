package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class StudentValidator {

    private final jakarta.validation.Validator beanValidator;
    private final StudentRepository studentRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());



    @Autowired
    public StudentValidator(Validator beanValidator, StudentRepository studentRepository) {
        this.beanValidator = beanValidator;
        this.studentRepository = studentRepository;
    }


    public boolean supports(Class<?> clazz) {
        return Student.class.equals(clazz);
    }

    public void validateExists(Long id) throws NotFoundException {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("No student found with the ID: " + id);
        }
    }

    public void validate(Object target) throws ValidationException {
        Student student = (Student) target;
        List<String> errorList = new ArrayList<>();


        // Bean validation
        Set<ConstraintViolation<Object>> beanValidationErrors = beanValidator.validate(target);
        beanValidationErrors.forEach(violation -> errorList.add(violation.getMessage()));

        // Custom validation
        if (student.getEmail() == null) {
            errorList.add("Email address is null.");

        } else {
            if (!RegistrationValidation.isValidEmail(student.getEmail())) {
                errorList.add("Invalid email address.");
            }
            Optional<Student> existingStudent = studentRepository.findByEmail(student.getEmail());
            if (existingStudent.isPresent() && !existingStudent.get().getId().equals(student.getId())) {
                errorList.add("Email already exists.");
            }
        }

        if (student.getPassword() == null || student.getPassword().length() < 8) {
            errorList.add("Password must be at least 8 characters long.");
        }

        if (student.getFirstName() == null || student.getFirstName().isEmpty()) {
            errorList.add("First name cannot be empty.");
        }

        if (student.getLastName() == null || student.getLastName().isEmpty()) {
            errorList.add("Last name cannot be empty.");
        }

        if (student.getCurrentLvas() != null && student.getCompletedLvas() != null) {
            if (!Collections.disjoint(student.getCurrentLvas(), student.getCompletedLvas())) {
                errorList.add("Completed Lvas cannot contain items contained in current Lvas");
            }
        }

        if (student.getDateOfBirth() != null && student.getDateOfBirth().isAfter(LocalDate.now())) {
            errorList.add("Date of birth cannot be in the future.");
        }

        if (student.getImageUrl() != null) {
            if (!isValidJpeg(student.getImageUrl())) {
                errorList.add("Make sure the picture is a jpeg format");
            }
        }

        if (!errorList.isEmpty()) {
            throw new ValidationException("Failed validations for Student", errorList);
        }
    }


    public boolean isValidJpeg(byte[] imageData) {
        final byte[] magicNumber = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        final byte[] magicNumberExif = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1};
        LOGGER.trace("isValidJPEG called");
        if (imageData.length < 4) {
            return false;
        }

        // Check the magic number at the start of the file
        for (int i = 0; i < magicNumber.length; i++) {
            if (imageData[i] != magicNumber[i] && imageData[i] != magicNumberExif[i]) {
                return false;
            }
        }

        // Check the EOF marker at the end of the file
        byte[] eofMarker = new byte[] {(byte) 0xFF, (byte) 0xD9};
        for (int i = 0; i < eofMarker.length; i++) {
            if (imageData[imageData.length - eofMarker.length + i] != eofMarker[i]) {
                return false;
            }
        }
        return true;
    }
}

