package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.FilterRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.FilterSpecifications;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.FilterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class FilterServiceImpl implements FilterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FilterRepository filterRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final LvaService lvaService;
    private final FilterValidator filterValidator;

    public FilterServiceImpl(FilterRepository filterRepository, StudentRepository studentRepository, GroupRepository groupRepository, LvaService lvaService, FilterValidator filterValidator
    ) {
        this.filterRepository = filterRepository;
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.lvaService = lvaService;
        this.filterValidator = filterValidator;
    }

    @Override
    public List<Student> getFilteredStudents(Student student) throws NotFoundException {
        LOGGER.debug("Get filtered students for {}", student);
        return getFilteredStudents(student.getId());

    }

    @Override
    public List<Student> getFilteredStudents(Long studentId) throws NotFoundException {
        LOGGER.debug("Get filtered students for student with id {}", studentId);
        var filter = getFilterForStudent(studentId, "Student with id %s to get filtered recommendations for not found.");
        List<Student> studentList = studentRepository.findAll(FilterSpecifications.filterSpecificationsStudent(filter, studentId));
        return studentList;
    }

    /**
     * Get all students that match the filter that is set for the given student (fetches filter from datasource)
     * except for the student given in {@code student} and students that are already part of a recommendation containing student
     * given in {@code student}.
     *
     * @param student student to get filtered results for
     * @return filtered list of Students
     * @throws NotFoundException if student given in {@code student} does not exist in data store
     */
    @Override
    public List<Group> getFilteredGroups(Student student) throws NotFoundException {
        LOGGER.debug("Get filtered groups for {}", student);
        return getFilteredGroups(student.getId());
    }

    /**
     * Get all groups that match the filter that is set for the given student (fetches filter from datasource)
     * except of groups that the student is already part of.
     *
     * @param studentId id of student to get filtered results for
     * @return filtered list of Students
     * @throws NotFoundException if student with id given in {@code studentId} does not exist in data store
     */
    @Override
    public List<Group> getFilteredGroups(Long studentId) throws NotFoundException {
        LOGGER.debug("Get filtered groups for student with id {}", studentId);
        var filter = getFilterForStudent(studentId, "Student with id %s to get filtered recommendations for not found.");
        return groupRepository.findAll(FilterSpecifications.filterSpecificatonsGroup(filter, studentId));
    }

    @Override
    public Filter setFilter(Filter filter) throws ValidationException, ConflictException {
        LOGGER.debug("Set filter {}", filter);
        checkForExistingFilter(filter);
        filterValidator.validateFilterForSave(filter, this, lvaService);
        var result = filterRepository.save(filter);
        return result;
    }

    @Override
    public Filter getFilter(Long filterId) {
        LOGGER.debug("Get filter with id: {}", filterId);
        Optional<Filter> filterOptional = filterRepository.findById(filterId);
        if (!filterOptional.isPresent()) {
            throw new NotFoundException(String.format("Filter with id %s not found", filterId));
        }
        return filterOptional.get();
    }

    @Override
    public void removeFilter(Student student) {
        LOGGER.debug("Remove filter for student: {}", student);
        filterRepository.deleteByStudent(student);
    }

    @Override
    public void removeFilter(Filter filter) throws ValidationException {
        LOGGER.debug("Remove filter: {}", filter);
        if (filter.getId() == null) {
            throw new ValidationException(String.format("Filter %s could not be removed.", filter), List.of("Filter to delete needs to contain an ID"));
        }
        filterRepository.delete(filter);
    }

    @Override
    public void removeFilter(Long id) {
        LOGGER.debug("Remove filter with id: {}", id);
        filterRepository.deleteById(id);
    }

    /**
     * Get information if student with id given in {@code studentId} searches for groups.
     *
     * @param studentId id to get information for
     * @return true if student searches for groups, false if student searches for individuals
     */
    @Override
    public Boolean getGroupRecomMode(Long studentId) {
        return getFilterForStudent(studentId, "Student with id %s to get recommendation type for not found.").getGroupRecomMode();
    }

    @Override
    public Filter getFilterByStudent(Long studentId) {
        var optional = this.filterRepository.findByStudent_Id(studentId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return Filter.FilterBuilder.aFilter().build();
    }

    private Filter getFilterForStudent(Long studentId, String notFoundMessage) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException(String.format(notFoundMessage, studentId));
        }
        Filter filter = Filter.FilterBuilder.aFilter().build();
        Optional<Filter> filterRequest = filterRepository.findByStudent(Student.StudentBuilder.aStudent().withId(studentId).build());
        if (filterRequest.isPresent()) {
            filter = filterRequest.get();
        }
        return filter;
    }

    private void checkForExistingFilter(Filter filter) {
        LOGGER.trace("check for existing filter {}", filter);
        Student student = filter.getStudent();
        Optional<Filter> filterOptional = filterRepository.findByStudent(student);
        if (filterOptional.isPresent()) {
            filter.setId(filterOptional.get().getId());
        }
    }
}
