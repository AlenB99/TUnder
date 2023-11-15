package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMemberRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRelationshipRepository groupRelationshipRepository;
    private final GroupValidator validator;
    private final StudentValidator studentValidator;
    private final StudentService studentService;
    private final CustomMatchMapper customMatchMapper;
    private final GroupValidator groupValidator;
    private final GroupRelationshipValidator groupRelationshipValidator;

    public GroupServiceImpl(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository,
                            GroupRelationshipRepository groupRelationshipRepository, GroupValidator validator, StudentValidator studentValidator,
                            StudentService studentService, GroupValidator groupValidator, GroupRelationshipValidator groupRelationshipValidator) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupRelationshipRepository = groupRelationshipRepository;
        this.validator = validator;
        this.groupValidator = groupValidator;
        this.groupRelationshipValidator = groupRelationshipValidator;
        this.customMatchMapper = new CustomMatchMapper();
        this.studentValidator = studentValidator;
        this.studentService = studentService;
    }

    @Override
    public List<Group> findAll() {
        LOGGER.debug("Find all groups");
        return groupRepository.findAll();
    }

    @Override
    public Group findGroupById(Long id) {
        LOGGER.debug("Get group by id: " + id);
        return groupRepository.findById(id).orElseThrow(() -> new NotFoundException("Group not found"));
    }

    @Override
    public Group persistGroup(Group group) throws ValidationException {
        LOGGER.trace("Persist new Group {}", group);
        validator.validate(group);
        return groupRepository.save(group);
    }

    @Override
    public Group updateGroup(Group group) throws ValidationException {
        LOGGER.trace("Update Group {}", group);
        validator.validate(group);
        return groupRepository.save(group);
    }

    @Override
    public Group createGroup(Group group) throws ValidationException {
        LOGGER.trace("Create Group: {}", group);
        validator.validate(group);
        Student groupLeader = studentService.findStudentById(group.getGroupLeaderId());
        GroupMember groupMemberLeader = new GroupMember(
            group,
            groupLeader
        );
        List<GroupMember> list = new ArrayList<>();
        list.add(groupMemberLeader);
        group.setMembers(list);
        Group savedGroup = groupRepository.save(group);
        groupMemberRepository.save(groupMemberLeader);
        return savedGroup;
    }

    @Override
    @Transactional
    public Group addStudentByMatr(Group group, long id) throws NotFoundException {
        LOGGER.trace("Add Student {} to Group: {}", id, group);
        studentValidator.validateExists(id);
        Student member = studentService.findStudentById(id);
        GroupMember groupMember = new GroupMember(
            group,
            member
        );
        group.addGroupMember(groupMember);
        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public Group addStudentByAdmin(Group group, long id) throws NotFoundException {
        LOGGER.trace("Add Student {} to Group initaiated by admin: {}", group, id);
        GroupMember groupMember = createGroupMember(group, id);
        group.addGroupMember(groupMember);
        groupMemberRepository.save(groupMember);
        groupRelationshipRepository.save(changeGroupRelationship(group, groupMember.getStudent(), RelStatus.MATCHED));
        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public List<GroupRelationship> acceptGroupInvite(Group group, long id) throws NotFoundException {
        LOGGER.trace("Add Student {} to Group initaiated by admin: {}", group, id);
        GroupMember groupMember = createGroupMember(group, id);
        group.addGroupMember(groupMember);
        groupMemberRepository.save(groupMember);
        groupRepository.save(group);
        groupRelationshipRepository.save(changeGroupRelationship(group, groupMember.getStudent(), RelStatus.MATCHED));
        return loadInvites(id);
    }

    @Override
    public List<Group> getGroupsByGroupLeaderId(long leaderId) {
        LOGGER.trace("Get groups with leaderId: {}", leaderId);
        return groupRepository.getGroupsByGroupLeaderId(leaderId);
    }

    @Override
    public List<SimpleStudentDto> declineGroupApplication(Group group, long id) {
        LOGGER.trace("Decline invitation to group by {} and student {}", group, id);
        GroupMember groupMember = createGroupMember(group, id);
        groupRelationshipRepository.save(changeGroupRelationship(group, groupMember.getStudent(), RelStatus.DELETED));
        return customMatchMapper.groupRelsToSimpleStudents(groupRelationshipRepository.getAllStudentsByGroupAndStatus(RelStatus.LIKED, group));
    }

    @Override
    public List<GroupRelationship> loadInvites(Long id) {
        return groupRelationshipRepository.findAllByUserAndStatus(studentService.findStudentById(id), RelStatus.INVITED);
    }

    @Override
    public List<GroupRelationship> declineGroupInvite(Group group, long id) {
        LOGGER.trace("Decline invitation to group by {} and student {}", group, id);
        GroupMember groupMember = createGroupMember(group, id);
        groupRelationshipRepository.save(changeGroupRelationship(group, groupMember.getStudent(), RelStatus.DELETED));
        return loadInvites(id);
    }

    @Override
    @Transactional
    public Group removeUserFromGroup(Long id, Long groupId, Long jwtId) throws NotAuthorizedException, ValidationException {
        groupValidator.validateExists(groupId);
        var group = groupRepository.getGroupById(groupId);
        if (!group.getGroupLeaderId().equals(jwtId)) {
            throw new NotAuthorizedException("Not authoritzed to perform this action");
        }

        List<GroupMember> memberList = group.getMembers();
        GroupMember groupMember = null;
        Iterator<GroupMember> iterator = memberList.iterator();

        while (iterator.hasNext()) {
            GroupMember member = iterator.next();
            if (member.getStudent().getId().equals(id)) {
                groupMember = member;
                iterator.remove();
                break;
            }
        }

        // Delete the GroupMember first
        groupMemberRepository.deleteUserFromGroup(id, groupId);

        groupRelationshipRepository.deleteGroupRelationshipByRecommendedGroupAndUser(group, groupMember.getStudent());

        return groupRepository.getGroupById(groupId);
    }

    @Override
    public List<SimpleStudentDto> loadStudents(long id, long jwtId) throws NotAuthorizedException {
        var group = groupRepository.getGroupById(id);
        if (!isGroupLeader(group, jwtId)) {
            throw new NotAuthorizedException("Not authoritzed to perform this action");
        }
        List<GroupRelationship> relationships = groupRelationshipRepository.getAllStudentsByGroupAndStatus(RelStatus.LIKED, group);
        return customMatchMapper.groupRelsToSimpleStudents(relationships);
    }

    private boolean isGroupLeader(Group group, long id) {
        return group.getGroupLeaderId() == id;
    }

    private GroupMember createGroupMember(Group group, long id) {
        studentValidator.validateExists(id);
        Student member = studentService.findStudentById(id);
        return new GroupMember(group, member);
    }

    private GroupRelationship changeGroupRelationship(Group group, Student member, RelStatus relStatus) {
        GroupRelationship groupRelationship = groupRelationshipRepository.findGroupRelationshipByRecommendedGroupAndUser(group, member);
        groupRelationship.setStatus(relStatus);
        return groupRelationship;
    }


    @Override
    @Transactional
    public Group leaveGroup(long groupId, long userId) throws NotFoundException {
        LOGGER.trace("Remove student {} from group {}", userId, groupId);

        Group group = findGroupById(groupId);
        Student member = studentService.findStudentById(userId);

        if (group == null || member == null) {
            throw new NotFoundException("Group or Student not found");
        }

        GroupMember groupMemberToRemove = group.getMembers().stream()
            .filter(groupMember -> groupMember.getStudent().getId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Student is not part of this group"));

        // Check if the member to remove is the group leader
        if (group.getGroupLeaderId().equals(groupMemberToRemove.getId())) {
            group.getMembers().remove(groupMemberToRemove);
            groupMemberRepository.delete(groupMemberToRemove);

            // Check if the group is now empty
            if (group.getMembers().isEmpty()) {
                groupRepository.delete(group);
                return null; // Or throw an exception, or return a response entity, depends on your design
            } else {
                // Set a new group leader randomly
                int randomMemberIndex = new Random().nextInt(group.getMembers().size());
                GroupMember newLeader = group.getMembers().get(randomMemberIndex);
                group.setGroupLeaderId(newLeader.getId());
            }
        } else {
            group.getMembers().remove(groupMemberToRemove);
            groupMemberRepository.delete(groupMemberToRemove);
        }

        return groupRepository.save(group);
    }


}