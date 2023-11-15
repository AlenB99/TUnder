package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("webSocketData")
public class ChatWebSocketTest implements TestData {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SingleRelationshipRepository singleRelationshipRepository;

    private WebSocketConnectionHandler test2;
    private WebSocketConnectionHandler test3;
    private WebSocketConnectionHandler test6;

    private final int WAIT_FOR_WEBSOCKET = 1500;

    @BeforeEach
    public void beforeTest() throws Exception {
        WebSocketClient client2 = new StandardWebSocketClient();
        test2 = new WebSocketConnectionHandler(objectMapper);
        client2.execute(test2, getUri()).get(10, TimeUnit.SECONDS);
        test2.sendMessage(jwtTokenizer.getAuthToken(DEFAULT_USER2, USER_ROLES, 2L));

        WebSocketClient client3 = new StandardWebSocketClient();
        test3 = new WebSocketConnectionHandler(objectMapper);
        client3.execute(test3, getUri()).get(10, TimeUnit.SECONDS);
        test3.sendMessage(jwtTokenizer.getAuthToken(DEFAULT_USER3, USER_ROLES, 3L));

        WebSocketClient client6 = new StandardWebSocketClient();
        test6 = new WebSocketConnectionHandler(objectMapper);
        client6.execute(test6, getUri()).get(10, TimeUnit.SECONDS);
        test6.sendMessage(jwtTokenizer.getAuthToken(DEFAULT_USER6, USER_ROLES, 6L));

        Thread.sleep(WAIT_FOR_WEBSOCKET / 2);  // wait for auth process

        assertTrue(test2.isOpened());
        assertTrue(test3.isOpened());
        assertTrue(test6.isOpened());
    }

    @AfterEach
    public void afterTest() throws IOException {
        test2.close();
        test3.close();
        test6.close();
    }

