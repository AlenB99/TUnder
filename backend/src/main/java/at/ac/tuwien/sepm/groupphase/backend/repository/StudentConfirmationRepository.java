package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.StudentConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for StudentConfirmationRepository.
 */
@Repository
public interface StudentConfirmationRepository extends JpaRepository<StudentConfirmation, Long> {
    StudentConfirmation findByConfirmationToken(String confirmationToken);

    List<StudentConfirmation> findAllByExpirationTimeBefore(LocalDateTime now);
}
