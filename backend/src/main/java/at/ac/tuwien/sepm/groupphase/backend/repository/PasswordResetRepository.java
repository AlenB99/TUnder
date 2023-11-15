package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.entity.StudentConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    PasswordReset findByConfirmationToken(String confirmationToken);

    List<PasswordReset> findAllByExpirationTimeBefore(LocalDateTime now);
}