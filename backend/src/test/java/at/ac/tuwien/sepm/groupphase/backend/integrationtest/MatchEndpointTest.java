package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MatchEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SingleRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.RankService;
import at.ac.tuwien.sepm.groupphase.backend.service.RecommendationService;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@ExtendWith(SpringExtension.class)
@WebMvcTest(MatchEndpoint.class)
public class MatchEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityProperties securityProperties;

    @Mock
    private SecurityConfig securityConfig;

    @MockBean
    private CustomMatchMapper matchMapper;

    @MockBean
    private MatchService matchService;

    @MockBean
    private StudentMapper studentMapper;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private RankService rankService;

    @MockBean
    private GroupMapper groupMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webAppContext;
    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void postSingleRelationship_validInput_returnsCreated() throws Exception {
        // given
        SingleRelationshipDto inputDto = new SingleRelationshipDto(1L,2L, RelStatus.LIKED);
        SingleRelationshipDto outputDto = new SingleRelationshipDto(1L,2L, RelStatus.LIKED);
        SingleRelationship singleRelationship = new SingleRelationship(
            Student.StudentBuilder.aStudent().withId(1L).build(),
            Student.StudentBuilder.aStudent().withId(2L).build(),
            RelStatus.LIKED
        );

        when(matchService.postSingleRelationship(any())).thenReturn(singleRelationship);
        when(matchMapper.singleRelToSingleRelDto(any())).thenReturn(outputDto);

        // when
        mockMvc.perform(post("/api/v1/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))

            // then
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(outputDto)));
    }

    @Test
    public void getRecommended_validId_returnsOk() throws Exception {
        // given
        Long id = 1L;

        when(recommendationService.getRecommended(id)).thenReturn(new Student());

        // when
        mockMvc.perform(get("/api/v1/match/" + id))

            // then
            .andExpect(status().isOk());
    }
    @Test
    public void getLikes_validId_returnsOk() throws Exception {
        // given
        Long id = 1L;
        List<SimpleStudentDto> l = new ArrayList<>();
        SimpleStudentDto s = new SimpleStudentDto(2L,"dsf","dsf","sdf",null);
        l.add(s);
        when(matchService.getLikes(id)).thenReturn(l);

        // when
        mockMvc.perform(get("/api/v1/match/likes/" + id))

            // then
            .andExpect(status().isOk());
    }
}

