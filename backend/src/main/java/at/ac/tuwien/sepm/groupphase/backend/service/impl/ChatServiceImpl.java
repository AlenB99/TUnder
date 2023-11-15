package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ChatDetailMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ChatMessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMessage;
import at.ac.tuwien.sepm.groupphase.backend.entity.IndividualMessage;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMemberRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.IndividualMessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ChatService;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SingleRelationshipRepository singleRelationshipRepository;
    private final StudentRepository studentRepository;
    private final IndividualMessageRepository individualMessageRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final StudentMapper studentMapper;
    private final ChatDetailMapper chatDetailMapper;
    private final ChatMessageMapper chatMessageMapper;

    public ChatServiceImpl(GroupRepository groupRepository,
                           GroupMemberRepository groupMemberRepository,
                           SingleRelationshipRepository singleRelationshipRepository,
                           StudentRepository studentRepository,
                           IndividualMessageRepository individualMessageRepository,
                           GroupMessageRepository groupMessageRepository,
                           StudentMapper studentMapper,
                           ChatDetailMapper chatDetailMapper,
                           ChatMessageMapper chatMessageMapper) {

        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.singleRelationshipRepository = singleRelationshipRepository;
        this.studentRepository = studentRepository;
        this.individualMessageRepository = individualMessageRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.studentMapper = studentMapper;
        this.chatDetailMapper = chatDetailMapper;
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public List<SimpleStudentDto> handleMessageAndGetReceivers(ChatMessageDto messageDto) throws NotAuthorizedException {
        LOGGER.trace("getReceivers for {} with id {} from sender id {}", messageDto.getChatType(), messageDto.getReceiver(), messageDto.getSender());

        return switch (messageDto.getChatType()) {
            case GROUP_CHAT -> handleAndGetReceiversForGroupChat(messageDto);
            case INDIVIDUAL_CHAT -> handleAndGetReceiversForIndividualChat(messageDto);
        };
    }

    @Override
    public List<ChatDetailDto> getChats(String username) {
        LOGGER.trace("getChats({})", username);

        Student student = studentRepository.findStudentByEmail(username);
        // get group chats
        List<Group> groups = groupMemberRepository.getGroupMembersByStudent(student).stream().map(GroupMember::getStudyGroup).toList();
        List<ChatMessageDto> groupMessages = groupMessageRepository.getGroupMessagesByReceiverInAndTimestampBeforeOrderByTimestampDesc(groups, Timestamp.from(Instant.now())).stream().map(chatMessageMapper::fromMessageToDto).toList();
        Stream<ChatDetailDto> groupDetails = groups.stream().map(g -> chatDetailMapper.mapToChatDetailDto(g, groupMessages));

        // get individual chats
        List<SingleRelationship> relationships = singleRelationshipRepository.findRelationshipsByUserIdsAndStatus(RelStatus.MATCHED, student);
        List<ChatMessageDto> individualMessages = individualMessageRepository.getLatestMessagePerRelationship(relationships, Timestamp.from(Instant.now()))
            .stream().map(chatMessageMapper::fromMessageToDto).toList();
        Stream<ChatDetailDto> individualDetails = relationships.stream().map(r -> chatDetailMapper.mapToChatDetailDto(r, student, individualMessages));

        Comparator<ChatDetailDto> comparator = Comparator
            .comparing(m -> m.getLastMessage() != null ? m.getLastMessage().getTimestamp() : Timestamp.from(Instant.MAX));
        return Stream.concat(groupDetails, individualDetails)
            .sorted(comparator.reversed())
            .toList();
    }

    @Override
    public List<ChatMessageDto> getHistoryForGroup(String username, long id, Timestamp timestamp, int limit) throws NotAuthorizedException {
        Student sender = studentRepository.findStudentByEmail(username);
        Group receiver = Group.GroupBuilder.aGroup().withId(id).build();
        if (!groupMemberRepository.existsGroupMemberByStudentAndStudyGroup(sender, receiver)) {
            throw new NotAuthorizedException("You are not a member of this group");
        }

        List<GroupMessage> messages = groupMessageRepository.getGroupMessagesByReceiverAndTimestampBeforeOrderByTimestampDesc(receiver, timestamp, PageRequest.of(0, limit));
        return messages.stream().map(chatMessageMapper::fromMessageToDto).toList();
    }

    @Override
    public List<ChatMessageDto> getHistoryForIndividual(String username, long id, Timestamp timestamp, int limit) throws NotAuthorizedException {
        Optional<SingleRelationship> relationship = singleRelationshipRepository.findById(id);
        if (relationship.isEmpty() || (!Objects.equals(relationship.get().getUser().getEmail(), username) && !Objects.equals(relationship.get().getRecommended().getEmail(), username))) {
            throw new NotAuthorizedException("You are not matched");
        }

        List<IndividualMessage> messages = individualMessageRepository.getIndividualMessagesByRelationshipAndTimestampBeforeOrderByTimestampDesc(relationship.get(), timestamp, PageRequest.of(0, limit));
        return messages.stream().map(chatMessageMapper::fromMessageToDto).toList();
    }

    private List<SimpleStudentDto> handleAndGetReceiversForGroupChat(ChatMessageDto message) throws NotAuthorizedException {
        Long groupChatId = message.getReceiver();
        Long senderId = message.getSender();
        Group g = groupRepository.getGroupById(groupChatId);
        if (g == null) {
            throw new NotFoundException("The specified group does not exist.");
        }

        List<SimpleStudentDto> receivers =  studentMapper.studentToSimpleStudentDtoList(g.getMembers().stream().distinct().map(GroupMember::getStudent).toList());
        if (receivers.stream().map(SimpleStudentDto::id).anyMatch(i -> Objects.equals(i, senderId))) {
            groupMessageRepository.save(chatMessageMapper.messageDtoToGroupEntity(message));
            return receivers;
        }

        Student sender = studentRepository.findStudentById(senderId);
        if (sender != null) {
            throw new NotAuthorizedException(String.format("%s is not a member of the group %s with id %d", sender.getEmail(), g.getName(), g.getId()));
        }
        throw new NotFoundException("The specified sender does not exist.");
    }

    private List<SimpleStudentDto> handleAndGetReceiversForIndividualChat(ChatMessageDto message) throws NotAuthorizedException {
        Long individualChatId = message.getReceiver();
        Long sender = message.getSender();
        SingleRelationship relationship = singleRelationshipRepository.getSingleRelationshipById(individualChatId);
        if (relationship == null
            || (!Objects.equals(relationship.getUserId(), sender) && !Objects.equals(relationship.getRecommendedId(), sender))) {
            throw new NotAuthorizedException("The specified channel does not exist.");
        }
        if (relationship.getStatus() != RelStatus.MATCHED) {
            Student receiver = Objects.equals(relationship.getUserId(), sender) ? relationship.getRecommended() : relationship.getUser();
            throw new NotAuthorizedException(String.format("%s is not a match", receiver.getEmail()));
        }

        individualMessageRepository.save(chatMessageMapper.messageDtoToIndividualEntity(message));
        return Arrays.asList(
            studentMapper.studentToSimpleStudentDto(relationship.getUser()),
            studentMapper.studentToSimpleStudentDto(relationship.getRecommended()));
    }
}
