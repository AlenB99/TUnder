package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

/**
 * DTO to chnage account data.
 *
 * @param email email of the account to be changed
 * @param oldPassword old password
 * @param newPassword new password
 */
public record ChangeAccountDataDto(String email, String oldPassword, String newPassword) {
    @Override
    public String email() {
        return email;
    }

    @Override
    public String oldPassword() {
        return oldPassword;
    }

    @Override
    public String newPassword() {
        return newPassword;
    }
}
