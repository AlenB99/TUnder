package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

public record ResetPasswordDto(
    String confirmationToken,
    String password) {
    @Override
    public String confirmationToken() {
        return confirmationToken;
    }

    @Override
    public String password() {
        return password;
    }
}
