package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GroupInviteDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GroupRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomGroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CustomMatchMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GroupMemberMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.StudentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.GroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.MatchService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupRelationshipValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.GroupValidator;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.StudentValidator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoint class for Students.
 **/
@RestController
@RequestMapping(value = "/api/v1/groups")
public class GroupEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GroupService groupService;
    private final MatchService matchService;
    private final GroupMapper groupMapper;
    private final CustomMatchMapper matchMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final CustomGroupMapper customGroupMapper;
    private final StudentMapper studentMapper;
    private final SecurityProperties securityProperties;
    private final GroupValidator groupValidator;
    private final GroupRelationshipValidator groupRelationshipValidator;
    private final StudentValidator studentValidator;

    @Autowired
    public GroupEndpoint(GroupService groupService, MatchService matchService, GroupMapper groupMapper,
                         GroupMemberMapper groupMemberMapper, StudentMapper studentMapper,
                         SecurityProperties securityProperties, GroupValidator groupValidator,
                         GroupRelationshipValidator groupRelationshipValidator, StudentValidator studentValidator) {
        this.groupService = groupService;
        this.matchService = matchService;
        this.groupMapper = groupMapper;
        this.groupValidator = groupValidator;
        this.groupRelationshipValidator = groupRelationshipValidator;
        this.studentValidator = studentValidator;
        this.matchMapper = new CustomMatchMapper();
        this.groupMemberMapper = groupMemberMapper;
        this.studentMapper = studentMapper;
        this.securityProperties = securityProperties;
        this.customGroupMapper = new CustomGroupMapper(studentMapper, groupMemberMapper);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get a simple list of all groups", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailedGroupDto> findAll() {
        LOGGER.info("GET /api/v1/groups");
        List<Group> group = groupService.findAll();
        return groupMapper.groupToDetailedGroupDtoList(groupService.findAll());
    }

    /**
     * Method to get all groups with a matching leaderId.
     **/
    @Secured("ROLE_USER")
    @GetMapping(value = "leader/{leaderId}")
    @Operation(summary = "Get a list of all groups with leaderId", security = @SecurityRequirement(name = "apiKey"))
    public List<DetailedGroupDto> findByLeaderId(@PathVariable Long leaderId) {
        var f = groupMapper.groupToDetailedGroupDtoList(groupService.getGroupsByGroupLeaderId(leaderId));
        LOGGER.info("GET /api/v1/groups/leader/{}", leaderId);
        return groupMapper.groupToDetailedGroupDtoList(groupService.getGroupsByGroupLeaderId(leaderId));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific group", security = @SecurityRequirement(name = "apiKey"))
    public DetailedGroupDto find(@PathVariable Long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/groups/{}", id);
        return groupMapper.groupToDetailedGroupDto(groupService.findGroupById(id));
    }

    /**
     * Method to update a group.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a group", security = @SecurityRequirement(name = "apiKey"))
    public DetailedGroupDto update(@PathVariable long id, @RequestBody DetailedGroupDto detailedGroupDto) throws ValidationException {
        LOGGER.info("PUT /api/v1/groups/ body: {}", detailedGroupDto.withId(id));
        //Group oldGroup = groupService.findGroupById(groupMapper.detailedGroupDtoToGroup(detailedGroupDto).getId());
        var group = groupMapper.detailedGroupDtoToGroup(detailedGroupDto);
        ArrayList<GroupMember> members = new ArrayList<>();
        for (var member : detailedGroupDto.members()) {
            GroupMember output = new GroupMember();
            groupMemberMapper.detailedGroupMemberDtoToGroupMember(member, output);
            members.add(output);
        }
        group.setMembers(members);

        return groupMapper.groupToDetailedGroupDto(
            groupService.updateGroup(group));
    }

    /**
     * Method to invite a student to a group via mat. number.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping(value = "/addUser/{matnr}")
    @Operation(summary = "Update a group", security = @SecurityRequirement(name = "apiKey"))
    public void addStudentByMatr(@PathVariable String matnr, @RequestBody Group group) throws NotFoundException, ValidationException {
        try {
            LOGGER.info("PUT /api/v1/groups/sendInvite/{} body: {}", matnr, group.getId());
            matchService.inviteToGroup(matnr, group.getId());
        } catch (NotFoundException | ValidationException e) {
            // Handle the exceptions internally and log them
            LOGGER.error("Exception occurred during inviteToGroup: {}", e.getMessage());
        }
    }

    /**
     * Method to invite a student to a group via id.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/inviteUser/{userId}/{groupId}")
    @Operation(summary = "invite user to group via id", security = @SecurityRequirement(name = "apiKey"))
    public GroupRelationshipDto addStudentById(@PathVariable long userId, @PathVariable long groupId) throws NotFoundException, ValidationException {
        LOGGER.info("PUT /api/v1/groups/sendInvite/{} body: {}", userId, groupId);
        return matchMapper.groupRelToGroupRelDto(
            matchService.inviteToGroup(userId, groupId));
    }

    /**
     * Method to update a group.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/addUser/{id}/{groupId}")
    public DetailedGroupDto addToGroupByAdmin(@PathVariable long id, @PathVariable long groupId) throws NotFoundException {
        LOGGER.info("PUT /api/v1/groups/addUser/ body: {}", groupId);
        Group group = groupService.findGroupById(groupId);
        if (group == null) {
            throw new NotFoundException("The specified group does not exist.");
        }
        return customGroupMapper.groupToGroupDto(
            groupService.addStudentByAdmin(
                group, id)
        );
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/accept/{id}/{groupId}")
    public List<GroupInviteDto> acceptGroupInvite(@PathVariable long id, @PathVariable long groupId) throws NotFoundException {
        LOGGER.info("PUT /api/v1/groups/addUser/ body: {}", groupId);
        Group group = groupService.findGroupById(groupId);
        if (group == null) {
            throw new NotFoundException("The specified group does not exist.");
        }
        List<GroupInviteDto> groupInviteDtos = new ArrayList<>();
        for (GroupRelationship element : groupService.acceptGroupInvite(group, id)) {
            GroupInviteDto groupInviteDto = new GroupInviteDto(element.getRecommendedGroup().getId(), element.getRecommendedGroup().getName());
            groupInviteDtos.add(groupInviteDto);
        }

        return groupInviteDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/declineUser/{id}/{groupId}")
    public List<SimpleStudentDto> declineInvitationByAdmin(@PathVariable long id, @PathVariable long groupId) throws NotFoundException {
        LOGGER.info("PUT /api/v1/groups/addUser/ body: {}", groupId);
        Group group = groupService.findGroupById(groupId);
        if (group == null) {
            throw new NotFoundException("The specified group does not exist.");
        }
        return
            groupService.declineGroupApplication(
                group, id);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/declineInvite/{id}/{groupId}")
    public List<GroupInviteDto> declineInvitation(@PathVariable long id, @PathVariable long groupId) throws NotFoundException {
        LOGGER.info("PUT /api/v1/groups/addUser/ body: {}", groupId);
        Group group = groupService.findGroupById(groupId);
        if (group == null) {
            throw new NotFoundException("The specified group does not exist.");
        }

        List<GroupInviteDto> groupInviteDtos = new ArrayList<>();
        for (GroupRelationship element : groupService.declineGroupInvite(group, id)) {
            GroupInviteDto groupInviteDto = new GroupInviteDto(element.getRecommendedGroup().getId(), element.getRecommendedGroup().getName());
            groupInviteDtos.add(groupInviteDto);
        }

        return groupInviteDtos;
    }

    /**
     * Method to create a group.
     **/
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Operation(summary = "Create a group", security = @SecurityRequirement(name = "apiKey"))
    public DetailedGroupDto post(@RequestBody DetailedGroupDto groupDto) throws ValidationException {
        LOGGER.info("POST /api/v1/groups/ body: {}", groupDto);
        return groupMapper.groupToDetailedGroupDto(
            groupService.createGroup(
                groupMapper.detailedGroupDtoToGroup(groupDto))
        );
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/remove/{id}")
    @Operation(summary = "Remove a user from group", security = @SecurityRequirement(name = "apiKey"))
    public DetailedGroupDto removeUserFromGroup(@PathVariable Long id, @RequestBody Group group, HttpServletRequest request) throws NotAuthorizedException, ValidationException {
        return customGroupMapper.groupToGroupDto(
            groupService.removeUserFromGroup(id, group.getId(), getId(request)));
    }

    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/invites/{id}")
    public List<GroupInviteDto> getAllInvitesForUser(@PathVariable Long id) {
        List<GroupInviteDto> groupInviteDtos = new ArrayList<>();
        for (GroupRelationship element : groupService.loadInvites(id)) {
            GroupInviteDto groupInviteDto = new GroupInviteDto(element.getRecommendedGroup().getId(), element.getRecommendedGroup().getName());
            groupInviteDtos.add(groupInviteDto);
        }
        return groupInviteDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/loadRelationships/{groupId}")
    public List<SimpleStudentDto> getAllStudentsWhoLikedTheGroup(@PathVariable Long groupId, HttpServletRequest request) throws NotAuthorizedException {
        return groupService.loadStudents(groupId, getId(request));
    }

    /**
     * Method to get the id of the caller TODO: should be moved to another class to make it available globally.
     *
     * @param request the incoming request
     * @return the id of the initiator of the request
     */
    private Long getId(HttpServletRequest request) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        String token = request.getHeader("Authorization");
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(signingKey))
            .build()
            .parseClaimsJws(token.replace("Bearer ", ""))
            .getBody()
            .get("id", Long.class);
    }


    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{groupId}/leave/{userId}")
    public void leaveGroup(@PathVariable long groupId, @PathVariable long userId) throws NotFoundException {
        LOGGER.info("DELETE /api/v1/groups/{}/leave/{}", groupId, userId);
        Group group = groupService.findGroupById(groupId);
        if (group == null) {
            throw new NotFoundException("The specified group does not exist.");
        }
        groupService.leaveGroup(groupId, userId);
    }


}