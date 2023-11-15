package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Service for working with Recommendations.
 */
public interface RecommendationService {

    /**
     * Get the next recommended student for the student with id (@code id).
     *
     * @param id to get the next recommendation for
     * @return returns entity of the next recommended student
     * @throws NotFoundException if the user with given ID (@code id) does not exist in the persistent data store
     */
    Student getRecommended(long id) throws NotFoundException, ValidationException;

    /**
     * Get the next recommended group for the student with id (@code id).
     *
     * @param id to get the next recommendation for
     * @return returns entity of the next recommended group
     * @throws NotFoundException if the user with given ID (@code id) does not exist in the persistent data store
     */
    Group getRecommendedGroup(long id) throws NotFoundException, ValidationException;

    /**
     * Get student value pair for the next recommendations.
     *
     * @param studentId Student for wich to get the stats
     * @return Pairs of recommended Students and their distances
     */
    List<Pair<Student, Double>> getRecommendedStudentDemo(Long studentId);

    /**
     * Get group value pair for the next recommendations.
     *
     * @param studentId Student for wich to get the stats
     * @return Pairs of recommended Groups and their distancesd
     */
    List<Pair<Group, Double>> getRecommendedGroupDemo(Long studentId);
}
