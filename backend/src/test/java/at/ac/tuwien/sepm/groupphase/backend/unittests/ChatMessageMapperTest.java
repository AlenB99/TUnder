package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ChatMessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ChatMessageMapperTest {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    private final Timestamp timestamp = Timestamp.from(Instant.now());

    private final ChatMessageDto individualDto = new ChatMessageDto()
        .setChatType(ChatType.INDIVIDUAL_CHAT)
        .setSender(2L)
        .setReceiver(3L)
        .setTimestamp(timestamp)
        .setContent("Just an individual message.");
    private final ChatMessageDto groupDto = new ChatMessageDto()
        .setChatType(ChatType.GROUP_CHAT)
        .setSender(3L)
        .setReceiver(2L)
        .setTimestamp(timestamp)
        .setContent("Just a group message.");
    private final IndividualMessage individualMessage = new IndividualMessage()
        .setSender(Student.StudentBuilder.aStudent().withId(2L).build())
        .setRelationship(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship().withId(3L).build())
        .setTimestamp(timestamp)
        .setContent("Just an individual message.");
    private final GroupMessage groupMessage = new GroupMessage()
        .setSender(Student.StudentBuilder.aStudent().withId(3L).build())
        .setReceiver(Group.GroupBuilder.aGroup().withId(2L).build())
        .setTimestamp(timestamp)
        .setContent("Just a group message.");

    @Test
    public void messageDtoToIndividualMessage_returnsCorrectEntity() {
        var result = chatMessageMapper.fromMessageToDto(individualMessage);
        checkEquals(individualDto, result);
    }

    @Test
    public void messageDtoToGroupMessage_returnsCorrectEntity() {
        var result = chatMessageMapper.fromMessageToDto(groupMessage);
        checkEquals(groupDto, result);
    }

    @Test
    public void individualMessageToDto_returnsCorrectDto() {
        var result = chatMessageMapper.messageDtoToIndividualEntity(individualDto);
        checkEquals(individualMessage, result);
    }

    @Test
    public void groupMessageToDto_returnsCorrectDto() {
        var result = chatMessageMapper.messageDtoToGroupEntity(groupDto);
        this.checkEquals(groupMessage, result);
    }

    @Test
    public void individualMessageDto_shouldThrowException() {
        assertThrows(AssertionError.class, () -> chatMessageMapper.messageDtoToIndividualEntity(new ChatMessageDto().setChatType(ChatType.GROUP_CHAT)));
    }

    @Test
    public void groupMessageDto_shouldThrowException() {
        assertThrows(AssertionError.class, () -> chatMessageMapper.messageDtoToGroupEntity(new ChatMessageDto().setChatType(ChatType.INDIVIDUAL_CHAT)));
    }

    private void checkEquals(ChatMessageDto expected, ChatMessageDto actual) {
        assertEquals(expected.getChatType(), actual.getChatType());
        assertEquals(expected.getSender(), actual.getSender());
        assertEquals(expected.getReceiver(), actual.getReceiver());
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertEquals(expected.getContent(), actual.getContent());
    }

    private void checkEquals(IndividualMessage expected, IndividualMessage actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSender(), actual.getSender());
        assertEquals(expected.getRelationship().getId(), actual.getRelationship().getId());
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertEquals(expected.getContent(), actual.getContent());
    }

    private void checkEquals(GroupMessage expected, GroupMessage actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSender().getId(), actual.getSender().getId());
        assertEquals(expected.getReceiver().getId(), actual.getReceiver().getId());
        assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertEquals(expected.getContent(), actual.getContent());
    }

}
