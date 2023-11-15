package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.GroupEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomGroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMemberMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GroupEndpoint.class)
public class GroupEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityProperties securityProperties;

    @Mock
    private SecurityConfig securityConfig;

    @MockBean
    private GroupMapper groupMapper;

    @MockBean
    private StudentMapper studentMapper;

    @MockBean
    private CustomGroupMapper customGroupMapper;

    @MockBean
    private GroupMemberMapper groupMemberMapper;

    @MockBean
    private CustomMatchMapper matchMapper;

    @MockBean
    private GroupService groupService;

    @MockBean
    private MatchService matchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webAppContext;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }


}
