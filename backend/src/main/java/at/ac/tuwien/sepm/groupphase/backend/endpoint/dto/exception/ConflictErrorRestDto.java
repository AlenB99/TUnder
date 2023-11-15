package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception;

import java.util.List;

/**
 * Record in case a ConflictError occurs, and it needs to be propagated to frontend.
 *
 * @param message message of the error
 * @param errors  list of all the errors
 */
public record ConflictErrorRestDto(
    String message,
    List<String> errors
) {
}

