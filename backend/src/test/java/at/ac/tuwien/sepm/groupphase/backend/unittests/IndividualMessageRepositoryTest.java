package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
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
public class IndividualMessageRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SingleRelationshipRepository singleRelationshipRepository;

    @Autowired
    private IndividualMessageRepository individualMessageRepository;

    private Student a;
    private Student b;
    private Student c;
    private SingleRelationship singleRelationship;
    private IndividualMessage newestMessage;

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

        singleRelationship = singleRelationshipRepository.save(SingleRelationship.SingleRelationshipBuilder.aSingleRelationship()
            .withStatus(RelStatus.MATCHED)
            .withUser(a)
            .withRecommended(b)
            .build());

        individualMessageRepository.save(new IndividualMessage()
            .setSender(a)
            .setRelationship(singleRelationship)
            .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.0"))
            .setContent("Just an old message"));

        individualMessageRepository.save(new IndividualMessage()
            .setSender(a)
            .setRelationship(singleRelationship)
            .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.1"))
            .setContent("Just an old message"));

        newestMessage = individualMessageRepository.save(new IndividualMessage()
            .setSender(b)
            .setRelationship(singleRelationship)
            .setTimestamp(Timestamp.valueOf("2023-01-01 10:00:00.2"))
            .setContent("Just an old message"));
    }

    @AfterEach
    public void afterEach() {
        individualMessageRepository.deleteAll();
        singleRelationshipRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    public void saveIndividualMessage_addsAndReturnsCorrectMessage() {
        var message = individualMessageRepository.save(new IndividualMessage()
            .setSender(a)
            .setRelationship(singleRelationship)
            .setTimestamp(Timestamp.from(Instant.now()))
            .setContent("Just a message"));
        var result = individualMessageRepository.findById(message.getId());
        assertTrue(result.isPresent());
        assertEquals(message, result.get());
    }

    @Test
    public void getMessagesAfterNow_returnsCorrectMessages() {
        var messages = individualMessageRepository.getIndividualMessagesByRelationshipAndTimestampBeforeOrderByTimestampDesc(singleRelationship, Timestamp.from(Instant.now()), PageRequest.of(0, 10));
        assertEquals(3, messages.size());
    }

    @Test
    public void getMessagesWithLimit_returnsCorrectMessages() {
        int limit = 2;
        var messages = individualMessageRepository.getIndividualMessagesByRelationshipAndTimestampBeforeOrderByTimestampDesc(singleRelationship, Timestamp.from(Instant.now()), PageRequest.of(0, limit));
        assertEquals(limit, messages.size());
    }

    @Test
    public void getMostRecentMessage_returnsCorrectMessage() {
        var messages = individualMessageRepository.getLatestMessagePerRelationship(Collections.singleton(singleRelationship), Timestamp.from(Instant.now()));
        assertEquals(1, messages.size());
        assertEquals(newestMessage, messages.get(0));
    }

}
