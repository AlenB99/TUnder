package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangeAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.service.RegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * Controller for account-management related data, as well as registration.
 */
@RestController
@RequestMapping(value = "/api/v1/registration")
public class AccountManagementController {
    private final RegistrationService registrationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AccountManagementController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Endpoint accepting a post request for registering a new account. Permits all requests.
     *
     * @param registrationDto DTO which holds all neccessary information for creating a new account.
     */
    @PermitAll
    @PostMapping("/signup")
    public void signUp(@RequestBody RegistrationDto registrationDto) throws ValidationException {
        LOGGER.info("signUp called with {}", registrationDto);
        registrationService.signUpUser(registrationDto);
    }

    /**
     * Endpoint to confirm an account. Wheen a user creates a new account, the user receives an email, which contains a link with a token targeting this endpoint.
     * This endpoint should never be user otherwise.
     *
     * @param confirmationToken The token which associates an open confirmation to a student account.
     */
    @PermitAll
    @GetMapping("/confirm-account")
    public void confirmUserAccount(@RequestParam("token") String confirmationToken, HttpServletResponse response) throws IOException {
        LOGGER.info("confirmUserAccount called with {}", confirmationToken);
        registrationService.confirmStudent(confirmationToken);
        response.sendRedirect("http://localhost:4200/#/login");
    }

    /**
     * Endpoint to change account related data, as of version 1 only password.
     *
     * @param changeAccountDataDto The dto holding the data to change account data.
     */
    @Secured("ROLE_USER")
    @PutMapping("/edit-account")
    public void editUserAccount(@RequestBody ChangeAccountDataDto changeAccountDataDto) throws ValidationException {
        LOGGER.info("confirmUserAccount called with {}", changeAccountDataDto);
        registrationService.changeAccountData(changeAccountDataDto);
    }

    @PermitAll
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody SimpleStudentDto email) {
        LOGGER.info("forgotPassword called with {}", email);
        registrationService.forgotPassword(email.email());
    }

    @PermitAll
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        LOGGER.info("confirmUserAccount called with {}", resetPasswordDto);
        registrationService.resetPassword(resetPasswordDto);
    }
}
