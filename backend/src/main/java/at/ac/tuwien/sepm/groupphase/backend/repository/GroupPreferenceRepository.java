package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.GroupPreference;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupPreferenceRepository extends JpaRepository<GroupPreference, Long> {
    Optional<GroupPreference> findByStudent(Student student);

    void deleteByStudent(Student toDelete);
}
