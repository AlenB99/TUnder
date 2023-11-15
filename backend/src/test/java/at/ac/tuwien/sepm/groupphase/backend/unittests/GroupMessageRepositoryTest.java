package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class GroupMessageRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    private Student a;
    private Student b;
    private Student c;

    private Group group;

    private GroupMessage newestGroupMessage;

    @BeforeEach
    public void beforeEach() {
        a = studentRepository.save(Student.StudentBuilder.aStudent()
            .withEmail("e10000001@student.tuwien.ac.at")
            .build());
        b = studentRepository.save(Student.StudentBuilder.aStudent()
            .withEmail("e10000002@student.tuwien.ac.at")
            .build());
        c = studentRepository.save(Student.StudentBuilder.aStudent()
            .withEmail("e10000003@student.tuwien.ac.at")
            .build());

        group = groupRepository.save(Group.GroupBuilder.aGroup()
            .withName("New Group")
            .build());
        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(a)
            .withStudyGroup(group)
            .build());
        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(c)
            .withStudyGroup(group)
            .build());

        groupMessageRepository.save(new GroupMessage()
            .setSender(a)
            .setReceiver(group)
            .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.0"))
            .setContent("Just an old message"));

        groupMessageRepository.save(new GroupMessage()
            .setSender(a)
            .setReceiver(group)
            .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.1"))
            .setContent("Just an old message"));

        newestGroupMessage = groupMessageRepository.save(new GroupMessage()
            .setSender(c)
            .setReceiver(group)
            .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.2"))
            .setContent("Just an old message"));
    }

    @AfterEach
    public void afterEach() {
        groupMessageRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    public void saveGroupMessage_addsAndReturnsCorrectMessage() {
        var message = groupMessageRepository.save(new GroupMessage()
            .setSender(a)
            .setReceiver(group)
            .setTimestamp(Timestamp.from(Instant.now()))
            .setContent("Just a message"));
        var result = groupMessageRepository.findById(message.getId());
        assertTrue(result.isPresent());
        assertEquals(message, result.get());
    }

    @Test
    public void getGroupMessagesAfterNow_returnsCorrectMessages() {
        var messages = groupMessageRepository.getGroupMessagesByReceiverAndTimestampBeforeOrderByTimestampDesc(group, Timestamp.from(Instant.now()), PageRequest.of(0, 10));
        assertEquals(3, messages.size());
    }

    @Test
    public void getGroupMessagesWithLimit_returnsCorrectMessages() {
        int limit = 2;
        var messages = groupMessageRepository.getGroupMessagesByReceiverAndTimestampBeforeOrderByTimestampDesc(group, Timestamp.from(Instant.now()), PageRequest.of(0, limit));
        assertEquals(limit, messages.size());
    }

    @Test
    public void getMostRecentGroupMessage_returnsCorrectMessage() {
        var message = groupMessageRepository.getGroupMessagesByReceiverInAndTimestampBeforeOrderByTimestampDesc(Collections.singletonList(group), Timestamp.from(Instant.now()));
        assertEquals(1, message.size());
        assertEquals(newestGroupMessage, message.get(0));
    }

}
