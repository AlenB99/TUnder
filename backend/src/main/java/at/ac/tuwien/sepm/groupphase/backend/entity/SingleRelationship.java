package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.RelStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.List;

/**
 * Represents a relationship between two users in the persistent data store.
 */
@Entity
@Table(name = "SingleRelationship")
public class SingleRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private Student user;

    @ManyToOne()
    @JoinColumn(name = "recommended_id")
    private Student recommended;

    @Enumerated(EnumType.STRING)
    private RelStatus status;

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.REMOVE)
    private List<IndividualMessage> messageList;

    public SingleRelationship() {
    }

    public SingleRelationship(Student user, Student recommended, RelStatus status) {
        this.user = user;
        this.recommended = recommended;
        this.status = status;
    }

    @PreRemove
    public void beforeRemoval() {
        if (messageList != null) {
            for (IndividualMessage m :
                messageList) {
                m.setSender(null);
            }
        }

    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getRecommendedId() {
        return recommended.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(Student userId) {
        this.user = userId;
    }

    public void setRecommended(Student recommendedId) {
        this.recommended = recommendedId;
    }

    public RelStatus getStatus() {
        return status;
    }

    public void setStatus(RelStatus status) {
        this.status = status;
    }

    public Student getUser() {
        return user;
    }

    public Student getRecommended() {
        return recommended;
    }

    public static final class SingleRelationshipBuilder {
        private Student user;
        private Student recommended;

        private RelStatus status;
        private Long id;

        private SingleRelationshipBuilder() {
        }

        public static SingleRelationshipBuilder aSingleRelationship() {
            return new SingleRelationshipBuilder();
        }

        public SingleRelationshipBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SingleRelationshipBuilder withRecommended(Student recommended) {
            this.recommended = recommended;
            return this;
        }

        public SingleRelationshipBuilder withUser(Student user) {
            this.user = user;
            return this;
        }

        public SingleRelationshipBuilder withStatus(RelStatus status) {
            this.status = status;
            return this;
        }

        public SingleRelationship build() {
            SingleRelationship recom = new SingleRelationship();
            recom.setId(id);
            recom.setUser(this.user);
            recom.setRecommended(this.recommended);
            recom.setStatus(this.status);
            return recom;
        }
    }

}


