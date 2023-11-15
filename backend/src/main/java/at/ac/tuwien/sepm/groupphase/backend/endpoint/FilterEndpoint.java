package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FilterMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.FilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/filter")
public class FilterEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FilterService filterService;
    private final FilterMapper filterMapper;

    @Autowired
    public FilterEndpoint(FilterService filterService, FilterMapper filterMapper) {
        this.filterService = filterService;
        this.filterMapper = filterMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Create a filter for student specified in filter", security = @SecurityRequirement(name = "apiKey"))
    public FilterDto create(@RequestBody FilterDto filter) throws ValidationException, ConflictException {
        LOGGER.info("POST /api/v1/filter body: {}", filter);
        return filterMapper.filterToFilterDto(filterService.setFilter(filterMapper.filterDtoToFilter(filter)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get filter", security = @SecurityRequirement(name = "apiKey"))
    public FilterDto get(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/filter/{}", id);
        return filterMapper.filterToFilterDto(filterService.getFilter(id));
    }

    @Secured("ROLE_USER")
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get filter", security = @SecurityRequirement(name = "apiKey"))
    public FilterDto getForStudent(@PathVariable Long studentId) {
        LOGGER.info("GET /api/v1/filter/{}", studentId);
        return filterMapper.filterToFilterDto(filterService.getFilterByStudent(studentId));
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete filter (removes filter from Student as well)", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/filter/{}", id);
        filterService.removeFilter(id);
    }
}
