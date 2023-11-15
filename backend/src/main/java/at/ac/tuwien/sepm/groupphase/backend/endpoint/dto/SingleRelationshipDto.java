package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Student;
import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;

/**
 * Class for SingleRelationship DTOs.
 * Contains all common properties to identify the Relationship
 */
public record SingleRelationshipDto(
    Long userId,
    Long recommendedId,
    RelStatus status
) {
    public Student getUser() {
        if (userId != null) {
            return new Student(userId);
        }
        return null;
    }

    public Student getRecommended() {
        if (recommendedId != null) {
            return new Student(recommendedId);
        }
        return null;
    }
}
