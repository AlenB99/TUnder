package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMemberRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.GroupServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;

public class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupValidator groupValidator;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupRelationshipRepository groupRelationshipRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentValidator studentValidator;

    @InjectMocks
    private GroupServiceImpl groupService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenFindAll_thenReturnGroupList() {
        Group group = new Group();
        group.setId(1L);
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group));

        var result = groupService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void whenFindGroupById_thenReturnGroup() {
        Group group = new Group();
        group.setId(1L);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        var result = groupService.findGroupById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    public void whenPersistGroup_thenReturnSavedGroup() throws ValidationException {
        Group group = new Group();
        group.setId(1L);
        doNothing().when(groupValidator).validate(any());
        when(groupRepository.save(any())).thenReturn(group);

        var result = groupService.persistGroup(new Group());

        assertEquals(1L, result.getId());
    }

    @Test
    public void whenGetAllGroups_thenReturnAllGroups() {
        // Prepare mock data
        Group group1 = new Group();
        group1.setId(1L);

        Group group2 = new Group();
        group2.setId(2L);

        List<Group> allGroups = new ArrayList<>();
        allGroups.add(group1);
        allGroups.add(group2);

        // Configure mock behaviour
        when(groupRepository.findAll()).thenReturn(allGroups);

        // Perform the test
        List<Group> result = groupService.findAll();

        // Verify the result
        assertEquals(allGroups.size(), result.size());
    }


    @Test
    public void whenUpdateGroup_thenReturnUpdatedGroup() throws ValidationException {
        Group group = new Group();
        group.setId(1L);
        doNothing().when(groupValidator).validate(any());
        when(groupRepository.save(any())).thenReturn(group);

        var result = groupService.updateGroup(new Group());

        assertEquals(1L, result.getId());
    }@Test
    public void whenCreateGroup_thenReturnCreatedGroup() throws ValidationException {
        Group group = new Group();
        group.setId(1L);
        doNothing().when(groupValidator).validate(any());
        when(studentService.findStudentById(anyLong())).thenReturn(new Student());
        when(groupRepository.save(any())).thenReturn(group);

        var result = groupService.createGroup(new Group());

        assertEquals(1L, result.getId());
    }

    @Test
    public void whenAddStudentByMatr_thenReturnUpdatedGroup() throws NotFoundException {
        Group group = new Group();
        group.setId(1L);
        doNothing().when(studentValidator).validateExists(anyLong());
        when(studentService.findStudentById(anyLong())).thenReturn(new Student());
        when(groupRepository.save(any())).thenReturn(group);

        var result = groupService.addStudentByMatr(new Group(), 1L);

        assertEquals(1L, result.getId());
    }



    @Test
    public void whenGetGroupsByGroupLeaderId_thenReturnGroups() {
        List<Group> groups = new ArrayList<>();
        Group group1 = new Group();
        group1.setId(1L);
        groups.add(group1);

        when(groupRepository.getGroupsByGroupLeaderId(anyLong())).thenReturn(groups);

        List<Group> result = groupService.getGroupsByGroupLeaderId(1L);

        assertNotNull(result);
        assertEquals(groups.size(), result.size());
    }


    @Test
    public void whenLoadInvites_thenReturnInvites() throws NotFoundException {
        List<GroupRelationship> groupRelationships = new ArrayList<>();
        GroupRelationship groupRelationship = new GroupRelationship();
        groupRelationship.setId(1L);
        groupRelationships.add(groupRelationship);

        when(groupRelationshipRepository.findAllByUserAndStatus(any(), any())).thenReturn(groupRelationships);

        List<GroupRelationship> result = groupService.loadInvites(1L);

        assertNotNull(result);
        assertEquals(groupRelationships.size(), result.size());
    }





    @Test
    public void whenRemoveUserFromGroup_thenDoNotThrowException() throws ValidationException, NotAuthorizedException {
        // Create group member
        GroupMember groupMember = new GroupMember();
        Student student = new Student();
        student.setId(1L);
        groupMember.setStudent(student);

// Create group
        Group group = new Group();
        group.setGroupLeaderId(2L); // Assuming 2L is the ID of the leader
        group.setMembers(new ArrayList<>(Arrays.asList(groupMember))); // Create mutable list of members

// Mock dependencies
        when(groupValidator.validateExists(anyLong())).thenReturn(true);
        when(groupRepository.getGroupById(anyLong())).thenReturn(group);
        doNothing().when(groupMemberRepository).deleteUserFromGroup(anyLong(), anyLong());

// Call the method under test and assert that it does not throw an exception
        assertDoesNotThrow(() -> groupService.removeUserFromGroup(1L, 1L, 2L));
    }



    @Test
    public void whenLoadStudents_thenDoesNotThrow() throws NotAuthorizedException {
        Group group = new Group();
        group.setId(1L);
        group.setGroupLeaderId(2L);
        when(groupRepository.getGroupById(anyLong())).thenReturn(group);
        when(groupRelationshipRepository.getAllStudentsByGroupAndStatus(any(), any())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> groupService.loadStudents(1L, 2L));
    }
}