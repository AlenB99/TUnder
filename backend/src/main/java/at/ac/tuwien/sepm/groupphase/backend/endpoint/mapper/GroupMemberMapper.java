package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGroupMemberDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.GroupMember;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {GroupMemberMapper.class, StudentMapper.class})
public interface GroupMemberMapper {
    void detailedGroupMemberDtoToGroupMember(DetailedGroupMemberDto detailedGroupMemberDto, @MappingTarget GroupMember member);
}
