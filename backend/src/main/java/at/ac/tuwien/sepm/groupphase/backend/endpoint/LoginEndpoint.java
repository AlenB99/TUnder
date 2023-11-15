package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StudentLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication")
public class LoginEndpoint {

    private final StudentService studentService;

    public LoginEndpoint(StudentService studentService) {
        this.studentService = studentService;
    }

    @PermitAll
    @PostMapping
    public String login(@RequestBody StudentLoginDto studentLoginDto) {
        return studentService.login(studentLoginDto);
    }
}
