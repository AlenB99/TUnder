package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity class for group members.
 **/
@Entity
@Table(name = "GroupMember")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "group_id")
    private Group studyGroup;

    @ManyToOne()
    @JoinColumn(name = "member_id")
    private Student student;

    public GroupMember() {
    }

    public void setStudent(Student student) {
        if (this.student != null) {
            this.student.getGroupMembers().remove(this);
        }
        this.student = student;
        if (student != null && !student.getGroupMembers().contains(this)) {
            student.getGroupMembers().add(this);
        }
    }


    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Group getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(Group studyGroup) {
        this.studyGroup = studyGroup;
    }

    // Getters and setters


    public GroupMember(Group studyGroup, Student student) {
        this.studyGroup = studyGroup;
        this.student = student;
    }


    public static class GroupMemberBuilder {
        GroupMember member;

        public GroupMemberBuilder() {
            this.member = new GroupMember();
        }

        public static GroupMemberBuilder aGroupMember() {
            return new GroupMemberBuilder();
        }

        public GroupMemberBuilder withStudent(Student student) {
            member.student = student;
            return this;
        }

        public GroupMemberBuilder withStudyGroup(Group group) {
            member.studyGroup = group;
            return this;
        }

        public GroupMember build() {
            return member;
        }
    }
}
