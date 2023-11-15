package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ChatDetailMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChatDetailMapperTest {

    @Mock
    private StudentMapper studentMapper;

    @Test
    public void groupToDto_returnsCorrectDto() {
        Student student1 = Student.StudentBuilder.aStudent()
            .withId(1L)
            .withEmail("e00000001@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withFirstName("Alice")
            .withLastName("A")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build();
        Student student2 = Student.StudentBuilder.aStudent()
            .withId(2L)
            .withEmail("e00000002@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 2, 2))
            .withFirstName("Bob")
            .withLastName("B")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build();
        Student student3 = Student.StudentBuilder.aStudent()
            .withId(3L)
            .withEmail("e00000003@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 3, 3))
            .withFirstName("Charles")
            .withLastName("C")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build();
        Student student4 = Student.StudentBuilder.aStudent()
            .withId(4L)
            .withEmail("e00000004@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 4, 4))
            .withFirstName("Dora")
            .withLastName("D")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build();

        Group group = Group.GroupBuilder.aGroup()
            .withId(1L)
            .withName("Group Test")
            .build();

        group.addGroupMember(GroupMember.GroupMemberBuilder.aGroupMember().withStudent(student1).withStudyGroup(group).build());
        group.addGroupMember(GroupMember.GroupMemberBuilder.aGroupMember().withStudent(student2).withStudyGroup(group).build());
        group.addGroupMember(GroupMember.GroupMemberBuilder.aGroupMember().withStudent(student4).withStudyGroup(group).build());

        Mockito.when(studentMapper.studentToDetailedStudentDto(Mockito.any(Student.class)))
            .thenAnswer(invocation -> {
                Student student = invocation.getArgument(0);
                return new DetailedStudentDto(
                    student.getId(),
                    student.getEmail(),
                    student.getFirstName(),
                    student.getLastName(),
                    null,  // Gender
                    null,  // Language
                    null,  // Description
                    student.getDateOfBirth(),
                    null,  // Admin
                    null,  // MeetsIrl
                    null,  // SettingsDto
                    null,  // CurrentLvas
                    null,  // CompletedLvas
                    null,  // FilterDto
                    null   // ImageUrl
                );

            });

        ChatDetailMapper chatDetailMapper = new ChatDetailMapper(studentMapper);
        ChatDetailDto expectedGroupDto = new ChatDetailDto()
            .setId(group.getId())
            .setType(ChatType.GROUP_CHAT)
            .setName(group.getName())
            .setMembers(Stream.of(student1, student2, student4).map(s -> studentMapper.studentToDetailedStudentDto(s)).toList());

        ChatDetailDto chatDetailDto = chatDetailMapper.mapToChatDetailDto(group, Collections.emptyList());

        assertEquals(expectedGroupDto.getId(), chatDetailDto.getId());
        assertEquals(expectedGroupDto.getType(), chatDetailDto.getType());
        assertEquals(expectedGroupDto.getName(), chatDetailDto.getName());

        assertEquals(expectedGroupDto.getMembers().size(), chatDetailDto.getMembers().size());
    }

    @Test
    public void singleRelationshipToDto_returnsCorrectDto() {
        Student student1 = Student.StudentBuilder.aStudent()
            .withId(1L)
            .withEmail("e00000001@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 1, 1))
            .withFirstName("Alice")
            .withLastName("A")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build();
        Student student2 = Student.StudentBuilder.aStudent()
            .withId(2L)
            .withEmail("e00000002@student.tuwien.ac.at")
            .withDateOfBirth(LocalDate.of(2000, 2, 2))
            .withFirstName("Bob")
            .withLastName("B")
            .withCurrentLvas(Collections.emptyList())
            .withCompletedLvas(Collections.emptyList())
            .build();

        SingleRelationship rel1 = SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withId(1L)
            .withUser(student1)
            .withRecommended(student2)
            .withStatus(RelStatus.MATCHED)
            .build();

        ChatDetailMapper chatDetailMapper = new ChatDetailMapper(studentMapper);
        ChatDetailDto expectedStudentDto = new ChatDetailDto()
            .setId(rel1.getId())
            .setType(ChatType.INDIVIDUAL_CHAT)
            .setName(rel1.getRecommended().getFirstName())
            .setMembers(Arrays.asList(
                new DetailedStudentDto(
                    student1.getId(),
                    student1.getEmail(),
                    student1.getFirstName(),
                    student1.getLastName(),
                    null,  // Gender
                    null,  // Language
                    null,  // Description
                    student1.getDateOfBirth(),
                    null,  // Admin
                    null,  // MeetsIrl
                    null,  // SettingsDto
                    null,  // CurrentLvas
                    null,  // CompletedLvas
                    null,  // FilterDto
                    null   // ImageUrl
                ),
                new DetailedStudentDto(
                    student2.getId(),
                    student2.getEmail(),
                    student2.getFirstName(),
                    student2.getLastName(),
                    null,  // Gender
                    null,  // Language
                    null,  // Description
                    student2.getDateOfBirth(),
                    null,  // Admin
                    null,  // MeetsIrl
                    null,  // SettingsDto
                    null,  // CurrentLvas
                    null,  // CompletedLvas
                    null,  // FilterDto
                    null   // ImageUrl
                )
            ));

        ChatDetailDto chatDetailDto = chatDetailMapper.mapToChatDetailDto(rel1, rel1.getUser(), Collections.emptyList());

        assertEquals(expectedStudentDto.getId(), chatDetailDto.getId());
        assertEquals(expectedStudentDto.getType(), chatDetailDto.getType());
        assertEquals(expectedStudentDto.getName(), chatDetailDto.getName());
        assertEquals(expectedStudentDto.getMembers().size(), chatDetailDto.getMembers().size());
    }

}

