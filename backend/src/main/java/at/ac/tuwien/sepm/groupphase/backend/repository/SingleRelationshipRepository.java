package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleRelationshipRepository extends JpaRepository<SingleRelationship, Long> {

    boolean existsByUserAndRecommended(Student user, Student recommended);

    SingleRelationship getSingleRelationshipById(Long id);

    List<SingleRelationship> getSingleRelationshipByUser(Student student);

    @Query("SELECT m FROM SingleRelationship m WHERE m.status = :status AND "
        + "((m.user = :student1 AND m.recommended = :student2) OR "
        + "(m.user = :student2 AND m.recommended = :student1))")
    SingleRelationship findRelationshipByUserIdsAndStatus(@Param("status") RelStatus status, @Param("student1") Student student1, @Param("student2") Student student2);

    @Query("SELECT m FROM SingleRelationship m WHERE m.status = :status AND "
        + "(m.user = :student OR m.recommended = :student)")
    List<SingleRelationship> findRelationshipsByUserIdsAndStatus(@Param("status") RelStatus status, @Param("student") Student student);

    @Query("SELECT m FROM SingleRelationship m WHERE m.status = :status AND "
        + "(m.user = :student1) AND"
        + "(m.recommended = :student2)")
    SingleRelationship isAlreadyStatus(@Param("status") RelStatus status, @Param("student1") Student student1, @Param("student2") Student student2);

    @Query("SELECT m FROM SingleRelationship m WHERE m.status = :status AND "
        + "(m.user = :student)")
    List<SingleRelationship> findLikesByUserIdAndStatus(@Param("status") RelStatus status, @Param("student") Student student);


    @Query("SELECT sr.recommended FROM SingleRelationship sr WHERE sr.user = :student AND (sr.status = 'LIKED' OR sr.status = 'MATCHED')")
    List<Student> getLikedAndMatchedStudents(@Param("student") Student student);

    @Query("SELECT sr.recommended FROM SingleRelationship sr WHERE (sr.user = :student AND sr.status = 'LIKED') OR ((sr.user = :student OR sr.recommended = :student) AND sr.status = 'MATCHED') ORDER BY sr.id DESC")
    List<Student> getLikedAndMatchedStudents(@Param("student") Student student, Pageable pageable);


    @Query("SELECT sr.recommended FROM SingleRelationship sr WHERE sr.user = :student AND sr.status = 'DISLIKED'")
    List<Student> getDislikedStudents(@Param("student") Student student);

    @Query("SELECT sr.recommended FROM SingleRelationship sr WHERE sr.user = :student AND sr.status = 'DISLIKED' ORDER BY sr.id DESC")
    List<Student> getDislikedStudents(@Param("student") Student student, Pageable pageable);

    @Modifying
    @Query("DELETE FROM SingleRelationship WHERE (user = :user AND recommended = :recommended) OR (user = :recommended AND recommended = :user)")
    void deleteByRecommendedAndUser(@Param("recommended") Student recommended, @Param("user") Student user);
}

