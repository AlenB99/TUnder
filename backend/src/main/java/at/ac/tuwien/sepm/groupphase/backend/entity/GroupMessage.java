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

/**
 * Entity class for group messages.
 **/
@Entity
@Table(name = "GroupMessage")
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "group_sender_id")
    private Student sender;

    @ManyToOne()
    @JoinColumn(name = "group_id")
    private Group receiver;

    @Column
    private Timestamp timestamp;

    @Column
    private String content;

    public Student getSender() {
        return sender;
    }

    public GroupMessage setSender(Student sender) {
        if (this.sender != null) {
            this.sender.getGroupMessages().remove(this);
        }
        this.sender = sender;
        if (sender != null) {
            if (!sender.getGroupMessages().contains(this)) {
                sender.getGroupMessages().add(this);
            }
        }
        return this;
    }

    public Group getReceiver() {
        return receiver;
    }

    public GroupMessage setReceiver(Group receiver) {
        this.receiver = receiver;
        return this;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public GroupMessage setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getContent() {
        return content;
    }

    public GroupMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public Long getId() {
        return id;
    }
}
