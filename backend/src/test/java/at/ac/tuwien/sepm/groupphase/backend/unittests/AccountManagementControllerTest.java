package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.AccountManagementController;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangeAccountDataDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.service.RegistrationService;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AccountManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @Test
    public void signUp_success() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto("test@test.com", "test123", "test123","YOYOYOYOY","YOYOYOYO", null);

        mockMvc.perform(post("/api/v1/registration/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(registrationDto)))
            .andExpect(status().isOk());

        verify(registrationService, times(1)).signUpUser(registrationDto);
    }

    @Test
    public void forgotPassword_success() throws Exception {
        SimpleStudentDto simpleStudentDto = new SimpleStudentDto(1L,"test@test.com", "test123", "test123","YOYOYOYOY");
        mockMvc.perform(post("/api/v1/registration/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(simpleStudentDto)))
            .andExpect(status().isOk());

        verify(registrationService, times(1)).forgotPassword("test@test.com");
    }

    @Test
    public void resetPassword_success() throws Exception {
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto("testToken", "newPassword");

        mockMvc.perform(post("/api/v1/registration/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(resetPasswordDto)))
            .andExpect(status().isOk());

        verify(registrationService, times(1)).resetPassword(resetPasswordDto);
    }
}