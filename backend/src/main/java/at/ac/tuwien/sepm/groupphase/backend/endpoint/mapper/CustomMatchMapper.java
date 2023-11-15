package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChatDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GroupRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SingleRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.ChatType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Class to map matches from entities to Dtos
 * and vice versa.
 */
@Component
public class CustomMatchMapper implements MatchMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Convert a match dto object to a {@link SingleRelationship}.
     *
     * @param dto the matchDto to convert
     * @return the converted {@link SingleRelationship}
     */
    public SingleRelationship singleRelDtoToSingleRel(SingleRelationshipDto dto) {
        LOGGER.trace("MatchMapper dto To entity: {}", dto);
        return new SingleRelationship(
            dto.getUser(),
            dto.getRecommended(),
            dto.status()
        );
    }

    /**
     * Convert a match entity object to a {@link SingleRelationshipDto}.
     *
     * @param singleRel the match entity to convert
     * @return the converted {@link SingleRelationshipDto}
     */
    public SingleRelationshipDto singleRelToSingleRelDto(SingleRelationship singleRel) {
        LOGGER.trace("MatchMapper entity To dto: {}", singleRel);
        return new SingleRelationshipDto(
            singleRel.getUserId(),
            singleRel.getRecommendedId(),
            singleRel.getStatus()
        );
    }

    /**
     * Convert a group-match-dto object to a {@link GroupRelationship}.
     *
     * @param dto the group-matchDto to convert
     * @return the converted {@link GroupRelationship}
     */
    public GroupRelationship groupRelDtoToGroupRel(GroupRelationshipDto dto) {
        LOGGER.trace("MatchMapper dto To entity: {}", dto);
        return new GroupRelationship(
            dto.getUser(),
            dto.getRecommended(),
            dto.status()
        );
    }

    /**
     * Convert a group-match-entity object to a {@link GroupRelationshipDto}.
     *
     * @param groupRel the match entity to convert
     * @return the converted {@link GroupRelationshipDto}
     */
    public GroupRelationshipDto groupRelToGroupRelDto(GroupRelationship groupRel) {
        LOGGER.trace("MatchMapper entity To dto: {}", groupRel);
        return new GroupRelationshipDto(
            groupRel.getUser().getId(),
            groupRel.getRecommendedGroup().getId(),
            groupRel.getStatus()
        );
    }

    public SimpleStudentDto singleRelToSimpleStudentDto(SingleRelationship singleRel, Student student) {
        Student likedStudent = singleRel.getRecommended();
        String image = this.mapByteArrayToString(likedStudent.getImageUrl());
        return new SimpleStudentDto(likedStudent.getId(),
                                    likedStudent.getEmail(),
                                    likedStudent.getFirstName(),
                                    likedStudent.getLastName(),
                                    image);
    }


    public List<SimpleStudentDto> groupRelsToSimpleStudents(List<GroupRelationship> groupRelationships) {
        List<SimpleStudentDto> result = new ArrayList<>();
        for (var relationShip : groupRelationships) {
            SimpleStudentDto simpleStudentDto = new SimpleStudentDto(
                relationShip.getUser().getId(),
                relationShip.getUser().getEmail(),
                relationShip.getUser().getFirstName(),
                relationShip.getUser().getLastName(),
                null);
            result.add(simpleStudentDto);
        }
        return result;
    }


}
