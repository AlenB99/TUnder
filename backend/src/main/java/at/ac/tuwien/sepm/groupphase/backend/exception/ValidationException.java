package at.ac.tuwien.sepm.groupphase.backend.exception;

import java.util.List;

public class ValidationException extends ErrorListException {
    /**
     * Constructor for ValidationException.
     *
     * @param messageSummary messageSummary.
     * @param errors         errors.
     */
    public ValidationException(String messageSummary, List<String> errors) {
        super("Failed validations", messageSummary, errors);
    }
}

