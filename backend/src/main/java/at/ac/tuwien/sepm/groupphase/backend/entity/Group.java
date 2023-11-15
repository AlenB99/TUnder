package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for Groups.
 **/
@Entity
@Table(name = "StudyGroup", indexes = {
    @Index(name = "meets_irl_group", columnList = "meets_irl")
})
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "groupLeader")
    private Long groupLeaderId;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<GroupMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "recommendedGroup", cascade = CascadeType.REMOVE)
    private List<GroupRelationship> relationships = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<GroupMessage> messages;

    @ManyToOne
    @JoinColumn(name = "pref_language")
    private Language prefLanguage;

    @Column(name = "meets_irl")
    private Boolean meetsIrl;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "group_lva",
        joinColumns = @JoinColumn(name = "studyGroup_id"),
        inverseJoinColumns = @JoinColumn(name = "lva_id"))
    private List<Lva> lvas;
    @Lob
    @Column(name = "image")
    private byte[] image;

    public Group(Long groupId) {
        this.id = groupId;
    }

    public Group() {
    }

    public List<Lva> getLvas() {
        return lvas;
    }

    public void setLvas(List<Lva> lvas) {
        this.lvas = lvas;
    }

    public Boolean getMeetsIrl() {
        return meetsIrl;
    }

    public void setMeetsIrl(Boolean meetsIrl) {
        this.meetsIrl = meetsIrl;
    }

    public Language getPrefLanguage() {
        return prefLanguage;
    }

    public void setPrefLanguage(Language prefLanguage) {
        this.prefLanguage = prefLanguage;
    }

    public List<GroupMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<GroupMessage> messages) {
        this.messages = messages;
    }

    public List<GroupMember> getMembers() {
        return this.members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

    public void addGroupMember(GroupMember member) {
        this.members.add(member);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupLeaderId() {
        return groupLeaderId;
    }

    public void setGroupLeaderId(Long groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }

    public static class GroupBuilder {
        private Long id;
        private String name;
        private List<GroupMember> members;
        private List<GroupMessage> messages;
        private Language prefLanguage;
        private Boolean meetsIrl;
        private List<Lva> lvas;
        private String description;
        private Long groupLeaderId;
        private byte[] image;

        public GroupBuilder() {
            // Set default values if needed
            this.description = "";
            this.members = new ArrayList<>();
            this.messages = new ArrayList<>();
            this.lvas = new ArrayList<>();
        }

        public static GroupBuilder aGroup() {
            return new GroupBuilder();
        }

        public GroupBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GroupBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public GroupBuilder withImage(byte[] image) {
            this.image = image;
            return this;
        }

        public GroupBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GroupBuilder withMembers(List<GroupMember> members) {
            this.members = members;
            return this;
        }

        public GroupBuilder withMessages(List<GroupMessage> messages) {
            this.messages = messages;
            return this;
        }

        public GroupBuilder withPrefLanguage(Language prefLanguage) {
            this.prefLanguage = prefLanguage;
            return this;
        }

        public GroupBuilder withMeetsIrl(Boolean meetsIrl) {
            this.meetsIrl = meetsIrl;
            return this;
        }

        public GroupBuilder withGroupLeaderId(long l) {
            this.groupLeaderId = l;
            return this;
        }

        public GroupBuilder withLvas(List<Lva> lvas) {
            this.lvas = lvas;
            return this;
        }

        public Group build() {
            Group group = new Group();
            members.forEach(groupMember -> groupMember.setStudyGroup(group));
            group.setId(this.id);
            group.setName(this.name);
            group.setMembers(this.members);
            group.setImage(this.image);
            group.setDescription(this.description);
            group.setMessages(this.messages);
            group.setPrefLanguage(this.prefLanguage);
            group.setMeetsIrl(this.meetsIrl);
            group.setLvas(this.lvas);
            group.setGroupLeaderId(this.groupLeaderId);
            return group;
        }


    }
}
