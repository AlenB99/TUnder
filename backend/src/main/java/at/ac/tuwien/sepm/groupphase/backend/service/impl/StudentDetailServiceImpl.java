package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StudentLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StudentRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.security.StudentDetailsService;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDetailServiceImpl implements StudentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentValidator validator;
    private final GroupRepository groupRepository;
    private final JwtTokenizer jwtTokenizer;

    @Autowired
    public StudentDetailServiceImpl(StudentRepository studentRepository,
                                    PasswordEncoder passwordEncoder, StudentValidator validator,
                                    GroupRepository groupRepository, JwtTokenizer jwtTokenizer) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.groupRepository = groupRepository;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.trace("Load all user by email");
        try {
            Student student = findStudentByEmail(email);
            List<GrantedAuthority> grantedAuthorities;
            if (student.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            return new User(student.getEmail(), student.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public Student findStudentByEmail(String email) throws NotFoundException {
        LOGGER.trace("Find student by email");
        Student student = studentRepository.findStudentByEmail(email);
        if (student != null) {
            return student;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public Student findStudentByIdPrivacy(Long id) throws NotFoundException {
        LOGGER.trace("Find student with id {} with privacy", id);
        Optional<Student> studentOptional = studentRepository.findById(id);
        if (studentOptional.isPresent()) {
            var student = studentOptional.get();
            if (student.getSettings().isHideFirstname()) {
                student.setFirstName("");
            }
            if (student.getSettings().isHideLastname()) {
                student.setLastName("");
            }
            if (student.getSettings().isHideEmail()) {
                student.setEmail("");
            }
            if (student.getSettings().isHideGender()) {
                student.setGender(null);
            }
            if (student.getSettings().isHideAge()) {
                student.setDateOfBirth(null);
            }
            return student;
        } else {
            throw new NotFoundException(String.format("Could not find student with id %s", id));
        }
    }

    @Override
    public String login(StudentLoginDto studentLoginDto) {
        Student student;
        try {
            student = findStudentByEmail(studentLoginDto.getEmail());
        } catch (Exception e) {
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        }

        StudentDetailsService studentDetailsService = new StudentDetailsService(student);
        if (studentDetailsService.isAccountNonLocked()
            && studentDetailsService.isEnabled()
            && passwordEncoder.matches(studentLoginDto.getPassword(), studentDetailsService.getPassword())
        ) {
            List<String> roles = studentDetailsService.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(studentDetailsService.getUsername(), roles, student.getId());
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    public List<Student> findAll() {
        LOGGER.trace("Find all students");
        return studentRepository.findAll();
    }

    @Override
    public Student persistStudent(Student student) {
        LOGGER.trace("Persist new Student {}", student);
        return studentRepository.save(student);
    }

    @Override
    public Student findStudentById(Long id) throws NotFoundException {
        LOGGER.trace("Find student with id {}", id);
        Optional<Student> studentOptional = studentRepository.findById(id);
        if (studentOptional.isPresent()) {
            var student = studentOptional.get();
            return student;
        } else {
            throw new NotFoundException(String.format("Could not find student with id %s", id));
        }
    }

    @Override
    public Student updateStudent(Student student) throws ValidationException {
        LOGGER.trace("Update Student {}", student);
        validator.validate(student);
        return studentRepository.save(student);
    }

    @Transactional
    @Override
    public void deleteOwnStudent(Long id, Long secId) throws NotAuthorizedException {
        LOGGER.trace("Delete Student with id:{}", id);
        validator.validateExists(id);
        if (!id.equals(secId)) {
            throw new NotAuthorizedException("Not authoritzed to perform this action");
        }
        Student toDelete = studentRepository.findStudentById(id);

        List<Group> groups = groupRepository.getGroupsByGroupLeaderId(id);
        /*
        toDelete.setEnabled(false);
        toDelete.getSettings().setSleeping(true);

         */
        studentRepository.delete(toDelete);
        groupRepository.deleteAll(groups);
    }

    @Override
    public List<Lva> getCurrentLvas(Long studentId) {
        LOGGER.trace("Get current lvas for student: {}", studentId);
        return findStudentById(studentId).getCurrentLvas();
    }
}
