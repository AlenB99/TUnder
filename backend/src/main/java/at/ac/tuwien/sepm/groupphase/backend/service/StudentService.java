package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StudentLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * Interface for student services.
 **/
public interface StudentService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address.
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exist
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find a student based on the email address.
     *
     * @param email the email address
     * @return a Student
     */
    Student findStudentByEmail(String email) throws NotFoundException;

    Student findStudentByIdPrivacy(Long id) throws NotFoundException;

    /**
     * Get current lvas of student given in {@code studentId}.
     *
     * @param studentId id of student to get currentLvas for
     * @return list of current lvas of student
     */
    List<Lva> getCurrentLvas(Long studentId);

    /**
     * Log in a user.
     *
     * @param studentLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(StudentLoginDto studentLoginDto);

    /**
     * Retrieves all users.
     *
     * @return a List of Students
     */
    List<Student> findAll();

    /**
     * Persists a Student.
     *
     * @return the persisted Student
     */
    Student persistStudent(Student student);

    /**
     * Finds 1 Student by their id.
     *
     * @param id the id of the Student
     * @return the Student with the id
     */
    Student findStudentById(Long id) throws NotFoundException;


    /**
     * Update 1 Student.
     *
     * @param student the Student to be updated
     * @return the resulting Student
     */
    Student updateStudent(Student student) throws ValidationException;

    /**
     * Delete student if student with id given in {@code secId} is same student.
     *
     * @param id    id to get information for
     * @param secId id from the JWT-Token
     */
    void deleteOwnStudent(Long id, Long secId) throws NotAuthorizedException;
}
