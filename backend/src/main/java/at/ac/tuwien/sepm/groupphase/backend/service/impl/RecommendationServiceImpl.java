package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SettingsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.RankService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import at.ac.tuwien.sepm.groupphase.backend.service.RecommendationService;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FilterService filterService;
    private final RankService rankService;
    private final StudentValidator studentValidator;
    private final SettingsRepository settingsRepository;

    public RecommendationServiceImpl(FilterService filterService, RankService rankService, StudentValidator studentValidator,
                                     SettingsRepository settingsRepository) {
        this.filterService = filterService;
        this.rankService = rankService;
        this.studentValidator = studentValidator;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Student getRecommended(long id) throws NotFoundException, ValidationException {
        LOGGER.trace("getNext(id: {})", id);
        studentValidator.validateExists(id);
        List<Student> studentList = filterService.getFilteredStudents(id);
        studentList = rankService.rankStudents(id, studentList);
        if (studentList.isEmpty()) {
            return new Student(-1L);
        }

        Student s = studentList.get(0);
        var settingsOptional = settingsRepository.findByStudentId(s.getId());
        if (settingsOptional.isPresent()) {
            var settings = settingsOptional.get();
            if (settings.isHideGender()) {
                s.setGender(null);
            }
            if (settings.isHideLastname()) {
                s.setLastName("");
            }
            if (settings.isHideAge()) {
                s.setDateOfBirth(null);
            }
        }

        return s;
    }


    @Override
    public Group getRecommendedGroup(long id) throws NotFoundException, ValidationException {
        LOGGER.trace("getNext(id: {})", id);
        studentValidator.validateExists(id);
        List<Group> groupList = filterService.getFilteredGroups(id);
        if (!groupList.isEmpty()) {
            return groupList.get(0);
        } else {
            return new Group(-1L);
        }
    }

    @Override
    public List<Pair<Student, Double>> getRecommendedStudentDemo(Long studentId) {
        LOGGER.trace("getDemoModeStats - Student(id: {})", studentId);
        studentValidator.validateExists(studentId);
        List<Student> studentList = filterService.getFilteredStudents(studentId);
        List<Pair<Student, Double>> rankListStudentsDemo = rankService.rankStudentsDemo(studentId, studentList);
        rankListStudentsDemo.sort(Comparator.comparing(Pair::getValue));

        return rankListStudentsDemo;
    }

    @Override
    public List<Pair<Group, Double>> getRecommendedGroupDemo(Long studentId) {
        LOGGER.trace("getDemoModeStats - Group(id: {})", studentId);
        studentValidator.validateExists(studentId);
        List<Group> groupList = filterService.getFilteredGroups(studentId);
        List<Pair<Group, Double>> rankListGroupsDemo = rankService.rankGroupsDemo(studentId, groupList);
        return rankListGroupsDemo;
    }
}
