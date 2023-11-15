package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;

public record DetailedGroupMemberDto(
    Long id,
    SimpleStudentDto student
) {

}
