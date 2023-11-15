package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LvaDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LvaMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Endpoint class for Students.
 **/
@RestController
@RequestMapping(value = "/api/v1/students")
public class StudentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final LvaMapper lvaMapper;
    private final FilterService filterService;
    private final SecurityProperties securityProperties;


    @Autowired
    public StudentEndpoint(StudentService studentService, StudentMapper studentMapper, LvaMapper lvaMapper, FilterService filterService, SecurityProperties securityProperties) {
        this.studentService = studentService;
        this.studentMapper = studentMapper;
        this.lvaMapper = lvaMapper;
        this.filterService = filterService;
        this.securityProperties = securityProperties;
    }

    /**
     * Method to retrieve all persisted students.
     **/
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get a simple list of all students", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleStudentDto> findAll() {
        LOGGER.info("GET /api/v1/students");
        return studentMapper.studentToSimpleStudentDtoList(studentService.findAll());
    }

    /**
     * Method to retrieve a specific student via their id.
     **/
    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific student", security = @SecurityRequirement(name = "apiKey"))
    public DetailedStudentDto find(@PathVariable Long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/students/{}", id);
        return studentMapper.studentToDetailedStudentDto(studentService.findStudentById(id));
    }

    /**
     * Method to retrieve a specific student via their id.
     **/
    @Secured("ROLE_USER")
    @GetMapping(value = "/privacy/{id}")
    @Operation(summary = "Get detailed information about a specific student", security = @SecurityRequirement(name = "apiKey"))
    public DetailedStudentDto findPrivacy(@PathVariable Long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/students/{}", id);
        return studentMapper.studentToDetailedStudentDto(studentService.findStudentByIdPrivacy(id));
    }

    /**
     * Method to create a new student.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Persist a user", security = @SecurityRequirement(name = "apiKey"))
    public DetailedStudentDto create(@Valid @RequestBody DetailedStudentDto detailedStudentDto) {
        LOGGER.info("POST /api/v1/students body: {}", detailedStudentDto);
        return studentMapper.studentToDetailedStudentDto(
            studentService.persistStudent(studentMapper.detailedStudentDtoToStudent(detailedStudentDto)));
    }

    /**
     * Method to update a student.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a user", security = @SecurityRequirement(name = "apiKey"))
    public DetailedStudentDto update(@PathVariable long id, @RequestBody DetailedStudentDto detailedStudentDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/students/ body: {}", detailedStudentDto.withId(id));
        Student oldStudent = studentService.findStudentById(studentMapper.detailedStudentDtoToStudent(detailedStudentDto).getId());
        studentMapper.updateStudentFromDto(detailedStudentDto, oldStudent);
        //System.out.println(oldStudent);
        return studentMapper.studentToDetailedStudentDto(
            studentService.updateStudent(oldStudent));
    }

    @Secured("ROLE_USER")
    @GetMapping("/groupmode/{id}")
    @Operation(summary = "Get if student searches for groups", security = @SecurityRequirement(name = "apiKey"))
    public Boolean getSearchForGroup(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/student/groupmode/{}", id);
        return filterService.getGroupRecomMode(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("/lvas/{id}")
    @Operation(summary = "Get currentLvas of student", security = @SecurityRequirement(name = "apiKey"))
    public List<LvaDto> getLvas(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/lvas/{}", id);
        return lvaMapper.lvaToLvaDto(studentService.getCurrentLvas(id));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "delete student with id", security = @SecurityRequirement(name = "apiKey"))
    public boolean deleteOwnStudent(@PathVariable Long id, HttpServletRequest request) throws NotAuthorizedException {
        LOGGER.info("DELETE /api/v1/filter/delete/{}", id);
        studentService.deleteOwnStudent(id, getId(request));
        return true;
    }

    private Long getId(HttpServletRequest request) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        String token = request.getHeader("Authorization");
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(signingKey))
            .build()
            .parseClaimsJws(token.replace("Bearer ", ""))
            .getBody()
            .get("id", Long.class);
    }
}
