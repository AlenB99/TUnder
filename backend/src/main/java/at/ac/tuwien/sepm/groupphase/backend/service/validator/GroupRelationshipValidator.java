package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
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
public class GroupRelationshipValidator {
    private final jakarta.validation.Validator beanValidator;
    private final GroupRelationshipRepository repository;
    private final StudentValidator studentValidator;
    private final GroupValidator groupValidator;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public GroupRelationshipValidator(GroupRelationshipRepository repository, StudentValidator studentValidator, GroupValidator groupValidator) {
        this.studentValidator = studentValidator;
        this.groupValidator = groupValidator;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.beanValidator = factory.getValidator();
        this.repository = repository;
    }

    public boolean supports(Class<?> clazz) {
        return GroupRelationship.class.equals(clazz);
    }




    public void validateGroupRelationshipForCreate(Object target) throws NotFoundException, ValidationException {
        LOGGER.trace("validate GroupRelationship: {}", target);
        GroupRelationship relationship = (GroupRelationship) target;
        List<String> errorList = new ArrayList<>();
        boolean userFlag = true;
        boolean recomendedFlag = true;

        // Bean validation
        Set<ConstraintViolation<Object>> beanValidationErrors = beanValidator.validate(target);
        beanValidationErrors.forEach(violation -> errorList.add(violation.getMessage()));

        // Additional validations
        if (relationship.getUser().getId() == null) {
            userFlag = false;
            errorList.add("User must not be null");
        } else {
            studentValidator.validateExists(relationship.getUser().getId());
        }

        if (relationship.getRecommendedGroup().getId() == null) {
            recomendedFlag = false;
            errorList.add("Recommended group must not be null");
        } else {
            groupValidator.validateExists(relationship.getRecommendedGroup().getId());
        }

        if (relationship.getStatus() == null) {
            errorList.add("Relationship status must not be null");
        }


        // Validate that the user-recommended pair is unique
        if (userFlag && recomendedFlag) {
            if (repository.existsByUserAndRecommendedGroup(relationship.getUser(), relationship.getRecommendedGroup())) {
                errorList.add("Student is already invited or already part of the group.");
            }
        }



        if (!errorList.isEmpty()) {
            throw new ValidationException("Failed validations for SingleRelationship", errorList);
        }

    }
}
