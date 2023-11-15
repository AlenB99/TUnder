package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.IndividualMessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepm.groupphase.backend.service.RankService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.MatchServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.SingleRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceImplTest {

    @Mock
    private SingleRelationshipRepository singleRelationshipRepository;

    @Mock
    private GroupRelationshipRepository groupRelationshipRepository;

    @Mock
    private SingleRelationshipValidator singleRelationshipValidator;

    @Mock
    private GroupRelationshipValidator groupRelationshipValidator;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CustomMatchMapper customMatchMapper;

    @Mock
    private GroupValidator groupValidator;

    @Mock
    private StudentValidator studentValidator;

    @Mock
    private RankService rankService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private IndividualMessageRepository messageRepositroy;

    private MatchServiceImpl matchService;

    @BeforeEach
    void setUp() {
        matchService = new MatchServiceImpl(singleRelationshipRepository,
            groupRelationshipRepository,
            singleRelationshipValidator,
            groupRelationshipValidator,
            studentRepository,
            customMatchMapper,
            groupValidator,
            studentValidator,
            rankService,
            emailSenderService, messageRepositroy);
    }


/*
    @Test
    void postSingleRelationship_success() throws ValidationException {
        when(matchRepository.save(any(SingleRelationship.class))).thenReturn(singleRel);

        SingleRelationship result = matchService.postSingleRelationship(singleRel);

        assertEquals(singleRel, result);
        verify(matchRepository, times(1)).save(singleRel);
    }

 */

    @Test
    public void postSingleRelationship_newRelationship_emailNotSent() throws ValidationException {
        SingleRelationship singleRelationship = new SingleRelationship();
        singleRelationship.setUser(new Student());
        singleRelationship.setRecommended(new Student());

        doNothing().when(singleRelationshipValidator).validateRelationshipForCreate(any());
        when(singleRelationshipRepository.isAlreadyStatus(RelStatus.LIKED, singleRelationship.getRecommended(), singleRelationship.getUser())).thenReturn(null);
        when(singleRelationshipRepository.getSingleRelationshipByUser(singleRelationship.getUser())).thenReturn(Collections.emptyList());
        when(singleRelationshipRepository.save(singleRelationship)).thenReturn(singleRelationship);

        SingleRelationship result = matchService.postSingleRelationship(singleRelationship);

        assertNotNull(result);
        verify(emailSenderService, times(0)).sendEmail(any());
        verify(singleRelationshipRepository, times(1)).save(singleRelationship);
    }

    @Test
    public void postGroupRelationship_validGroupRelationship_savedSuccessfully() throws ValidationException {
        GroupRelationship groupRelationship = new GroupRelationship();
        groupRelationship.setUser(new Student());

        doNothing().when(groupRelationshipValidator).validateGroupRelationshipForCreate(any());
        when(groupRelationshipRepository.findGroupRelationshipByUser(groupRelationship.getUser())).thenReturn(Collections.emptyList());
        when(groupRelationshipRepository.save(groupRelationship)).thenReturn(groupRelationship);

        GroupRelationship result = matchService.postGroupRelationship(groupRelationship);

        assertNotNull(result);
        verify(groupRelationshipRepository, times(1)).save(groupRelationship);
    }

    @Test
    public void inviteToGroup_validMatnr_inviteSent() throws ValidationException {
        String matnr = "123456";
        Long groupId = 1L;
        Student student = new Student();
        student.setEmail("test@test.com");

        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(student));
        doNothing().when(groupRelationshipValidator).validateGroupRelationshipForCreate(any());
        when(groupRelationshipRepository.save(any(GroupRelationship.class))).thenReturn(new GroupRelationship());

        GroupRelationship result = matchService.inviteToGroup(matnr, groupId);

        assertNotNull(result);
        verify(groupRelationshipRepository, times(1)).save(any());
    }

    @Test
    public void getLikes_validId_returnsLikes() {
        long id = 1L;
        Student student = new Student();
        student.setId(id);

        List<SingleRelationship> likes = new ArrayList<>();
        likes.add(new SingleRelationship());

        when(studentRepository.findStudentById(id)).thenReturn(student);
        when(singleRelationshipRepository.findLikesByUserIdAndStatus(RelStatus.LIKED, student)).thenReturn(likes);
        //when(customMatchMapper.singleRelToSimpleStudentDto(any(), any())).thenReturn(new SimpleStudentDto());

        List<SimpleStudentDto> result = matchService.getLikes(id);

        assertNotNull(result);
        assertEquals(likes.size(), result.size());
        verify(customMatchMapper, times(1)).singleRelToSimpleStudentDto(any(), any());
    }

    @Test
    public void getLikedAndMatchedStudents_validIdAndCount_returnsStudents() throws NotFoundException {
        long studentId = 1L;
        int count = 5;
        Student student = new Student();
        student.setId(studentId);

        List<Student> likedAndMatchedStudents = new ArrayList<>();
        likedAndMatchedStudents.add(new Student());

        when(studentRepository.findStudentById(studentId)).thenReturn(student);
        when(singleRelationshipRepository.getLikedAndMatchedStudents(student, PageRequest.of(0, count))).thenReturn(likedAndMatchedStudents);

        List<Student> result = matchService.getLikedAndMatchedStudents(studentId, count);

        assertNotNull(result);
        assertEquals(likedAndMatchedStudents.size(), result.size());
    }

    @Test
    public void getDislikedStudents_validId_returnsStudents() throws NotFoundException {
        long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);

        List<Student> dislikedStudents = new ArrayList<>();
        dislikedStudents.add(new Student());

        when(studentRepository.findStudentById(studentId)).thenReturn(student);
        when(singleRelationshipRepository.getDislikedStudents(student)).thenReturn(dislikedStudents);

        List<Student> result = matchService.getDislikedStudents(studentId);

        assertNotNull(result);
        assertEquals(dislikedStudents.size(), result.size());
    }
    @Test
    public void getLikedAndMatchedGroups_validIdAndCount_returnsGroups() throws NotFoundException {
        long studentId = 1L;
        int count = 5;
        Student student = new Student();
        student.setId(studentId);

        List<Group> likedAndMatchedGroups = new ArrayList<>();
        likedAndMatchedGroups.add(new Group());

        when(studentRepository.findStudentById(studentId)).thenReturn(student);
        when(groupRelationshipRepository.getLikedAndMatchedGroups(student, PageRequest.of(0, count))).thenReturn(likedAndMatchedGroups);

        List<Group> result = matchService.getLikedAndMatchedGroups(studentId, count);

        assertNotNull(result);
        assertEquals(likedAndMatchedGroups.size(), result.size());
    }




}

