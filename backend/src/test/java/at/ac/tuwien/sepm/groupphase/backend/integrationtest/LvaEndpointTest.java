package at.ac.tuwien.sepm.groupphase.backend.integrationtest;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.LanguageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.LvaEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LanguageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LvaDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LanguageMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LvaMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.entity.Lva;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.LanguageService;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@WebMvcTest(LvaEndpoint.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class LvaEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private SecurityProperties securityProperties;

    @Mock
    private SecurityConfig securityConfig;

    @MockBean
    private LvaService lvaService;

    @MockBean
    private FilterService filterService;

    private LvaDto lvaDto;

    @MockBean
    private LvaMapper lvaMapper;

    @Autowired
    private WebApplicationContext webAppContext;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_shouldReturn200() throws Exception {
        when(lvaService.findAll()).thenReturn(Collections.singletonList(new Lva()));
        when(lvaMapper.lvaToLvaDto(any(Lva.class))).thenReturn(lvaDto);

        mockMvc.perform(get("/api/v1/lvas").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_shouldReturn201() throws Exception {
        when(lvaService.persistLva(any())).thenReturn(new Lva());
        when(lvaMapper.lvaDtotoLva(any())).thenReturn(new Lva());
        when(lvaMapper.lvaToLvaDto(any(Lva.class))).thenReturn(lvaDto);

        mockMvc.perform(post("/api/v1/lvas").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isCreated());
    }


}
