package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilterValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public FilterValidator() {
    }

    public void validateFilterForSave(Filter filter, FilterService filterService, LvaService lvaService) throws ConflictException, ValidationException {
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictionErrors = new ArrayList<>();

        if (filter.getStudent() == null) {
            //TOASK: throw fatal exception as this should not be happening
            validationErrors.add("No student provided");
        }
        if (filter.getId() != null) {
            var currentFilter = filterService.getFilter(filter.getId());
            if (currentFilter.getStudent().getId() != filter.getStudent().getId()) {
                //TOASK: throw fatal exception as this should not be happening
                conflictionErrors.add("Student must not be changed for a existing filter");
            }
        }
        validateAge(filter, validationErrors);
        validateLva(filter.getLvas(), lvaService, validationErrors, conflictionErrors);

        //TODO: add lva validation

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Error saving filter", validationErrors);
        }
        if (!conflictionErrors.isEmpty()) {
            throw new ConflictException("Conflicts saving filter", conflictionErrors);
        }
    }

    private void validateAge(Filter filter, List<String> validationErrors) {
        var minAge = filter.getMinAge();
        var maxAge = filter.getMaxAge();
        if (minAge != null) {
            if (minAge < 0) {
                validationErrors.add("Minimal age must be greater 0");
            }
            if (maxAge != null && maxAge < minAge) {
                validationErrors.add("Maximal age must be greater minimal age");
            }
        }
        if (maxAge != null) {
            if (maxAge < 0) {
                validationErrors.add("Maximal age must be greater 0");
            }
        }
    }

    private void validateLva(List<Lva> lvas, LvaService lvaService, List<String> validationErrors, List<String> conflictErrors) {
        if (lvas != null) {
            for (var lva : lvas
            ) {
                try {
                    lvaService.getLvaById(lva.getId());
                    //TOASK: should we check if titles match
                } catch (NotFoundException e) {
                    conflictErrors.add("LVA with id: " + lva.getId() + " does not exist");
                }
            }
        }
    }
}
