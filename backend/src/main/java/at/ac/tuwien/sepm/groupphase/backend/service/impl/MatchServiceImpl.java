package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.IndividualMessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SingleRelationshipRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailSenderService;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.RankService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.SingleRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SingleRelationshipRepository singleRelationshipRepository;
    private final GroupRelationshipRepository groupRelationShipRepository;
    private final SingleRelationshipValidator singleRelationshipValidator;
    private final GroupRelationshipValidator groupRelationshipValidator;
    private final GroupValidator groupValidator;
    private final StudentValidator studentValidator;
    private final StudentRepository studentRepository;
    private final CustomMatchMapper matchMapper;
    private final RankService rankService;
    private final EmailSenderService emailSenderService;
    private final IndividualMessageRepository individualMessageRepository;


    @Autowired
    public MatchServiceImpl(SingleRelationshipRepository singleRelationshipRepository,
                            GroupRelationshipRepository groupRelationShipRepository,
                            SingleRelationshipValidator singleRelationshipValidator,
                            GroupRelationshipValidator groupRelationshipValidator,
                            StudentRepository studentRepository,
                            CustomMatchMapper matchMapper,
                            GroupValidator groupValidator,
                            StudentValidator studentValidator,
                            //TOASK: is lazy fine here
                            @Lazy RankService rankService,
                            EmailSenderService emailSenderService, IndividualMessageRepository individualMessageRepository) {
        this.groupRelationShipRepository = groupRelationShipRepository;
        this.groupRelationshipValidator = groupRelationshipValidator;
        this.studentRepository = studentRepository;
        this.matchMapper = matchMapper;
        this.groupValidator = groupValidator;
        this.studentValidator = studentValidator;
        this.rankService = rankService;
        this.emailSenderService = emailSenderService;
        this.individualMessageRepository = individualMessageRepository;
        this.singleRelationshipValidator = singleRelationshipValidator;
        this.singleRelationshipRepository = singleRelationshipRepository;
    }

    @Override
    @Transactional
    public SingleRelationship postSingleRelationship(SingleRelationship singleRel) throws ValidationException {
        LOGGER.trace("Persist new SingleRelationship {}", singleRel);
        if (singleRel.getStatus() == RelStatus.DELETED) {
            SingleRelationship singleRelationship = singleRelationshipRepository.findRelationshipByUserIdsAndStatus(RelStatus.MATCHED, singleRel.getRecommended(), singleRel.getUser());
            individualMessageRepository.deleteIndividualMessagesByRelationship(singleRelationship);
            singleRelationshipRepository.deleteById(singleRelationship.getId());
            return singleRel;
        }
        singleRelationshipValidator.validateRelationshipForCreate(singleRel);
        SingleRelationship relExitsAlready = singleRelationshipRepository.isAlreadyStatus(RelStatus.LIKED, singleRel.getRecommended(), singleRel.getUser());
        if (relExitsAlready != null) {
            singleRel.setStatus(RelStatus.MATCHED);
            //send email
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setSubject("You matched!");
            if (relExitsAlready.getUser().getSettings().isSubscribing()) {
                simpleMailMessage.setText("You matched with " + relExitsAlready.getRecommended().getEmail());
                simpleMailMessage.setTo(relExitsAlready.getUser().getEmail());
                emailSenderService.sendEmail(simpleMailMessage);
            }
            if (relExitsAlready.getRecommended().getSettings().isSubscribing()) {
                simpleMailMessage.setText("You matched with " + relExitsAlready.getUser().getEmail());
                simpleMailMessage.setTo(relExitsAlready.getRecommended().getEmail());
                emailSenderService.sendEmail(simpleMailMessage);
            }
            singleRelationshipRepository.delete(relExitsAlready);
        }

        var relationship = singleRelationshipRepository.save(singleRel);

        int count = singleRelationshipRepository.getSingleRelationshipByUser(singleRel.getUser()).size();
        if ((count) % 10 == 0) {
            if ((count) % 30 == 0) {
                rankService.updatePreference(singleRel.getUserId());
            } else {
                rankService.updateWeights(singleRel.getUserId());
            }
        }
        return relationship;
    }

    public void reCalculate(long studentId, int maxData)  {
        rankService.updatePreference(studentId, 1, maxData);
    }

    @Override
    @Transactional
    public GroupRelationship postGroupRelationship(GroupRelationship groupRel) throws ValidationException {
        LOGGER.trace("Persist new GroupRelationship {}", groupRel);
        groupRelationshipValidator.validateGroupRelationshipForCreate(groupRel);

        var groupRelationship = groupRelationShipRepository.save(groupRel);

        int count = groupRelationShipRepository.findGroupRelationshipByUser(groupRel.getUser()).size();
        if ((count) % 10 == 0) {
            if ((count) % 30 == 0) {
                rankService.updateGroupPreference(groupRel.getUser().getId());
            } else {
                rankService.updateGroupWeights(groupRel.getUser().getId());
            }
        }
        return groupRel;
    }

    @Override
    public GroupRelationship inviteToGroup(String matnr, Long groupId) throws ValidationException {
        LOGGER.trace("Send invite from group: {} to student: {}", groupId, matnr);
        Optional<Student> f = studentRepository.findByEmail(buildEmail(matnr));
        if (!f.isPresent()) {

            throw new NotFoundException("user with email: " + buildEmail(matnr));
        }
        GroupRelationship groupRelationship = new GroupRelationship();
        groupRelationship.setRecommendedGroup(Group.GroupBuilder.aGroup().withId(groupId).build());
        groupRelationship.setUser(f.get());
        groupRelationship.setStatus(RelStatus.INVITED);
        groupRelationshipValidator.validateGroupRelationshipForCreate(groupRelationship);
        return groupRelationShipRepository.save(groupRelationship);
    }

    @Override
    public GroupRelationship inviteToGroup(long userId, Long groupId) throws ValidationException {
        LOGGER.trace("Send invite from group: {} to student: {}", groupId, userId);
        Optional<Student> f = studentRepository.findById(userId);
        if (!f.isPresent()) {
            throw new NotFoundException("user not found");
        }
        GroupRelationship groupRelationship = new GroupRelationship();
        groupRelationship.setRecommendedGroup(Group.GroupBuilder.aGroup().withId(groupId).build());
        groupRelationship.setUser(f.get());
        groupRelationship.setStatus(RelStatus.INVITED);
        groupRelationshipValidator.validateGroupRelationshipForCreate(groupRelationship);
        return groupRelationShipRepository.save(groupRelationship);
    }

    @Override
    public List<SimpleStudentDto> getLikes(Long id) {
        LOGGER.trace("Get likes of user {}", id);
        Student student = studentRepository.findStudentById(id);
        Stream<SimpleStudentDto> likes = singleRelationshipRepository.findLikesByUserIdAndStatus(RelStatus.LIKED, student).stream().map(r -> matchMapper.singleRelToSimpleStudentDto(r, student));
        return likes.toList();
    }

    @Override
    public List<Student> getLikedAndMatchedStudents(Long studentId) throws NotFoundException {
        LOGGER.trace("Get liked and matched students of user {}", studentId);
        Student student = studentRepository.findStudentById(studentId);
        return singleRelationshipRepository.getLikedAndMatchedStudents(student);
    }

    @Override
    public List<Student> getLikedAndMatchedStudents(Long studentId, int count) throws NotFoundException {
        LOGGER.trace("Get the last {} liked and matched students of user {}", count, studentId);
        Student student = studentRepository.findStudentById(studentId);
        return singleRelationshipRepository.getLikedAndMatchedStudents(student, PageRequest.of(0, count));
    }

    @Override
    public List<Student> getDislikedStudents(Long studentId) throws NotFoundException {
        LOGGER.trace("Get disliked students for {}", studentId);
        Student student = studentRepository.findStudentById(studentId);
        return singleRelationshipRepository.getDislikedStudents(student);
    }

    @Override
    public List<Student> getDislikedStudents(Long studentId, int count) throws NotFoundException {
        LOGGER.trace("Get the last {} disliked students for {}", count, studentId);
        Student student = studentRepository.findStudentById(studentId);
        return singleRelationshipRepository.getDislikedStudents(student, PageRequest.of(0, count));
    }

    @Override
    public List<Group> getLikedAndMatchedGroups(Long studentId) throws NotFoundException {
        LOGGER.trace("Get liked and matched groups of user {}", studentId);
        Student student = studentRepository.findStudentById(studentId);
        return groupRelationShipRepository.getLikedAndMatchedGroups(student);

    }

    @Override
    public List<Group> getLikedAndMatchedGroups(Long studentId, int count) throws NotFoundException {
        LOGGER.trace("Get the last {} liked and matched groups of user {}", count, studentId);
        Student student = studentRepository.findStudentById(studentId);
        return groupRelationShipRepository.getLikedAndMatchedGroups(student, PageRequest.of(0, count));
    }

    @Override
    public List<Group> getDislikedGroups(Long studentId) throws NotFoundException {
        LOGGER.trace("Get disliked groups for {}", studentId);
        Student student = studentRepository.findStudentById(studentId);
        return groupRelationShipRepository.getDislikedGroups(student);
    }

    @Override
    public List<Group> getDislikedGroups(Long studentId, int count) throws NotFoundException {
        LOGGER.trace("Get the last {} disliked groups for {}", count, studentId);
        Student student = studentRepository.findStudentById(studentId);
        return groupRelationShipRepository.getDislikedGroups(student, PageRequest.of(0, count));
    }

    private String buildEmail(String matnr) {
        LOGGER.trace("build email called with matnr: {}", matnr);
        StringBuilder stringBuilder = new StringBuilder();
        if (matnr.startsWith("e")) {
            stringBuilder.append(matnr);
        } else {
            stringBuilder.append("e" + matnr);
        }
        stringBuilder.append("@student.tuwien.ac.at");
        return stringBuilder.toString();
    }
}
