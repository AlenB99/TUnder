package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.config.EncoderConfig;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMemberRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LanguageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.Gender;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Profile("webSocketData")
@Component
public class ChatWebSocketTestDataGenerator {
    static PasswordEncoder passwordEncoder = EncoderConfig.passwordEncoder();

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_STUDENTS_TO_GENERATE = 50;
    private static final int[] GROUPS_TO_GENERATE = new int[] { 1, 2, 3, 4, 5, 6 };
    private static final String TEST_PASSWORD = passwordEncoder.encode("password");
    private static final String TEST_EMAIL = "e%08d@student.tuwien.ac.at";
    private static final String TEST_FIRST_NAME = "Alfons";
    private static final String TEST_LAST_NAME = "Müller";
    private static final String TEST_DESCRIPTION = "This is a description made for testing the backend and its capabilities";
    private static final Lva TEST_LVA_1 = new Lva("123.04", "TEST_LVA_1", null, null);
    private static final Lva TEST_LVA_2 = new Lva("123.05", "TEST_LVA_2", null, null);
    private static final Lva TEST_LVA_3 = new Lva("123.06", "TEST_LVA_3", null, null);


    private final StudentRepository studentRepository;
    private final LvaRepository lvaRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SingleRelationshipRepository singleRelationshipRepository;
    private final LanguageRepository languageRepository;
    private List<Language> allLanguages;

    public ChatWebSocketTestDataGenerator(StudentRepository studentRepository,
                                LvaRepository lvaRepository,
                                GroupRepository groupRepository,
                                GroupMemberRepository groupMemberRepository,
                                SingleRelationshipRepository singleRelationshipRepository,
                                LanguageRepository languageRepository) {
        this.studentRepository = studentRepository;
        this.lvaRepository = lvaRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.singleRelationshipRepository = singleRelationshipRepository;
        this.languageRepository = languageRepository;
    }

    @PostConstruct
    private void generateData() throws IOException {

        generateLanguages();
        allLanguages = languageRepository.findAll();

        generateLvas();
        generateStudents();
        generateGroups();
        generateMatches();
    }

    private void generateLanguages() throws IOException {
        if (languageRepository.findAll().size() > 0) {
            return;
        }

        File fileObj = new File("src/main/resources/languages.JSON");
        Map<String, String> result = new ObjectMapper().readValue(fileObj, HashMap.class);
        for (Map.Entry<String, String> entry : result.entrySet()) {
            Language l = new Language(entry.getKey(), entry.getValue());
            languageRepository.save(l);
        }
    }

    private void generateLvas() {
        if (lvaRepository.findAll().size() > 0) {
            LOGGER.debug("lva already generated");
        } else {
            lvaRepository.save(TEST_LVA_1);
            lvaRepository.save(TEST_LVA_2);
            lvaRepository.save(TEST_LVA_3);
        }
    }

    private void generateStudents() {
        if (studentRepository.findAll().size() > 0) {
            LOGGER.debug("student already generated");
        } else {
            LOGGER.debug("generating {} student entries", NUMBER_OF_STUDENTS_TO_GENERATE);

            LinkedList<String> idArr = new LinkedList<>();
            idArr.add("123.04");
            idArr.add("123.05");
            for (int i = 1; i <= NUMBER_OF_STUDENTS_TO_GENERATE; i++) {
                String email = String.format(TEST_EMAIL, i);
                Student student = Student.StudentBuilder.aStudent()
                    .withEmail(email)
                    .withPassword(TEST_PASSWORD)
                    .withFirstName(TEST_FIRST_NAME + " " + i)
                    .withLastName(TEST_LAST_NAME + " " + i)
                    .withDateOfBirth(LocalDate.now())
                    .withAdmin(true)
                    .withMeetsIrl(true)
                    .withDescription(TEST_DESCRIPTION + " " + i)
                    .withGender(Gender.MALE)
                    .withPrefLanguage(allLanguages.get(0))
                    .withCurrentLvas(lvaRepository.findAllById(idArr))
                    .withCompletedLvas(lvaRepository.findAllById(idArr))
                    .withEnabled(true)
                    .build();
                LOGGER.debug("saving student {}", student);
                studentRepository.save(student);
            }
        }
    }

    private void generateGroups() {
        if (groupRepository.findAll().size() > 0) {
            LOGGER.debug("groups already generated");
        } else {
            LOGGER.debug("generating {} group entries", GROUPS_TO_GENERATE.length);

            var students = studentRepository.findAll();
            for (int group : GROUPS_TO_GENERATE) {
                Group generated = groupRepository.save(Group.GroupBuilder.aGroup().withName("mod" + group).build());
                for (int i = 1; i < students.size(); i++) {
                    if (i % group != 0) {
                        continue;
                    }
                    Student s = studentRepository.findStudentById(i);
                    groupMemberRepository.save(GroupMember.GroupMemberBuilder.aGroupMember()
                        .withStudyGroup(generated)
                        .withStudent(s)
                        .build());
                }
            }
        }
    }

    private void generateMatches() {
        if (singleRelationshipRepository.findAll().size() > 0) {
            LOGGER.debug("relationships already generated");
        } else {
            LOGGER.debug("generating relationships");

            var students = studentRepository.findAll();
            for (int i = 2; i < students.size(); i++) {
                singleRelationshipRepository.save(new SingleRelationship(
                    Student.StudentBuilder.aStudent().withId(i - 1L).build(),
                    Student.StudentBuilder.aStudent().withId((long) i).build(),
                    RelStatus.MATCHED));
            }
        }
    }


}