    @Test
    public void sendSingleGroupMessage_othersShouldReceiveOne() throws Exception {
        Long groupId = groupRepository.getGroupByName("mod1").getId();
        ChatMessageDto chatMessageDto = new ChatMessageDto()
            .setReceiver(groupId)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test");

        test2.sendMessage(chatMessageDto);
        Thread.sleep(WAIT_FOR_WEBSOCKET);  // wait to receive messages

        assertEquals(1, test2.getReceivedMessages().size());
        assertEquals(1, test3.getReceivedMessages().size());
        assertEquals(1, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void sendMultipleMessages_othersShouldReceiveAll() throws Exception {
        Long groupId = groupRepository.getGroupByName("mod1").getId();
        int messages = 50;
        for (int i = 1; i <= messages; i++) {
            ChatMessageDto chatMessageDto = new ChatMessageDto()
                .setReceiver(groupId)
                .setChatType(ChatType.GROUP_CHAT)
                .setContent("Test " + i);

            test2.sendMessage(chatMessageDto);
            Thread.sleep(WAIT_FOR_WEBSOCKET / messages * 5);
        }

        Thread.sleep(WAIT_FOR_WEBSOCKET * 5);  // wait to receive messages

        assertEquals(messages, test2.getReceivedMessages().size());
        assertEquals(messages, test3.getReceivedMessages().size());
        assertEquals(messages, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void sendToMod2_onlyMod2ShouldReceive() throws Exception {
        Long groupId = groupRepository.getGroupByName("mod2").getId();
        ChatMessageDto chatMessageDto = new ChatMessageDto()
            .setReceiver(groupId)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test");

        test2.sendMessage(chatMessageDto);
        Thread.sleep(WAIT_FOR_WEBSOCKET);  // wait to receive messages

        assertEquals(1, test2.getReceivedMessages().size());
        assertEquals(0, test3.getReceivedMessages().size());
        assertEquals(1, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void sendToMod3_onlyMod3ShouldReceive() throws Exception {
        Long groupId = groupRepository.getGroupByName("mod3").getId();
        ChatMessageDto chatMessageDto = new ChatMessageDto()
            .setReceiver(groupId)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test");

        test3.sendMessage(chatMessageDto);
        Thread.sleep(WAIT_FOR_WEBSOCKET);  // wait to receive messages

        assertEquals(0, test2.getReceivedMessages().size());
        assertEquals(1, test3.getReceivedMessages().size());
        assertEquals(1, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }


    @Test
    public void test6SendToAllGroups_allShouldReceiveSomething() throws Exception {
        Long groupAllId = groupRepository.getGroupByName("mod1").getId();
        ChatMessageDto chatMessageDto1 = new ChatMessageDto()
            .setReceiver(groupAllId)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test1");

        test6.sendMessage(chatMessageDto1);


        Long groupMod2Id = groupRepository.getGroupByName("mod2").getId();
        ChatMessageDto chatMessageDto2 = new ChatMessageDto()
            .setReceiver(groupMod2Id)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test2");

        test6.sendMessage(chatMessageDto2);


        Long groupMod3Id = groupRepository.getGroupByName("mod3").getId();
        ChatMessageDto chatMessageDto3 = new ChatMessageDto()
            .setReceiver(groupMod3Id)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test3");

        test6.sendMessage(chatMessageDto3);
        Thread.sleep(WAIT_FOR_WEBSOCKET);  // wait to receive messages

        assertEquals(2, test2.getReceivedMessages().size());
        assertEquals(2, test3.getReceivedMessages().size());
        assertEquals(3, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void nonMemberSendsToGroup_shouldReceiveErrorMessage() throws Exception {
        ChatMessageDto chatMessageDto = new ChatMessageDto()
            .setReceiver(2L)
            .setChatType(ChatType.GROUP_CHAT)
            .setContent("Test");

        test3.sendMessage(chatMessageDto);
        Thread.sleep(WAIT_FOR_WEBSOCKET);

        assertEquals(0, test2.getReceivedMessages().size());
        assertEquals(1, test3.getReceivedMessages().size());
        assertEquals(0, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertTrue(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void sendToMatch_bothShouldReceiveMessage() throws Exception {
        var s2 = Student.StudentBuilder.aStudent().withId(2L).build();
        var s3 = Student.StudentBuilder.aStudent().withId(3L).build();
        var match = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, s2, s3);
        ChatMessageDto chatMessageDto = new ChatMessageDto()
            .setReceiver(match.getId())
            .setChatType(ChatType.INDIVIDUAL_CHAT)
            .setContent("Test");

        test2.sendMessage(chatMessageDto);
        Thread.sleep(WAIT_FOR_WEBSOCKET);  // wait to receive messages

        assertEquals(1, test2.getReceivedMessages().size());
        assertEquals(1, test3.getReceivedMessages().size());
        assertEquals(0, test6.getReceivedMessages().size());
        assertFalse(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void sendToNonMatch_shouldReceiveErrorMessage() throws Exception {
        ChatMessageDto chatMessageDto = new ChatMessageDto()
            .setReceiver(Long.MAX_VALUE)
            .setChatType(ChatType.INDIVIDUAL_CHAT)
            .setContent("Test");

        test2.sendMessage(chatMessageDto);
        Thread.sleep(WAIT_FOR_WEBSOCKET);  // wait to receive messages

        assertEquals(1, test2.getReceivedMessages().size());
        assertEquals(0, test3.getReceivedMessages().size());
        assertEquals(0, test6.getReceivedMessages().size());
        assertTrue(test2.hasErrors());
        assertFalse(test3.hasErrors());
        assertFalse(test6.hasErrors());
    }

    @Test
    public void sendBinaryData_socketShouldDisconnect() throws Exception {
        test2.sendMessage(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        Thread.sleep(WAIT_FOR_WEBSOCKET);
        assertFalse(test2.isOpened());
    }

    @Test
    public void sendWrongData_socketShouldDisconnect() throws Exception {
        test2.sendMessage("Just a chat message, not a ChatMessageDto");
        Thread.sleep(WAIT_FOR_WEBSOCKET);
        assertFalse(test2.isOpened());
    }

    @Test
    public void withoutJwt_socketShouldDisconnect() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionHandler handler = new WebSocketConnectionHandler(objectMapper);
        client.execute(handler, getUri()).get(10, TimeUnit.SECONDS);

        assertTrue(handler.isOpened());
        handler.sendMessage(new ChatMessageDto().setContent("Just a message without authentication"));
        Thread.sleep(WAIT_FOR_WEBSOCKET);
        assertFalse(handler.isOpened());
    }


    private String getUri() {
        return "ws://localhost:" + port + "/ws";
    }


    private static class WebSocketConnectionHandler extends AbstractWebSocketHandler {

        private final ObjectMapper objectMapper;
        private final ArrayList<ChatMessageDto> receivedMessages = new ArrayList<>();
        private WebSocketSession session;
        private boolean error = false;

        public WebSocketConnectionHandler(ObjectMapper objectMapper) {

            this.objectMapper = objectMapper;
        }

        @Override
        public void afterConnectionEstablished(@NonNull WebSocketSession session) {
            this.session = session;
        }

        @Override
        protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws JsonProcessingException {
            ChatMessageDto chatMessage = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
            if (chatMessage.getSender() == null) {
                error = true;
            }

            receivedMessages.add(chatMessage);
        }

        @Override
        public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
            error = true;
        }

        public void sendMessage(ChatMessageDto chatMessage) throws Exception {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }

        public void sendMessage(String chatMessage) throws Exception {
            session.sendMessage(new TextMessage(chatMessage));
        }

        public void sendMessage(byte[] chatMessage) throws Exception {
            session.sendMessage(new BinaryMessage(chatMessage));
        }

        public ArrayList<ChatMessageDto> getReceivedMessages() {
            return receivedMessages;
        }

        public void close() throws IOException {
            session.close();
        }

        public boolean hasErrors() {
            return error;
        }

        public boolean isOpened() {
            return session.isOpen();
        }
    }

}
