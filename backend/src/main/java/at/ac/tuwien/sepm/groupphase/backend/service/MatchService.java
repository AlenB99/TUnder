package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

/**
 * Service for working with Relationships.
 */
public interface MatchService {

    /**
     * Create new relationship for user {@code singleRel.getUserId()}.
     *
     * @param singleRel contains id of user and id of recommended(user that
     *                  was recommended to original user) and the status
     *                  of their relationship (enum RelStatus).
     * @return returns entity after update on db
     * @throws NotFoundException   if the user with given ID (in singleRel) does not exist in the persistent data store
     * @throws ValidationException if the validations does not succeed
     */
    SingleRelationship postSingleRelationship(SingleRelationship singleRel) throws NotFoundException, ValidationException;

    /**
     * Create new relationship for user {@code singleRel.getUserId()}.
     *
     * @param groupRel contains id of user and id of recommended(group that
     *                 was recommended to original user) and the status
     *                 of their relationship (enum RelStatus).
     * @return returns entity after update on db
     * @throws ValidationException if the validations does not succeed
     */
    GroupRelationship postGroupRelationship(GroupRelationship groupRel) throws ValidationException;

    GroupRelationship inviteToGroup(String matnr, Long groupId) throws ValidationException;

    /**
     * Set relationship between user and group to {@code RelStatus.INVITED}.
     *
     * @param userId  student in relationship
     * @param groupId group in relationship
     * @return returns entity
     */
    GroupRelationship inviteToGroup(long userId, Long groupId) throws ValidationException;


    /**
     * Retrieves all likes of user {@code singleRel.getUserId()}.
     *
     * @param id contains username of user whose likes to retrieve.
     * @return returns list of single relationships where user has liked but has not been liked back
     */
    List<SimpleStudentDto> getLikes(Long id);

    /**
     * Retrieves all liked and matched students of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @return list of students liked or matched by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Student> getLikedAndMatchedStudents(Long studentId) throws NotFoundException;

    /**
     * Retrieves most recent liked and matched students of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @param count     max amount of students to retrieve
     * @return list of students liked or matched by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Student> getLikedAndMatchedStudents(Long studentId, int count) throws NotFoundException;

    /**
     * Retrieves all disliked students of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @return list of students disliked by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Student> getDislikedStudents(Long studentId) throws NotFoundException;

    /**
     * Retrieves most recent disliked students of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @param count     max amount of students to retrieve
     * @return list of students disliked by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Student> getDislikedStudents(Long studentId, int count) throws NotFoundException;

    /**
     * Retrieves all liked and matched groups of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @return list of groups liked or matched by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Group> getLikedAndMatchedGroups(Long studentId) throws NotFoundException;

    /**
     * Retrieves most recent liked and matched groups of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @param count     max amount of students to retrieve
     * @return list of groups liked or matched by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Group> getLikedAndMatchedGroups(Long studentId, int count) throws NotFoundException;

    /**
     * Retrieves all disliked groups of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @return list of groups disliked by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Group> getDislikedGroups(Long studentId) throws NotFoundException;

    /**
     * Retrieves most recent disliked students of user specified in {@code studentId}.
     *
     * @param studentId id of student to get results for
     * @param count     max amount of students to retrieve
     * @return list of students disliked by user specified in {@code studentId}
     * @throws NotFoundException if no student with {@code studentId} found in data store
     */
    List<Group> getDislikedGroups(Long studentId, int count) throws NotFoundException;

    void reCalculate(long studentId, int maxData);
}
