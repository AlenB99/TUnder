package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GroupRelationshipValidatorTest {

    @Mock
    private GroupRelationshipRepository repository;

    @Mock
    private StudentValidator studentValidator;

    @Mock
    private GroupValidator groupValidator;

    @InjectMocks
    private GroupRelationshipValidator validator;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void validateGroupRelationshipForCreate_validRelationship_noExceptionThrown() throws NotFoundException, ValidationException {
        // given
        Student student = new Student(1L);
        Group group = new Group(2L);
        GroupRelationship groupRelationship = new GroupRelationship(student, group, RelStatus.LIKED);

        // when
        validator.validateGroupRelationshipForCreate(groupRelationship);

        // then
        // no exception is thrown
    }

    /*
    @Test
    public void validateGroupRelationshipForCreate_relationshipAlreadyExists_throwsValidationException() throws NotFoundException, ValidationException {
        // given
        Student student = new Student(1L);
        Group group = new Group(2L);
        GroupRelationship groupRelationship = new GroupRelationship(student, group, RelStatus.LIKED);

        doNothing().when(studentValidator).validateExists(any());
        doNothing().when(groupValidator).validateExists(any());
        when(repository.existsByUserAndRecommendedGroup(any(Student.class), any(Group.class))).thenReturn(true);


        // when
        assertThrows(ValidationException.class, () -> validator.validateGroupRelationshipForCreate(groupRelationship));

        // then
        // a ValidationException is thrown
    }

     */
}
