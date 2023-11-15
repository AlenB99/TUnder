package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ChatDetailMapper {

    private final StudentMapper studentMapper;

    public ChatDetailMapper(StudentMapper studentMapper) {

        this.studentMapper = studentMapper;
    }

    public ChatDetailDto mapToChatDetailDto(Group g, List<ChatMessageDto> messages) {
        Optional<ChatMessageDto> latestMessage = messages.stream().filter(m -> Objects.equals(m.getReceiver(), g.getId())).findFirst();
        return new ChatDetailDto()
            .setId(g.getId())
            .setType(ChatType.GROUP_CHAT)
            .setName(g.getName())
            .setImageUrl(studentMapper.mapByteArrayToString(g.getImage()))
            .setMembers(g.getMembers().stream().map(GroupMember::getStudent).map(studentMapper::studentToDetailedStudentDto).toList())
            .setLastMessage(latestMessage.orElse(null));
    }

    public ChatDetailDto mapToChatDetailDto(SingleRelationship r, Student student, List<ChatMessageDto> messages) {
        Optional<ChatMessageDto> latestMessage = messages.stream().filter(m -> Objects.equals(m.getReceiver(), r.getId())).findFirst();
        Student other = Objects.equals(r.getUserId(), student.getId()) ? r.getRecommended() : r.getUser();
        return new ChatDetailDto()
            .setId(r.getId())
            .setType(ChatType.INDIVIDUAL_CHAT)
            .setName(other.getFirstName())
            .setImageUrl(studentMapper.mapByteArrayToString(other.getImageUrl()))
            .setMembers(Stream.of(r.getUser(), r.getRecommended()).map(studentMapper::studentToDetailedStudentDto).toList())
            .setLastMessage(latestMessage.orElse(null));
    }

}
