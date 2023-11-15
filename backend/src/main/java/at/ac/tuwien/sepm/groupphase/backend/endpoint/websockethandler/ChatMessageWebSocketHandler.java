package at.ac.tuwien.sepm.groupphase.backend.endpoint.websockethandler;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatMessageWebSocketHandler implements WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<Long, List<WebSocketSession>> userToSession = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Long> sessionToUser = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    public ChatMessageWebSocketHandler(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        LOGGER.trace("Received message via WebSocket {}", message);

        if (!(message instanceof TextMessage)) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        try {
            ChatMessageDto receivedMessage = objectMapper.readValue(message.getPayload().toString(), ChatMessageDto.class)
                .setSender(sessionToUser.get(session))
                .setTimestamp(Timestamp.from(Instant.now()));
            List<SimpleStudentDto> receivers = chatService.handleMessageAndGetReceivers(receivedMessage);
            TextMessage newMessage = new TextMessage(objectMapper.writeValueAsString(receivedMessage));
            for (SimpleStudentDto users : receivers) {
                List<WebSocketSession> list = userToSession.get(users.getId());
                if (list == null) {
                    continue;
                }

                for (WebSocketSession s : list) {
                    if (s != null && s.isOpen()) {
                        s.sendMessage(newMessage);
                    }
                }
            }
        } catch (NotAuthorizedException | NotFoundException ex) {
            ChatMessageDto chatMessageDto = new ChatMessageDto()
                .setContent(ex.getMessage());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
        } catch (Exception ex) {
            LOGGER.warn("Exception on message {}: \n{}\n{}", message, ex.getMessage(), ex.getStackTrace());
            ChatMessageDto chatMessageDto = new ChatMessageDto()
                .setContent(ex.getMessage());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        LOGGER.warn("A WebSocket transport error occurred: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        Long id = sessionToUser.remove(session);
        List<WebSocketSession> list = userToSession.get(id);
        if (list != null) {
            list.remove(session);
        }

        LOGGER.trace("User with id {} disconnected from WebSocket", id);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void userAuthenticated(Long id, WebSocketSession session) {
        LOGGER.trace("User with id {} successfully authenticated to WebSocket", id);
        List<WebSocketSession> list = userToSession.computeIfAbsent(id, s -> new CopyOnWriteArrayList<>());
        list.add(session);

        userToSession.put(id, list);
        sessionToUser.put(session, id);
    }
}
