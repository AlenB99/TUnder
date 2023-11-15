package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMessage;
import at.ac.tuwien.sepm.groupphase.backend.entity.IndividualMessage;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessageMapper() {
    }

    public GroupMessage messageDtoToGroupEntity(ChatMessageDto chatMessage) {
        assert chatMessage.getChatType() == ChatType.GROUP_CHAT;

        return new GroupMessage()
            .setReceiver(Group.GroupBuilder.aGroup().withId(chatMessage.getReceiver()).build())
            .setSender(Student.StudentBuilder.aStudent().withId(chatMessage.getSender()).build())
            .setTimestamp(chatMessage.getTimestamp())
            .setContent(chatMessage.getContent());
    }

    public IndividualMessage messageDtoToIndividualEntity(ChatMessageDto chatMessage) {
        assert chatMessage.getChatType() == ChatType.INDIVIDUAL_CHAT;

        return new IndividualMessage()
            .setSender(Student.StudentBuilder.aStudent().withId(chatMessage.getSender()).build())
            .setRelationship(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship().withId(chatMessage.getReceiver()).build())
            .setTimestamp(chatMessage.getTimestamp())
            .setContent(chatMessage.getContent());
    }

    public ChatMessageDto fromMessageToDto(IndividualMessage message) {
        return new ChatMessageDto()
            .setChatType(ChatType.INDIVIDUAL_CHAT)
            .setSender(message.getSender().getId())
            .setReceiver(message.getRelationship().getId())
            .setTimestamp(message.getTimestamp())
            .setContent(message.getContent());
    }

    public ChatMessageDto fromMessageToDto(GroupMessage message) {
        return new ChatMessageDto()
            .setChatType(ChatType.GROUP_CHAT)
            .setSender(message.getSender().getId())
            .setReceiver(message.getReceiver().getId())
            .setTimestamp(message.getTimestamp())
            .setContent(message.getContent());
    }

}
