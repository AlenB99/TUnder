package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupMemberDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SingleRelationshipDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.SingleRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/**
 * Class to map matches from entities to Dtos
 * and vice versa.
 */
public class CustomGroupMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StudentMapper studentMapper;

    private final GroupMemberMapper groupMemberMapper;

    public CustomGroupMapper(StudentMapper studentMapper,
                             GroupMemberMapper groupMemberMapper) {
        this.studentMapper = studentMapper;
        this.groupMemberMapper = groupMemberMapper;
    }

    /**
     * Convert a group dto object to a {@link SingleRelationship}.
     *
     * @param dto the groupDto to convert
     * @return the converted {@link Group}
     */

    public Group groupDtoToGroup(DetailedGroupDto dto) {
        LOGGER.trace("CustomGroupMapper dto To entity: {}", dto);
        Group group = new Group();
        group.setGroupLeaderId(dto.groupLeaderId());
        group.setDescription(dto.description());
        group.setId(dto.id());
        group.setName(dto.name());
        //group.setImage(studentMapper.mapStringToByteArray(dto.image()));
        for (DetailedGroupMemberDto memberDto : dto.members()) {
            GroupMember memberEntity = new GroupMember();
            groupMemberMapper.detailedGroupMemberDtoToGroupMember(memberDto, memberEntity);
            group.addGroupMember(memberEntity);
        }
        return group;
    }



    /**
     * Convert a group entity object to a {@link DetailedGroupDto}.
     *
     * @param group the group entity to convert
     * @return the converted {@link DetailedGroupDto}
     */
    public DetailedGroupDto groupToGroupDto(Group group) {
        LOGGER.trace("CustomGroupMapper entity To dto: {}", group);
        ArrayList<DetailedGroupMemberDto> memberDtos = new ArrayList<>();
        for (GroupMember memberEntity : group.getMembers()) {
            DetailedGroupMemberDto memberDto = new DetailedGroupMemberDto(
                memberEntity.getId(),
                studentMapper.studentToSimpleStudentDto(memberEntity.getStudent())
            );
            memberDtos.add(memberDto);
        }
        String imageString = null;
        if (group.getImage() != null) {
            imageString  = group.getImage().toString();
        }
        DetailedGroupDto dto = new DetailedGroupDto(
            group.getId(),
            group.getName(),
            group.getDescription(),
            imageString,
            group.getGroupLeaderId(),
            group.getPrefLanguage(),
            memberDtos

        );
        return dto;
    }


}
