package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Filter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface FilterService {
    /**
     * Get all students that match the filter that is set for the given student (fetches filter from datasource)
     * except for the student given in {@code student} and students that are already part of a recommendation containing student
     * given in {@code student}.
     *
     * @param student student to get filtered results for
     * @return filtered list of Students
     * @throws NotFoundException if student given in {@code student} does not exist in data store
     */
    List<Student> getFilteredStudents(Student student) throws NotFoundException;

    /**
     * Get all students that match the filter that is set for the given student (fetches filter from datasource)
     * except for the student given in {@code studentId} and students that are already part of a recommendation containing student
     * given in {@code studentId}.
     *
     * @param studentId id of student to get filtered results for
     * @return filtered list of Students
     * @throws NotFoundException if student with id given in {@code studentId} does not exist in data store
     */
    List<Student> getFilteredStudents(Long studentId) throws NotFoundException;

    /**
     * Get all students that match the filter that is set for the given student (fetches filter from datasource)
     * except for the student given in {@code student} and students that are already part of a recommendation containing student
     * given in {@code student}.
     *
     * @param student student to get filtered results for
     * @return filtered list of Students
     * @throws NotFoundException if student given in {@code student} does not exist in data store
     */
    List<Group> getFilteredGroups(Student student) throws NotFoundException;

    /**
     * Get all groups that match the filter that is set for the given student (fetches filter from datasource)
     * except of groups that the student is already part of.
     *
     * @param studentId id of student to get filtered results for
     * @return filtered list of Students
     * @throws NotFoundException if student with id given in {@code studentId} does not exist in data store
     */
    List<Group> getFilteredGroups(Long studentId) throws NotFoundException;

    /**
     * set filter for student given in {@code filter.student}. If the student already has a filter, this filter gets updated.
     *
     * @param filter filter to be created or updated
     * @return created or updated Filter
     * @throws ValidationException if filter given in {@code filter} does not match conditions
     * @throws ConflictException   if filter given in {@code filter} has a conflict with data in data store (e.g. student does not exist)
     */
    Filter setFilter(Filter filter) throws ValidationException, ConflictException;

    /**
     * get filter with id given in {@code filterId}.
     *
     * @param filterId id of filter to get
     * @return filter
     * @throws NotFoundException If no filter with {@code filterId} is found, a NotFoundException is thrown
     */
    Filter getFilter(Long filterId) throws NotFoundException;

    /**
     * removes a filter for student given in {@code student}.
     *
     * @param student student to delete filter for
     */
    void removeFilter(Student student);

    /**
     * removes filter given in {@code filter}.
     *
     * @param filter filter to delete
     */
    void removeFilter(Filter filter) throws ValidationException;

    /**
     * removes filter by id given in {@code id}.
     *
     * @param filterId id of filter to delete
     */
    void removeFilter(Long filterId);

    /**
     * Get information if student with id given in {@code studentId} searches for groups.
     *
     * @param studentId id to get information for
     * @return true if student searches for groups, false if student searches for individuals
     */
    Boolean getGroupRecomMode(Long studentId);

    Filter getFilterByStudent(Long studentId);


}
