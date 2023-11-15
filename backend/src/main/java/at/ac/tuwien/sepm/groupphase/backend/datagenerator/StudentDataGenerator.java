package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.config.EncoderConfig;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMessage;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Settings;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMemberRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupMessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LanguageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LvaRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SettingsRepository;
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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Profile("generateData")
@Component
public class StudentDataGenerator {
    static PasswordEncoder passwordEncoder = EncoderConfig.passwordEncoder();

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_STUDENTS_TO_GENERATE = 50;
    private static final int[] GROUPS_TO_GENERATE = new int[]{1, 2, 3, 4, 5, 6};
    private static final String TEST_PASSWORD = passwordEncoder.encode("password");
    private static final String TEST_EMAIL = "e%08d@student.tuwien.ac.at";
    private static List<byte[]> base64Images = new ArrayList<>();

    private final StudentRepository studentRepository;
    private final LvaRepository lvaRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SingleRelationshipRepository singleRelationshipRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final GroupRelationshipRepository groupRelationshipRepository;
    private final TestNameGenerator testNameGenerator;
    private final TestCourseGenerator testCourseGenerator;
    private final RandomDescriptionGenerator descriptionGenerator;
    private final LanguageRepository languageRepository;
    private List<Language> allLanguages;
    private final SettingsRepository settingsRepository;

    public StudentDataGenerator(StudentRepository studentRepository,
                                LvaRepository lvaRepository,
                                GroupRepository groupRepository,
                                GroupMemberRepository groupMemberRepository,
                                SingleRelationshipRepository singleRelationshipRepository,
                                GroupMessageRepository groupMessageRepository,
                                GroupRelationshipRepository groupRelationshipRepository, TestNameGenerator testNameGenerator,
                                TestCourseGenerator testCourseGenerator, LanguageRepository languageRepository, List<Language> allLanguages,
        SettingsRepository settingsRepository) {
        this.studentRepository = studentRepository;
        this.lvaRepository = lvaRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.singleRelationshipRepository = singleRelationshipRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.groupRelationshipRepository = groupRelationshipRepository;
        this.testNameGenerator = testNameGenerator;
        this.testCourseGenerator = testCourseGenerator;
        this.languageRepository = languageRepository;
        this.descriptionGenerator = new RandomDescriptionGenerator();
        this.settingsRepository = settingsRepository;
    }



    @PostConstruct
    private void generateData() throws IOException {
        generateLanguages();
        allLanguages = languageRepository.findAll();
        generateLvas();
        generateStudents();
        generateGroups();
        generateMatches();
        generateMessages();
        generateGroupRelationShips();
        generateSettings();
    }

    private void generateProfilePictures() {
        if (!base64Images.isEmpty()) {
            LOGGER.debug("profile pictures already generated");
        } else {
            // The directory with the JPEG files.
            String imageDirectoryPath = "src/main/resources/images";

            // Get all JPEG files in the directory.
            File folder = new File(imageDirectoryPath);
            File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".jpeg"));

            if (listOfFiles == null) {
                System.out.println("No files in the directory.");
                return;
            }

            // Create the list to hold the Base64 strings.
            base64Images = new ArrayList<>();

