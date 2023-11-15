package at.ac.tuwien.sepm.groupphase.backend.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "IndividualMessage")
public class IndividualMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "sender_id")
    private Student sender;

    @ManyToOne()
    @JoinColumn(name = "relationship_id")
    private SingleRelationship relationship;

    @Column
    private Timestamp timestamp;

    @Column
    private String content;

    public Student getSender() {
        return sender;
    }

    public IndividualMessage setSender(Student sender) {
        this.sender = sender;
        return this;
    }

    public SingleRelationship getRelationship() {
        return relationship;
    }

    public IndividualMessage setRelationship(SingleRelationship relationship) {
        this.relationship = relationship;
        return this;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public IndividualMessage setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getContent() {
        return content;
    }

    public IndividualMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setReceiver(Student s) {
        this.sender = s;
    }
}
