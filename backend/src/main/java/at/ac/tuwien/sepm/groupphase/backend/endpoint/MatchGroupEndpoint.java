package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GroupRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = {"/api/v1/match/group"})
public class MatchGroupEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MatchService matchService;
    private final CustomMatchMapper matchMapper;
    private final GroupMapper groupMapper;
    private final RecommendationService recommendationService;


    @Autowired
    public MatchGroupEndpoint(MatchService matchService,
                              GroupMapper groupMapper,
                              RecommendationService recommendationService) {
        LOGGER.trace("Create new MatchEndpoint instance");
        this.groupMapper = groupMapper;
        this.matchService = matchService;
        this.recommendationService = recommendationService;
        this.matchMapper = new CustomMatchMapper();
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Update relationship of user to recommended user", security = @SecurityRequirement(name = "apiKey"))
    public GroupRelationshipDto postGroupRelationship(@Valid @RequestBody GroupRelationshipDto groupRelDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/match/group body: {}", groupRelDto);
        GroupRelationship groupRel = matchMapper.groupRelDtoToGroupRel(groupRelDto);
        return matchMapper.groupRelToGroupRelDto(
            matchService.postGroupRelationship(groupRel));

    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get next recommended group", security = @SecurityRequirement(name = "apiKey"))
    public DetailedGroupDto getRecommendedGroup(@PathVariable Long id) throws ValidationException {
        LOGGER.info("GET /api/v1/match/group/{}", id);
        return groupMapper.groupToDetailedGroupDto(recommendationService.getRecommendedGroup(id));
    }

}
