package at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception.ConflictErrorRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception.NotFoundErrorRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception.ValidationErrorRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.exception.BadCredentialsExceptionDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.invoke.MethodHandles;

/**
 * Register all your Java exceptions here to map them into meaningful HTTP exceptions
 * If you have special cases which are only important for specific endpoints, use <a href="https://www.baeldung.com/exception-handling-for-rest-with-spring#responsestatusexception">ResponseStatusExceptions</a>.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * ExceptionHandler for ValidationException.
     *
     * @param e exception.
     * @return new error Dto.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ValidationErrorRestDto handleValidationException(ValidationException e) {
        LOGGER.warn("Terminating request processing with status 422 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ValidationErrorRestDto(e.summary(), e.errors());
    }

    /**
     * ExceptionHandler for NotFoundException.
     *
     * @param notFoundException the exception.
     * @return dto for exception.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public NotFoundErrorRestDto handleNotFoundException(NotFoundException notFoundException) {
        LOGGER.warn("Terminating request processing with status 404 due to {}: {}", notFoundException.getClass().getSimpleName(), notFoundException.getMessage());
        return new NotFoundErrorRestDto(notFoundException.getMessage());
    }

    /**
     * ExceptionHandler for NotFoundException.
     *
     * @param badCredentialsException the exception.
     * @return dto for exception.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public BadCredentialsExceptionDto handleNotFoundException(BadCredentialsException badCredentialsException) {
        LOGGER.warn("Terminating request processing with status 404 due to {}: {}", badCredentialsException.getClass().getSimpleName(), badCredentialsException.getMessage());
        return new BadCredentialsExceptionDto(badCredentialsException.getMessage());
    }

    /**
     * ExceptionHandler for ConflictException.
     *
     * @param conflictException the exception.
     * @return ConflictErrorRestDto.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ConflictErrorRestDto handleConflictException(ConflictException conflictException) {
        LOGGER.warn("Terminating request processing with status 409 due to {}: {}", conflictException.getClass().getSimpleName(), conflictException.getMessage());
        return new ConflictErrorRestDto(conflictException.summary(), conflictException.errors());
    }

    /**
     * ExceptionHandler for NotAuthorizedException.
     *
     * @param notAuthorizedException the exception.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleConflictException(NotAuthorizedException notAuthorizedException) {
        LOGGER.warn("Terminating request processing with status 401 due to {}: {}", notAuthorizedException.getClass().getSimpleName(), notAuthorizedException.getMessage());
    }
}
