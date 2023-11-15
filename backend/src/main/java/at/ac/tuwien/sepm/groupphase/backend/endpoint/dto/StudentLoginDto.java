package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class StudentLoginDto {

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "Password must not be null")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentLoginDto studentLoginDto)) {
            return false;
        }
        return Objects.equals(email, studentLoginDto.email)
            && Objects.equals(password, studentLoginDto.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "StudentLoginDto{"
            + "email='" + email + '\''
            + ", password='" + password + '\''
            + '}';
    }


    public static final class StudentLoginDtoBuilder {
        private String email;
        private String password;

        private StudentLoginDtoBuilder() {
        }

        public static StudentLoginDtoBuilder anUserLoginDto() {
            return new StudentLoginDtoBuilder();
        }

        public StudentLoginDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public StudentLoginDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public StudentLoginDto build() {
            StudentLoginDto studentLoginDto = new StudentLoginDto();
            studentLoginDto.setEmail(email);
            studentLoginDto.setPassword(password);
            return studentLoginDto;
        }
    }
}
