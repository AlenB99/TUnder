package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception;

/**
 * Dto in case of an NotFoundError.
 *
 * @param message message of the error.
 */
public record NotFoundErrorRestDto(
    String message
) {
}

