package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository to persist Filters in.
 */
@Repository
public interface FilterRepository extends JpaRepository<Filter, Long> {
    /**
     * delete a filter for student given in {@code student}.
     *
     * @param student student to delete filter for
     * @return count of deleted filters - must not be > 1
     */
    long deleteByStudent(Student student);

    /**
     * gets filter for Student given in {@code student} if exists.
     *
     * @param student student to get filter for
     * @return optional filter
     */
    Optional<Filter> findByStudent(Student student);

    Optional<Filter> findByStudent_Id(Long studentId);
}
