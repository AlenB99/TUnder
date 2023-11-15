package at.ac.tuwien.sepm.groupphase.backend.exception;

import java.util.Collections;
import java.util.List;

/**
 * Common super class for exceptions that report a list of errors.
 * back to the user, when the given data did not pass a certain kind of checks.
 */
public abstract class ErrorListException extends Exception {
    /**
     * List of errors.
     */
    private final List<String> errors;
    /**
     * Summary of messages.
     */
    private final String messageSummary;

    /**
     * errorListDescriptor.
     */
    private final String errorListDescriptor;

    /**
     * constructor for an ErrorListException.
     *
     * @param errorListDescriptor the errorListDescriptor.
     * @param messageSummary      the messageSummary.
     * @param errors              list of errors.
     */
    public ErrorListException(String errorListDescriptor, String messageSummary, List<String> errors) {
        super(messageSummary);
        this.errorListDescriptor = errorListDescriptor;
        this.messageSummary = messageSummary;
        this.errors = errors;
    }

    /**
     * gets a message.
     *
     * @return gets a message.
     */
    @Override
    public String getMessage() {
        return "%s. %s: %s."
            .formatted(messageSummary, errorListDescriptor, String.join(", ", errors));
    }

    /**
     * gets the summary.
     *
     * @return the summary.
     */
    public String summary() {
        return messageSummary;
    }

    /**
     * gets the errors.
     *
     * @return the errors.
     */
    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }
}

