package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity class for group recommendations.
 **/
@Entity
@Table(name = "GroupRelationship")
public class GroupRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne ()
    @JoinColumn(name = "user_id")
    private Student user;

    @ManyToOne ()
    @JoinColumn(name = "recommended_id")
    private Group recommendedGroup;

    @Enumerated(EnumType.STRING)
    private RelStatus status;

    public GroupRelationship() {
    }

    public GroupRelationship(Student user, Group recommendedGroup, RelStatus status) {
        this.user = user;
        this.recommendedGroup = recommendedGroup;
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getRecommendedGroup() {
        return recommendedGroup;
    }

    public void setRecommendedGroup(Group recommendedGroup) {
        this.recommendedGroup = recommendedGroup;
    }

    public Student getUser() {
        return user;
    }

    public void setUser(Student user) {
        this.user = user;
    }

    public RelStatus getStatus() {
        return status;
    }

    public void setStatus(RelStatus status) {
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }
}