package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public record SimpleStudentDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    String image
) {

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public String image() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleStudentDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "SimpleStudentDto{"
            + "id=" + id
            + ", email=" + email + '\''
            + '}';
    }


}