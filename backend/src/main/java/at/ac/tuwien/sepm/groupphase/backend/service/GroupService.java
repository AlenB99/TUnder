package at.ac.tuwien.sepm.groupphase.backend.service;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface GroupService {

    List<Group> findAll();

    Group findGroupById(Long id) throws NotFoundException;

    Group persistGroup(Group group) throws ValidationException;

    Group updateGroup(Group group) throws ValidationException;

    Group createGroup(Group group) throws ValidationException;

    Group addStudentByMatr(Group group, long id) throws NotFoundException;

    /**
     * loads all students who applied for a group where the admin is group leader.
     *
     * @param id of the group
     * @param jwtId id of the admin
     * @return list of students who liked the group
     *
     * @throws NotAuthorizedException if the initiator of the request is not an admin
     */
    List<SimpleStudentDto> loadStudents(long id, long jwtId) throws NotAuthorizedException;

    /**
     * When a user likes a group, the admin can add the student to the group using this method.
     *
     * @param group group to join
     * @param id id of the student
     * @return the group with the new student.
     */
    Group addStudentByAdmin(Group group, long id);

    /**
     * Student declines invite to group.
     *
     * @param group group which invited the student
     * @param id id of the student
     * @return a list of simple students which also want to join the group.
     */
    List<SimpleStudentDto> declineGroupApplication(Group group, long id);

    List<Group> getGroupsByGroupLeaderId(long leaderId);

    List<GroupRelationship> declineGroupInvite(Group group, long id);

    /**
     * Remove one student from a group he is in, and you are the group-leader.
     *
     * @param id student to remove
     * @param groupId group to be removed from
     * @param id1 id from JWT Token
     * @return Group without student with id
     * @throws NotAuthorizedException if the JWT Token does not contain the id of the group leader
     * @throws ValidationException if the group does not exist
     */
    Group removeUserFromGroup(Long id, Long groupId, Long id1) throws NotAuthorizedException, ValidationException;

    /**
     * Loads all invites of student with the given id.
     *
     * @param id of the student
     * @return all outstanding group invitations.
     */
    List<GroupRelationship> loadInvites(Long id);

    Group leaveGroup(long groupId, long userId);

    List<GroupRelationship> acceptGroupInvite(Group group, long id) throws NotFoundException;
}