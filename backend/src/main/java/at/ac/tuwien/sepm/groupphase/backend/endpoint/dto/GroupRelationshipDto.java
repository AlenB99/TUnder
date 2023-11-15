package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Group;
import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;

public record GroupRelationshipDto(

    Long userId,
    Long groupId,
    RelStatus status
) {
    public Student getUser() {
        if (userId != null) {
            return new Student(userId);
        }
        return null;
    }

    public Group getRecommended() {
        if (groupId != null) {
            return new Group(groupId);
        }
        return null;
    }
}
