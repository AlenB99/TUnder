package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to persist group members.
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @Modifying
    @Query("delete from GroupMember gm where gm.studyGroup.id = :groupId and gm.student.id = :userId")
    void deleteUserFromGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);

    List<GroupMember> getGroupMembersByStudent(Student student);

    boolean existsGroupMemberByStudentAndStudyGroup(Student student, Group studyGroup);

    void deleteByStudent(Student toDelete);

    List<GroupMember> findByStudyGroup(Group group);
}
