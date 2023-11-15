package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.FilterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
@SpringBootTest
public class FilterValidatorTest {

    @Mock
    FilterService filterService;

    @Mock
    LvaService lvaService;

    @Autowired
    FilterValidator filterValidator;

    @Test
    public void validateForSave_newFilter_noErrors() {
        var filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(20).withStudent(1L).build();
        assertDoesNotThrow(()->filterValidator.validateFilterForSave(filter, filterService, lvaService));
    }

    @Test
    public void validateForSave_updateExisting_noErrors() {
        var filter = Filter.FilterBuilder.aFilter().withId(1L).withStudent(1L).build();
        var existingFilter = Filter.FilterBuilder.aFilter().withId(1L).withStudent(1L).withMinAge(21).build();


        given(filterService.getFilter(filter.getId())).willReturn(existingFilter);
        assertDoesNotThrow(() -> filterValidator.validateFilterForSave(filter, filterService, lvaService));
    }

    @Test
    public void validateForSave_MaxAgeLowerMinAge_ThrowsValidationException() {
        var filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(19).withStudent(1L).build();
        var ex = assertThrows(ValidationException.class, () -> filterValidator.validateFilterForSave(filter, filterService, lvaService));
        assertEquals("Error saving filter. Failed validations: Maximal age must be greater minimal age.", ex.getMessage());
    }

    @Test
    public void validateForSave_MaxAgeNegative_ThrowsValidationException() {
        var filter = Filter.FilterBuilder.aFilter().withMaxAge(-1).withStudent(1L).build();
        var ex = assertThrows(ValidationException.class, () -> filterValidator.validateFilterForSave(filter, filterService, lvaService));
        assertEquals("Error saving filter. Failed validations: Maximal age must be greater 0.", ex.getMessage());
    }

    @Test
    public void validateForSave_MinAgeNegative_ThrowsValidationException() {
        var filter = Filter.FilterBuilder.aFilter().withMinAge(-1).withStudent(1L).build();
        var ex = assertThrows(ValidationException.class, () -> filterValidator.validateFilterForSave(filter, filterService, lvaService));
        assertEquals("Error saving filter. Failed validations: Minimal age must be greater 0.", ex.getMessage());
    }

    @Test
    public void validateForSave_NoStudentProvided_ThrowsValidationException() {
        var filter = Filter.FilterBuilder.aFilter().build();
        var ex = assertThrows(ValidationException.class, () -> filterValidator.validateFilterForSave(filter, filterService, lvaService));
        assertEquals("Error saving filter. Failed validations: No student provided.", ex.getMessage());
    }

    @Test
    public void validateForSave_UpdateFilterChangeStudent_ThrowsConflictException() {
        var filter = Filter.FilterBuilder.aFilter().withId(1L).withStudent(1L).build();
        var existingFilter = Filter.FilterBuilder.aFilter().withId(1L).withStudent(2L).build();


        given(filterService.getFilter(filter.getId())).willReturn(existingFilter);
        var ex = assertThrows(ConflictException.class, () -> filterValidator.validateFilterForSave(filter, filterService, lvaService));
        assertEquals("Conflicts saving filter. Conflicts: Student must not be changed for a existing filter.", ex.getMessage());
    }

    @Test
    public void validateForSave_newFilterWithExistingLVA_noErrors() {
        var lva = Lva.LvaBuilder.aLvaBuilder().withId("1L").build();
        given(lvaService.getLvaById(lva.getId())).willReturn(lva);

        var filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(20).withStudent(1L).withLvas(List.of(lva)).build();
        assertDoesNotThrow(()->filterValidator.validateFilterForSave(filter, filterService, lvaService));
    }

    @Test
    public void validateForSave_newFilterWithNonExistingLVA_conflictError() {
        var lva = Lva.LvaBuilder.aLvaBuilder().withId("1L").build();
        given(lvaService.getLvaById(lva.getId())).willThrow(NotFoundException.class);

        var filter = Filter.FilterBuilder.aFilter().withMinAge(20).withMaxAge(20).withStudent(1L).withLvas(List.of(lva)).build();

        var ex = assertThrows(ConflictException.class, ()->filterValidator.validateFilterForSave(filter, filterService, lvaService));
        assertEquals("Conflicts saving filter. Conflicts: LVA with id: 1L does not exist.", ex.getMessage());
    }
}
