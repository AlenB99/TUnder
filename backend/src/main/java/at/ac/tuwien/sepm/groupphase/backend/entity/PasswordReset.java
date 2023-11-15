package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "PasswordReset")
public class PasswordReset {
    @Id
    private String confirmationToken;
    private LocalDateTime expirationTime;
    @OneToOne
    private Student student;

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public Student getStudent() {
        return student;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
