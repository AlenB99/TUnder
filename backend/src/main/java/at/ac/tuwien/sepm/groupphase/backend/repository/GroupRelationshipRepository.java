package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRelationshipRepository extends JpaRepository<GroupRelationship, Long> {
    boolean existsByUserAndRecommendedGroup(Student user, Group recommendedGroup);

    GroupRelationship findGroupRelationshipByRecommendedGroupAndUser(Group group, Student student);

    List<GroupRelationship> findGroupRelationshipByUser(Student student);

    List<GroupRelationship> findAllByUserAndStatus(Student student, RelStatus status);

    @Query("SELECT m FROM GroupRelationship m WHERE m.status = :status AND "
        + "(m.recommendedGroup = :groupId)")
    List<GroupRelationship> getAllStudentsByGroupAndStatus(@Param("status") RelStatus status, @Param("groupId") Group group);

    @Query("SELECT gr.recommendedGroup FROM GroupRelationship gr WHERE gr.user = :student AND (gr.status = 'Liked' OR gr.status = 'Matched')")
    List<Group> getLikedAndMatchedGroups(@Param("student") Student student);

    @Query("SELECT gr.recommendedGroup FROM GroupRelationship gr WHERE gr.user = :student AND (gr.status = 'Liked' OR gr.status = 'Matched') ORDER BY gr.id DESC")
    List<Group> getLikedAndMatchedGroups(@Param("student") Student student, Pageable pageable);

    @Query("SELECT gr.recommendedGroup FROM GroupRelationship gr WHERE gr.user = :student AND gr.status = 'Disliked'")
    List<Group> getDislikedGroups(Student student);

    @Query("SELECT gr.recommendedGroup FROM GroupRelationship gr WHERE gr.user = :student AND gr.status = 'Disliked' ORDER BY gr.id DESC")
    List<Group> getDislikedGroups(@Param("student") Student student, Pageable pageable);

    void deleteGroupRelationshipByRecommendedGroupAndUser(Group recommendedGroup, Student user);

    void deleteByUser(Student toDelete);
}