            for (File file : listOfFiles) {
                try {
                    // Read each file into a byte array.
                    byte[] imageBytes = Files.readAllBytes(file.toPath());

                    // Add the Base64 string to the list.
                    base64Images.add(imageBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void generateLvas() {
        if (lvaRepository.findAll().size() > 0) {
            LOGGER.debug("lva already generated");
        } else {
            Random random = new Random();

            IntStream.rangeClosed(1, 50).forEach(i -> {
                String id = String.format("%03d", random.nextInt(1000)) + "." + String.format("%03d", random.nextInt(100));
                String name = testCourseGenerator.getRandomCourse(i);

                Lva lva = new Lva(id, name, null, null);

                lvaRepository.save(lva);
            });
        }
    }

    private void generateStudents() {
        if (studentRepository.findAll().size() > 0) {
            LOGGER.debug("student already generated");
        } else {
            generateProfilePictures();
            LOGGER.debug("generating {} student entries", NUMBER_OF_STUDENTS_TO_GENERATE);
            /*
            List<String> idArr = new LinkedList<>();
            idArr.add("123.04");
            idArr.add("123.05");
            List<Lva> lvaList = new ArrayList<>();
            */
            List<Gender> genders = new ArrayList<>();
            genders.add(Gender.MALE);
            genders.add(Gender.FEMALE);
            genders.add(Gender.OTHER);
            List<Lva> lvaList = lvaRepository.findAll();
            Random random = new Random();
            int upperBound1 = 5 + random.nextInt(6);
            int upperBound2 = 5 + random.nextInt(6);
            for (int i = 1; i <= NUMBER_OF_STUDENTS_TO_GENERATE; i++) {
                //Get random lvas from repository
                upperBound1 = 5 + random.nextInt(6);
                upperBound2 = 5 + random.nextInt(6);
                Collections.shuffle(lvaList);
                List<Lva> randomCurrentLvas = lvaList.subList(0, upperBound1);
                List<Lva> randomCompletedLvas = lvaList.subList(49 - upperBound2, 49);
                String email = String.format(TEST_EMAIL, i);
                Student student = Student.StudentBuilder.aStudent()
                    .withEmail(email)
                    .withPassword(TEST_PASSWORD)
                    .withFirstName(testNameGenerator.getRandomFirstName())
                    .withLastName(testNameGenerator.getRandomLastName())
                    .withDateOfBirth(randomDateOfBirth())
                    .withAdmin(i == 1)
                    .withMeetsIrl(i % 2 == 0)
                    .withDescription(descriptionGenerator.getRandomDescription())
                    .withGender(genders.get(i % 3))
                    .withPrefLanguage(allLanguages.get(i % 3))
                    .withCurrentLvas(randomCurrentLvas)
                    .withCompletedLvas(randomCompletedLvas)
                    .withEnabled(true)
                    .withImageUrl(base64Images.get(i - 1))
                    //.withImageUrl(null)
                    .build();
                LOGGER.debug("saving student {}", student);
                studentRepository.save(student);
            }
        }
    }

    private LocalDate randomDateOfBirth() {
        int currentYear = LocalDate.now().getYear();
        int startYearUndergraduate = currentYear - 25;
        int endYearUndergraduate = currentYear - 18;
        int startYearGraduate = currentYear - 30;
        int endYearGraduate = currentYear - 22;

        int randomYear;
        if (ThreadLocalRandom.current().nextBoolean()) { // 50% chance to be undergraduate or graduate
            randomYear = ThreadLocalRandom.current().nextInt(startYearUndergraduate, endYearUndergraduate + 1);
        } else {
            randomYear = ThreadLocalRandom.current().nextInt(startYearGraduate, endYearGraduate + 1);
        }

        int randomDayOfYear = ThreadLocalRandom.current().nextInt(1, LocalDate.ofYearDay(randomYear, 1).lengthOfYear() + 1);

        return LocalDate.ofYearDay(randomYear, randomDayOfYear);
    }

    private void generateGroups() {
        if (groupRepository.findAll().size() > 0) {
            LOGGER.debug("groups already generated");
        } else {
            LOGGER.debug("generating {} group entries", GROUPS_TO_GENERATE.length);

            var students = studentRepository.findAll();
            for (int group : GROUPS_TO_GENERATE) {
                Group generated = groupRepository.save(Group.GroupBuilder.aGroup().withGroupLeaderId(group).withName("TU Wien Team " + group).withDescription("This is a Test.").build());
                for (int i = 1; i < students.size(); i++) {
                    if (i % group != 0) {
                        continue;
                    }
                    Student s = students.get(i - 1);
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
                Student s1 = students.get(i - 1);
                Student s2 = students.get(i);
                singleRelationshipRepository.save(new SingleRelationship(
                    s1,
                    s2,
                    RelStatus.MATCHED));
            }
        }
    }

    private void generateSettings() {
        if (!settingsRepository.findAll().isEmpty()) {
            LOGGER.debug("settings already generated");
        } else {
            LOGGER.debug("generating settings");

            var students = studentRepository.findAll();
            for (int i = 0; i < students.size(); i++) {
                Settings settings = new Settings();
                settings.setHideLastname(false);
                settings.setHideEmail(false);
                settings.setStudent(students.get(i));
                settings.setHideFirstname(false);
                settings.setHideGender(false);
                settings.setHideEmail(false);
                settingsRepository.save(settings);
            }
        }
    }

    private void generateGroupRelationShips() {
        if (!groupRelationshipRepository.findAll().isEmpty()) {
            LOGGER.debug("relationships already generated");
        } else {
            LOGGER.debug("generating group relationships");

            var students = studentRepository.findAll();
            var groups = groupRepository.findAll();
            for (int i = 2; i < students.size() - students.size() / 2; i++) {
                GroupRelationship groupRelationship = new GroupRelationship();
                groupRelationship.setUser(students.get(i));
                groupRelationship.setRecommendedGroup(groups.get(0));
                groupRelationship.setStatus(RelStatus.LIKED);
                groupRelationshipRepository.save(groupRelationship);
            }
        }
    }

    private void generateMessages() {
        if (groupMessageRepository.findAll().size() > 0) {
            LOGGER.debug("messages already generated");
            return;
        }
        LOGGER.debug("generating messages");
        Student student = Student.StudentBuilder.aStudent().withId(1L).build();
        Group group = Group.GroupBuilder.aGroup().withId(1L).build();
        Instant instant = Instant.now();
        for (int i = 1; i < 1000; i++) {
            instant = instant.plusMillis(1);
            groupMessageRepository.save(new GroupMessage()
                .setSender(student)
                .setReceiver(group)
                .setTimestamp(Timestamp.from(instant))
                .setContent("Message" + i));
        }
    }

    private void generateLanguages() throws IOException {
        File fileObj = new File("src/main/resources/languages.JSON");
        Map<String, String> result = new ObjectMapper().readValue(fileObj, HashMap.class);
        for (Map.Entry<String, String> entry : result.entrySet()) {
            Language l = new Language(entry.getKey(), entry.getValue());
            languageRepository.save(l);
        }
    }

}
