package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MatchGroupEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GroupRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MatchGroupEndpoint.class)
public class MatchGroupEndpointTest {

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
    private GroupMapper groupMapper;

    @MockBean
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webAppContext;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    public void postGroupRelationship_validInput_returnsCreated() throws Exception {
        // given
        GroupRelationshipDto inputDto = new GroupRelationshipDto(1L,2L, RelStatus.LIKED);
        GroupRelationshipDto outputDto = new GroupRelationshipDto(1L,2L, RelStatus.LIKED);
        GroupRelationship groupRelationship = new GroupRelationship(
            Student.StudentBuilder.aStudent().withId(1L).build(),
            Group.GroupBuilder.aGroup().withId(2L).build(),
            RelStatus.LIKED
        );

        when(matchService.postGroupRelationship(any())).thenReturn(groupRelationship);
        when(matchMapper.groupRelToGroupRelDto(any())).thenReturn(outputDto);

        // when
        mockMvc.perform(post("/api/v1/match/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))

            // then
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(outputDto)));
    }

    @Test
    public void getRecommendedGroup_validId_returnsOk() throws Exception {
        // given
        Long id = 1L;

        when(recommendationService.getRecommendedGroup(id)).thenReturn(Group.GroupBuilder.aGroup().withId(2L).build());

        // when
        mockMvc.perform(get("/api/v1/match/group/" + id))

            // then
            .andExpect(status().isOk());
    }
}
