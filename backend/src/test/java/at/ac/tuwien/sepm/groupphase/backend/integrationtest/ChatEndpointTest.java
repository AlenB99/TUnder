package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ChatMessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.XsiNilLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ChatEndpointTest implements TestData {

    private static final int CREATE_MESSAGE_COUNT = 100;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SingleRelationshipRepository singleRelationshipRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private IndividualMessageRepository individualMessageRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    private Student a;
    private Student b;
    private Student c;
    private Group group;
    private SingleRelationship singleRelationship;
    private ChatMessageDto newestIndividualMessageDto;
    private ChatMessageDto newestGroupMessageDto;

    @BeforeEach
    public void beforeEach() throws InterruptedException {
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

        singleRelationship = singleRelationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withStatus(RelStatus.MATCHED)
            .withUser(a)
            .withRecommended(b)
            .build());

        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(a)
            .withStudyGroup(group)
            .build());
        groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
            .withStudent(c)
            .withStudyGroup(group)
            .build());

        Instant now = Instant.now();
        var newestGroupMessage = new GroupMessage();
        for (int i = 0; i < CREATE_MESSAGE_COUNT; i++) {
            now = now.plusMillis(1);
            newestGroupMessage = groupMessageRepository.save(new GroupMessage()
                .setSender(a)
                .setReceiver(group)
                .setTimestamp(Timestamp.from(now))
                .setContent("Just an old message"));
        }
        newestIndividualMessageDto = chatMessageMapper.fromMessageToDto(newestGroupMessage);

        var newestIndividualMessage = new IndividualMessage();
        for (int i = 0; i < CREATE_MESSAGE_COUNT; i++) {
            now = now.plusMillis(1);
            newestIndividualMessage = individualMessageRepository.save(new IndividualMessage()
                .setSender(b)
                .setRelationship(singleRelationship)
                .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.2"))
                .setContent("Just an old message"));
        }
        newestIndividualMessageDto = chatMessageMapper.fromMessageToDto(newestIndividualMessage);

        Thread.sleep(CREATE_MESSAGE_COUNT);
    }

    @AfterEach
    public void afterEach() {
        individualMessageRepository.deleteAll();
        singleRelationshipRepository.deleteAll();
        groupMessageRepository.deleteAll();
        groupMemberRepository.deleteAll();
        groupRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    public void list_forTest1_shouldReturnTwoChats() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(CHAT_BASE_URI + "/list")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, 1L)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatDetailDto> chatDetailDtoList = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(2, chatDetailDtoList.size());
    }

    @Test
    public void list_forNewUser_shouldReturnNoChat() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(CHAT_BASE_URI + "/list")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("new@email.com", USER_ROLES, Long.MAX_VALUE)))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatDetailDto> chatDetailDtoList = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(0, chatDetailDtoList.size());
    }

    @Test
    public void getIndividualChatMessages_shouldReturnAll() throws Exception {
        String url = String.format(CHAT_BASE_URI + "/messages/individual/%d?from=%s&limit=%d", singleRelationship.getId(), formatter.format(ZonedDateTime.now()), Integer.MAX_VALUE);
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatMessageDto> chatMessages = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(CREATE_MESSAGE_COUNT, chatMessages.size());
    }

    @Test
    public void getGroupChatMessages_shouldReturnAll() throws Exception {
        String url = String.format(CHAT_BASE_URI + "/messages/group/%d?from=%s&limit=%d", group.getId(), formatter.format(ZonedDateTime.now()), Integer.MAX_VALUE);
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatMessageDto> chatMessages = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(CREATE_MESSAGE_COUNT, chatMessages.size());
    }

    @Test
    public void getIndividualChatMessagesWithLimit_shouldReturnLimit() throws Exception {
        int limit = CREATE_MESSAGE_COUNT / 2;
        String url = String.format(CHAT_BASE_URI + "/messages/individual/%d?from=%s&limit=%d", singleRelationship.getId(), formatter.format(ZonedDateTime.now()), limit);
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatMessageDto> chatMessages = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(limit, chatMessages.size());
    }


    @Test
    public void getGroupChatMessagesWithLimit_shouldReturnLimit() throws Exception {
        int limit = CREATE_MESSAGE_COUNT / 2;
        String url = String.format(CHAT_BASE_URI + "/messages/group/%d?from=%s&limit=%d", group.getId(), formatter.format(ZonedDateTime.now()), limit);
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatMessageDto> chatMessages = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(limit, chatMessages.size());
    }

    @Test
    public void getIndividualChatMessagesWithoutParams_shouldReturnAll() throws Exception {
        String url = String.format(CHAT_BASE_URI + "/messages/individual/%d", singleRelationship.getId());
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatMessageDto> chatMessages = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(CREATE_MESSAGE_COUNT, chatMessages.size());
    }


    @Test
    public void getGroupChatMessagesWithoutParams_shouldReturnAll() throws Exception {
        String url = String.format(CHAT_BASE_URI + "/messages/group/%d", group.getId());
        MvcResult mvcResult = this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(a.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ChatMessageDto> chatMessages = objectMapper.readValue(response, new TypeReference<>() { });

        assertEquals(CREATE_MESSAGE_COUNT, chatMessages.size());
    }

    @Test
    public void getIndividualChatMessages_byNonMatch_shouldReturnUnauthorized() throws Exception {
        String url = String.format(CHAT_BASE_URI + "/messages/individual/%d", singleRelationship.getId());
        this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(c.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn();
    }

    @Test
    public void getGroupChatMessages_byNonMember_shouldReturnUnauthorized() throws Exception {
        String url = String.format(CHAT_BASE_URI + "/messages/group/%d", group.getId());
        this.mockMvc.perform(get(url)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(b.getEmail(), USER_ROLES, a.getId())))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andReturn();
    }

}
