package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangeAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

/**
 * Interface for registration service.
 */
public interface RegistrationService {
    /**
     * signUp a new User using a registrationDto.
     *
     * @param registrationDto dto with email, password and repeated password.
     */
    void signUpUser(RegistrationDto registrationDto) throws ValidationException;

    /**
     * Confirm student method.
     *
     * @param registrationToken token which associates a registration with a user account.
     */
    void confirmStudent(String registrationToken);

    /**
     * Method to change account data.
     *
     * @param changeAccountDataDto method to change account data(only password in this iteration)
     */
    void changeAccountData(ChangeAccountDataDto changeAccountDataDto)
        throws ValidationException;

    void forgotPassword(String email);

    void resetPassword(ResetPasswordDto resetPasswordDto);
}
