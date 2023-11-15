package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.ChatService;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class ChatServiceTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private SingleRelationshipRepository singleRelationshipRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private IndividualMessageRepository individualMessageRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ChatService chatService;

    private ChatMessageDto groupMessage;
    private List<SimpleStudentDto> groupReceivers;
    private ChatMessageDto individualMessage;
    private List<SimpleStudentDto> individualReceivers;
    private ChatMessageDto invalidGroupMessage;
    private ChatMessageDto nonExistentGroupMessage;
    private ChatMessageDto nonExistentSenderGroupMessage;
    private ChatMessageDto invalidIndividualMessage;
    private List<ChatDetailDto> student1Dto;
    private List<ChatDetailDto> student3Dto;

    @BeforeEach
    public void beforeEach() {

        Student student1 = studentRepository.save(Student.StudentBuilder.aStudent()
            .withId(1L)
            .withEmail("e00000001@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withFirstName("Alice")
            .withLastName("A")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build());
        Student student2 = studentRepository.save(Student.StudentBuilder.aStudent()
            .withId(2L)
            .withEmail("e00000002@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 2, 2))
            .withFirstName("Bob")
            .withLastName("B")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build());
        Student student3 = studentRepository.save(Student.StudentBuilder.aStudent()
            .withId(3L)
            .withEmail("e00000003@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 3, 3))
            .withFirstName("Charles")
            .withLastName("C")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build());
        Student student4 = studentRepository.save(Student.StudentBuilder.aStudent()
            .withId(4L)
            .withEmail("e00000004@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 4, 4))
            .withFirstName("Dora")
            .withLastName("D")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build());

        Group group = groupRepository.save(Group.GroupBuilder.aGroup()
            .withName("Group Test")
            .build());

        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(student1).withStudyGroup(group).build());
        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(student2).withStudyGroup(group).build());
        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(student4).withStudyGroup(group).build());

        SingleRelationship rel1 = singleRelationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withUser(student1).withRecommended(student2).withStatus(RelStatus.MATCHED).build());
        SingleRelationship rel2 = singleRelationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withUser(student1).withRecommended(student4).withStatus(RelStatus.MATCHED).build());

        groupMessage = new ChatMessageDto()
            .setSender(student1.getId())
            .setReceiver(group.getId())
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Message")
            .setTimestamp(Timestamp.from(Instant.now()));
        groupReceivers = Stream.of(student1, student2, student4).map(studentMapper::studentToSimpleStudentDto).toList();

        individualMessage = new ChatMessageDto()
            .setSender(student1.getId())
            .setReceiver(rel1.getId())
            .setChatType(ChatType.INDIVIDUAL_CHAT)
            .setContent("Message")
            .setTimestamp(Timestamp.from(Instant.now()));
        individualReceivers = Stream.of(student1, student2).map(studentMapper::studentToSimpleStudentDto).toList();

        nonExistentGroupMessage =  new ChatMessageDto()
            .setSender(Long.MAX_VALUE)
            .setReceiver(student1.getId())
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Message")
            .setTimestamp(Timestamp.from(Instant.now()));

        nonExistentSenderGroupMessage =  new ChatMessageDto()
            .setSender(group.getId())
            .setReceiver(Long.MAX_VALUE)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Message")
            .setTimestamp(Timestamp.from(Instant.now()));

        invalidGroupMessage = new ChatMessageDto()
            .setSender(student3.getId())
            .setReceiver(group.getId())
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Message")
            .setTimestamp(Timestamp.from(Instant.now()));

        invalidIndividualMessage = new ChatMessageDto()
            .setSender(student3.getId())
            .setReceiver(rel1.getId())
            .setChatType(ChatType.INDIVIDUAL_CHAT)
            .setContent("Message")
            .setTimestamp(Timestamp.from(Instant.now()));

        this.student1Dto = Arrays.asList(new ChatDetailDto()
                .setId(group.getId())
                .setType(ChatType.GROUP_CHAT)
                .setName(group.getName())
                .setMembers(Stream.of(student1, student2, student4).map(s -> studentMapper.studentToDetailedStudentDto(s)).toList()),
            new ChatDetailDto()
                .setId(rel1.getId())
                .setType(ChatType.INDIVIDUAL_CHAT)
                .setName(rel1.getRecommended().getFirstName())
                .setMembers(Stream.of(student1, student2).map(s -> studentMapper.studentToDetailedStudentDto(s)).toList()),
            new ChatDetailDto()
                .setId(rel2.getId())
                .setType(ChatType.INDIVIDUAL_CHAT)
                .setName(rel2.getRecommended().getFirstName())
                .setMembers(Stream.of(student1, student4).map(s -> studentMapper.studentToDetailedStudentDto(s)).toList())
        );

        this.student3Dto = Collections.emptyList();
    }

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        individualMessageRepository.deleteAll();
        groupMessageRepository.deleteAll();
        singleRelationshipRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    public void getReceivers_forGroup_shouldReturnCorrectReceivers() throws NotAuthorizedException {
        List<SimpleStudentDto> result = chatService.handleMessageAndGetReceivers(groupMessage);
        assertEquals(groupReceivers, result);
    }

    @Test
    public void getReceivers_forIndividual_shouldReturnCorrectReceivers() throws NotAuthorizedException {
        List<SimpleStudentDto> result = chatService.handleMessageAndGetReceivers(individualMessage);
        assertEquals(individualReceivers, result);
    }

    @Test
    public void getReceivers_forNonMember_shouldThrowNotAuthorized() {
        assertThrows(NotAuthorizedException.class, () -> chatService.handleMessageAndGetReceivers(invalidGroupMessage));
    }

    @Test
    public void getReceivers_forNonExistingGroup_shouldThrowNotAuthorized() {
        assertThrows(NotFoundException.class, () -> chatService.handleMessageAndGetReceivers(nonExistentGroupMessage));
    }

    @Test
    public void getReceivers_forNonExistingStudent_shouldThrowNotAuthorized() {
        assertThrows(NotFoundException.class, () -> chatService.handleMessageAndGetReceivers(nonExistentSenderGroupMessage));
    }

    @Test
    public void getReceivers_forNonMatch_shouldThrowNotAuthorized() {
        assertThrows(NotAuthorizedException.class, () -> chatService.handleMessageAndGetReceivers(invalidIndividualMessage));
    }

    @Test
    public void getChats_forStudent1_shouldReturnCorrectChatDetails() {
        List<ChatDetailDto> result = chatService.getChats("e00000001@student.tuwien.ac.at");
        assertEquals(student1Dto.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(student1Dto.get(i).getId(), result.get(i).getId());
            assertEquals(student1Dto.get(i).getType(), result.get(i).getType());
            assertEquals(student1Dto.get(i).getName(), result.get(i).getName());
            assertEquals(student1Dto.get(i).getMembers(), result.get(i).getMembers());
        }
    }

    @Test
    public void getChats_forStudent3_shouldReturnEmptyList() {
        List<ChatDetailDto> result = chatService.getChats("e00000003@student.tuwien.ac.at");
        assertEquals(student3Dto, result);
    }

}
