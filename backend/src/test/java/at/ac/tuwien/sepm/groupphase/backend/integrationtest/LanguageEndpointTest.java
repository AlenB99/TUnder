package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.LanguageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.StudentEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LanguageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LanguageMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import at.ac.tuwien.sepm.groupphase.backend.service.LanguageService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LanguageEndpoint.class)
public class LanguageEndpointTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private SecurityProperties securityProperties;

    @Mock
    private SecurityConfig securityConfig;

    @MockBean
    private LanguageService languageService;

    @MockBean
    private FilterService filterService;

    @MockBean
    private LanguageMapper languageMapper;

    private LanguageDto languageDto;

    @Autowired
    private WebApplicationContext webAppContext;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findAll_shouldReturn200() throws Exception {
        when(languageService.findAll()).thenReturn(Collections.singletonList(new Language()));
        when(languageMapper.languageToLanguageDto(any(Language.class))).thenReturn(languageDto);

        mockMvc.perform(get("/api/v1/languages").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}
