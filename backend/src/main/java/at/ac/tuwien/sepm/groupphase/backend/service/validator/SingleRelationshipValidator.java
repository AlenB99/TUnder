package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class SingleRelationshipValidator {
    private final jakarta.validation.Validator beanValidator;
    private final SingleRelationshipRepository repository;
    private final StudentValidator studentValidator;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public SingleRelationshipValidator(SingleRelationshipRepository repository, StudentValidator studentValidator) {
        this.studentValidator = studentValidator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.beanValidator = factory.getValidator();
        this.repository = repository;
    }

    public boolean supports(Class<?> clazz) {
        return SingleRelationship.class.equals(clazz);
    }

    public void validateRelationshipForCreate(Object target) throws ValidationException {
        LOGGER.trace("validate SingleRelationship: {}", target);
        SingleRelationship relationship = (SingleRelationship) target;
        List<String> errorList = new ArrayList<>();
        boolean userFlag = true;
        boolean recomendedFlag = true;

        // Bean validation
        Set<ConstraintViolation<Object>> beanValidationErrors = beanValidator.validate(target);
        beanValidationErrors.forEach(violation -> errorList.add(violation.getMessage()));

        // Additional validations
        if (relationship.getUser() == null) {
            userFlag = false;
            errorList.add("User must not be null");
        } else {
            studentValidator.validateExists(relationship.getUserId());
        }

        if (relationship.getRecommended() == null) {
            recomendedFlag = false;
            errorList.add("Recommended student must not be null");
        } else {
            studentValidator.validateExists(relationship.getRecommendedId());
        }

        if (relationship.getStatus() == null) {
            errorList.add("Relationship status must not be null");
        }

        if (userFlag && recomendedFlag) {
            if (relationship.getUser().equals(relationship.getRecommended())) {
                errorList.add("User and recommended student cannot be the same.");
            }
        }

        // Validate that the user-recommended pair is unique
        if (relationship.getUser() != null && relationship.getRecommended() != null
            && repository.existsByUserAndRecommended(relationship.getUser(), relationship.getRecommended())) {
            errorList.add("A relationship between these two students already exists.");
        }

        if (!errorList.isEmpty()) {
            throw new ValidationException("Failed validations for SingleRelationship", errorList);
        }
    }
}
