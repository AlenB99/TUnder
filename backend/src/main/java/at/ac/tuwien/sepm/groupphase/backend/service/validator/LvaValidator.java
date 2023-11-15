package at.ac.tuwien.sepm.groupphase.backend.service.validator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation class for LVA.
 */
@Component
public class LvaValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String LVA_ID_REGEX = "\\d{3}.[A-Z0-9]{3}"; //validates LVA ID
    private static final Pattern pattern = Pattern.compile(LVA_ID_REGEX);
    private static final int LVA_ID_LENGTH = 7; // Max length for LVA ID
    private static final int LVA_NAME_LENGTH = 99; // Max length for LVA Name
    private final LvaRepository lvaRepository;

    @Autowired
    public LvaValidator(LvaRepository lvaRepository) {
        this.lvaRepository = lvaRepository;
    }

    private boolean isValidLvaId(String lvaId) {
        LOGGER.trace("isValidLvaId called with {}", lvaId);
        if (lvaId == null || lvaId.isBlank()) {
            return false;
        }

        Matcher matcher = pattern.matcher(lvaId);
        return matcher.matches() && lvaId.length() == LVA_ID_LENGTH;
    }

    private boolean isValidLvaName(String lvaName) {
        LOGGER.trace("isValidLvaName called with {}", lvaName);
        return lvaName != null && !lvaName.isBlank() && lvaName.length() <= LVA_NAME_LENGTH;
    }

    private boolean doesLvaWithThatIdAlreadyExist(LvaRepository lvaRepository, String id) {
        LOGGER.trace("doesLvaWithThatIDAlreadyExist called with {}", id);
        return lvaRepository.getLvaById(id) != null;
    }

    public void validateLva(Lva lva) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (!isValidLvaId(lva.getId())) {
            errors.add("Make sure it's not blank and follows the correct pattern "
                + "Example: (123.456 or 012.A03).\n");
        }

        if (!isValidLvaName(lva.getName())) {
            errors.add("Make sure it's not blank and doesn't exceed the maximum character limit.\n");
        }

        if (doesLvaWithThatIdAlreadyExist(lvaRepository, lva.getId())) {
            errors.add("LVA already exists with this ID.\n");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("LVA Validation failed", errors);
        }
    }
}
