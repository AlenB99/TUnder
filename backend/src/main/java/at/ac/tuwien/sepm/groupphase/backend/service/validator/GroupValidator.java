package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class GroupValidator {

    private final Validator beanValidator;
    private final GroupRepository groupRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public GroupValidator(Validator beanValidator, GroupRepository groupRepository) {
        this.beanValidator = beanValidator;
        this.groupRepository = groupRepository;
    }

    public boolean supports(Class<?> clazz) {
        return Group.class.equals(clazz);
    }

    public boolean validateExists(Long id) throws ValidationException {
        if (!groupRepository.existsById(id)) {
            throw new ValidationException("No group found with the given ID", List.of("No group found with ID: " + id));
        }
        return true;
    }

    public void validate(Group target) throws ValidationException {
        Group group = (Group) target;
        List<String> errorList = new ArrayList<>();

        // Bean validation
        Set<ConstraintViolation<Object>> beanValidationErrors = beanValidator.validate(target);
        beanValidationErrors.forEach(violation -> errorList.add(violation.getMessage()));

        // Custom validation
        if (group.getName() == null || group.getName().isEmpty()) {
            errorList.add("Group name cannot be empty.");
        }

        if (!errorList.isEmpty()) {
            throw new ValidationException("Failed validations for Group", errorList);
        }
    }
}
