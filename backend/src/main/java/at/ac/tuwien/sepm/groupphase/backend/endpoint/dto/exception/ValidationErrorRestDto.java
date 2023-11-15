package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception;

import java.util.List;

/**
 * Dto for ValidationError.
 *
 * @param message message.
 * @param errors  errors.
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}

