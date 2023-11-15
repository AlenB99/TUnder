package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupMemberDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedStudentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Mapper(uses = {GroupMemberMapper.class, StudentMapper.class})
public interface GroupMapper {

    List<DetailedGroupDto> groupToDetailedGroupDtoList(List<Group> groups);

    //@Mapping(target = "groupLeaderId", source = "groupLeaderId")
    DetailedGroupDto groupToDetailedGroupDto(Group group);

    DetailedGroupMemberDto groupMemberToDetailedGroupMemberDto(GroupMember groupMember);

    List<DetailedGroupMemberDto> groupMemberToDetailedGroupMemberDtoList(List<GroupMember> groupMembers);

    //@Mapping(target = "groupLeaderId", source = "groupLeaderId")
    Group detailedGroupDtoToGroup(DetailedGroupDto detailedGroupDto);

}