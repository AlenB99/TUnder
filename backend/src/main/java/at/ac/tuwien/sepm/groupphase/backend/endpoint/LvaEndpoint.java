package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LvaDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LvaMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.LvaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Endpoint class for Lvas.
 **/
@RestController
@RequestMapping(value = "/api/v1/lvas")
public class LvaEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LvaService lvaService;
    private final LvaMapper lvaMapper;

    @Autowired
    public LvaEndpoint(LvaService lvaService, LvaMapper lvaMapper) {
        this.lvaService = lvaService;
        this.lvaMapper = lvaMapper;
    }

    /**
     * Method to retrieve all persisted lvas from the repository.
     **/
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get a simple list of all lvas", security = @SecurityRequirement(name = "apiKey"))
    public List<LvaDto> findAll() {
        LOGGER.info("GET /api/v1/lvas");
        List<LvaDto> test = lvaMapper.lvaToLvaDto(lvaService.findAll());
        return lvaMapper.lvaToLvaDto(lvaService.findAll());
    }

    /**
     * Method to persist an lva to the repository.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Persist a lva", security = @SecurityRequirement(name = "apiKey"))
    public LvaDto create(@Valid @RequestBody LvaDto lvaDto) throws ValidationException {
        LOGGER.info("POST /api/v1/students body: {}", lvaDto);
        return lvaMapper.lvaToLvaDto(
            lvaService.persistLva(lvaMapper.lvaDtotoLva(lvaDto)));
    }
}
