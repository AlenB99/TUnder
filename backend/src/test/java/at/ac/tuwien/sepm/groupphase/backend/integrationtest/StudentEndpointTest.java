package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.StudentEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LvaMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.StudentService;
import at.ac.tuwien.sepm.groupphase.backend.type.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentEndpoint.class)
public class StudentEndpointTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private SecurityProperties securityProperties;

    @Mock
    private SecurityConfig securityConfig;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FilterService filterService;

    @MockBean
    private StudentMapper studentMapper;

    @MockBean
    private LvaMapper lvaMapper;

    private SimpleStudentDto simpleStudentDto;
    private DetailedStudentDto detailedStudentDto;

    @Autowired
    private WebApplicationContext webAppContext;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        simpleStudentDto = new SimpleStudentDto(1L, "1email@test.com", "test", "test", null);

        detailedStudentDto = new DetailedStudentDto(
            1L,
            "e12122106@student.tuwien.ac.at",
            "eins",
            "zwei",
            Gender.OTHER,
            null,
            "test entity 2",
            LocalDate.now().minusDays(2),
            false,
            false,
            null,
            null,
            null,
            null,
            null
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_shouldReturn200() throws Exception {
        when(studentService.findAll()).thenReturn(Collections.singletonList(new Student()));
        when(studentMapper.studentToSimpleStudentDto(any(Student.class))).thenReturn(simpleStudentDto);

        mockMvc.perform(get("/api/v1/students").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void find_shouldReturn200() throws Exception {
        when(studentService.findStudentById(any())).thenReturn(new Student());
        when(studentMapper.studentToDetailedStudentDto(any())).thenReturn(detailedStudentDto);

        mockMvc.perform(get("/api/v1/students/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_shouldReturn201() throws Exception {
        when(studentService.persistStudent(any())).thenReturn(new Student());
        when(studentMapper.detailedStudentDtoToStudent(any())).thenReturn(new Student());
        when(studentMapper.studentToDetailedStudentDto(any())).thenReturn(detailedStudentDto);


        mockMvc.perform(post("/api/v1/students").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void update_shouldReturn201() throws Exception {
        when(studentService.findStudentById(any())).thenReturn(new Student());
        when(studentService.updateStudent(any())).thenReturn(new Student());
        when(studentMapper.detailedStudentDtoToStudent(any())).thenReturn(new Student());
        when(studentMapper.studentToDetailedStudentDto(any())).thenReturn(detailedStudentDto);

        mockMvc.perform(put("/api/v1/students/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isOk());
    }
}
