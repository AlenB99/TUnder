package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LanguageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LanguageMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Endpoint class for Languages.
 **/
@RestController
@RequestMapping(value = "/api/v1/languages")
public class LanguageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LanguageService languageService;
    private final LanguageMapper languageMapper;

    @Autowired
    public LanguageEndpoint(LanguageService languageService, LanguageMapper languageMapper) {
        this.languageService = languageService;
        this.languageMapper = languageMapper;
    }

    /**
     * Method to retrieve all persisted languages from the repository.
     **/
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get a simple list of all languages", security = @SecurityRequirement(name = "apiKey"))
    public List<LanguageDto> findAll() {
        LOGGER.info("GET /api/v1/languages");
        List<LanguageDto> test = languageMapper.languageToLanguageDto(languageService.findAll());
        return languageMapper.languageToLanguageDto(languageService.findAll());
    }
}
