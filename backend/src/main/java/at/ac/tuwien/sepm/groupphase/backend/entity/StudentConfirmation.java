package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.security.StudentDetailsService;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Class for confirmation of a student account.
 */
@Entity
@Table(name = "StudentConfirmation")
public class StudentConfirmation {
    @Id
    private String confirmationToken;
    private LocalDateTime expirationTime;
    @OneToOne
    private Student student;

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public Student getStudent() {
        return student;
    }

    public String getConfirmationToken() {
        return confirmationToken;
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
