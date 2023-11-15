package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Language;

import java.util.List;

public record DetailedGroupDto(
    Long id,
    String name,
    String description,
    String image,
    Long groupLeaderId,
    Language prefLanguage,
    List<DetailedGroupMemberDto> members
) {

    public DetailedGroupDto withId(long newId) {
        return new DetailedGroupDto(
            newId,
            name,
            image,
            description,
            groupLeaderId,
            prefLanguage,
            members
        );
    }

}
