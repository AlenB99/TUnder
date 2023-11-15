package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Entity class for student.
 **/
@Entity
@Table(name = "Student",
    indexes = {
        @Index(name = "date_of_birth_student", columnList = "date_of_birth"),
        @Index(name = "meets_irl_student", columnList = "meets_irl")
    })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "password")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pref_language")
    private Language prefLanguage;
    @Column(name = "description")
    private String description;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "is_admin")
    private Boolean admin;
    @Column(name = "meets_irl")
    private Boolean meetsIrl;
    @Lob
    @Column(name = "image")
    private byte[] imageUrl;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Settings settings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_lva_current",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "lva_current_id"))
    private List<Lva> currentLvas;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_lva_completed",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "lva_complete_id"))
    private List<Lva> completedLvas;

    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Filter filter;
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<IndividualMessage> messagesSent = new HashSet<>();
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<GroupMessage> groupMessages = new HashSet<>();
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<GroupMember> groupMembers = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<SingleRelationship> singleRelationshipUserList;
    @OneToMany(mappedBy = "recommended", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<SingleRelationship> singleRelationshipRecommendedList;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<GroupRelationship> groupRelationshipList;
    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private SingleWeight weight;
    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Preference preference;
    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private GroupWeight groupWeight;
    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private GroupPreference groupPreference;

    public Student(Long id, String email, String password, String firstName, String lastName,
                   Gender gender, Language prefLanguage, String description, LocalDate dateOfBirth,
                   Boolean admin, Boolean meetsIrl, Settings settings, List<Lva> currentLvas,
                   List<Lva> completedLvas, Filter filter, Set<IndividualMessage> messagesSent,
                   Set<GroupMessage> groupMessages, SingleWeight weight, Preference preference, GroupWeight groupWeight, GroupPreference groupPreference) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.prefLanguage = prefLanguage;
        this.description = description;
        this.dateOfBirth = dateOfBirth;
        this.admin = admin;
        this.meetsIrl = meetsIrl;
        this.settings = settings;
        this.currentLvas = currentLvas;
        this.completedLvas = completedLvas;
        this.filter = filter;
        this.messagesSent = messagesSent;
        this.groupMessages = groupMessages;
        this.weight = weight;
        this.preference = preference;
        this.groupWeight = groupWeight;
        this.groupPreference = groupPreference;
    }


    public Student() {
    }

    @PreRemove
    public void beforeRemoval() {
        if (messagesSent != null) {
            for (IndividualMessage m :
                messagesSent) {
                m.setRelationship(null);
                m.setSender(null);
            }
        }

        if (settings != null) {
            settings.setStudent(null);
        }
        if (filter != null) {
            filter.setStudent(null);
        }
        if (weight != null) {
            weight.setStudent(null);
        }
        if (preference != null) {
            preference.setStudent(null);
        }
        if (groupWeight != null) {
            groupWeight.setStudent(null);
        }
        if (groupPreference != null) {
            groupPreference.setStudent(null);
        }

        if (currentLvas != null) {
            currentLvas.clear();
        }
        if (completedLvas != null) {
            completedLvas.clear();
        }


    }

    public Student(Long userId) {
        this.id = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Language getPrefLanguage() {
        return prefLanguage;
    }

    public void setPrefLanguage(Language prefLanguage) {
        this.prefLanguage = prefLanguage;
    }

    public byte[] getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(byte[] imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getMeetsIrl() {
        return meetsIrl;
    }

    public void setMeetsIrl(Boolean meetsIrl) {
        this.meetsIrl = meetsIrl;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<Lva> getCurrentLvas() {
        return currentLvas;
    }

    public void setCurrentLvas(List<Lva> currentLvas) {
        this.currentLvas = currentLvas;
    }

    public List<Lva> getCompletedLvas() {
        return completedLvas;
    }

    public void setCompletedLvas(List<Lva> completedLvas) {
        this.completedLvas = completedLvas;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Set<IndividualMessage> getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(Set<IndividualMessage> messagesSent) {
        this.messagesSent = messagesSent;
    }

    public Set<GroupMessage> getGroupMessages() {
        return groupMessages;
    }

    public void setGroupMessages(Set<GroupMessage> groupMessages) {
        this.groupMessages = groupMessages;
    }

    /*
    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, gender, prefLanguage, description, dateOfBirth, admin, meetsIrl, settings, currentLvas, completedLvas, filter, messagesSent, groupMessages);
    }

     */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student student)) {
            return false;
        }
        return Objects.equals(id, student.id)
            && Objects.equals(email, student.email)
            && Objects.equals(password, student.password)
            && Objects.equals(firstName, student.firstName)
            && Objects.equals(lastName, student.lastName)
            && gender == student.gender
            && prefLanguage == student.prefLanguage
            && Objects.equals(description, student.description)
            && Objects.equals(dateOfBirth, student.dateOfBirth)
            && Objects.equals(admin, student.admin)
            && Objects.equals(meetsIrl, student.meetsIrl)
            && Objects.equals(settings, student.settings)
            && Objects.equals(currentLvas, student.currentLvas)
            && Objects.equals(completedLvas, student.completedLvas)
            && Objects.equals(filter, student.filter)
            && Objects.equals(messagesSent, student.messagesSent)
            && Objects.equals(groupMessages, student.groupMessages);
    }

    public SingleWeight getWeight() {
        return weight;
    }

    public void setWeight(SingleWeight weight) {
        this.weight = weight;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public GroupWeight getGroupWeight() {
        return groupWeight;
    }

    public void setGroupWeight(GroupWeight groupWeight) {
        this.groupWeight = groupWeight;
    }

    public GroupPreference getGroupPreference() {
        return groupPreference;
    }

    public void setGroupPreference(GroupPreference groupPreference) {
        this.groupPreference = groupPreference;
    }

    public List<GroupMember> getGroupMembers() {
        return this.groupMembers;
    }

    public static final class StudentBuilder {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private Gender gender;
        private Language prefLanguage;
        private String description;
        private String password;
        private LocalDate dateOfBirth;
        private Boolean admin;
        private Boolean meetsIrl;
        private Settings settings;
        private List<Lva> currentLvas;
        private List<Lva> completedLvas;
        private Filter filter;
        private byte[] imageUrl;
        private boolean enabled;
        private GroupWeight groupWeight;
        private SingleWeight weight;
        private Preference preference;
        private GroupPreference groupPreference;

        private StudentBuilder() {
        }

        public static Student.StudentBuilder aStudent() {
            return new Student.StudentBuilder();
        }

        public Student.StudentBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Student.StudentBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Student.StudentBuilder withImageUrl(byte[] imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Student.StudentBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Student.StudentBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Student.StudentBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Student.StudentBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Student.StudentBuilder withGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Student.StudentBuilder withPrefLanguage(Language language) {
            this.prefLanguage = language;
            return this;
        }

        public Student.StudentBuilder withDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Student.StudentBuilder withAdmin(Boolean admin) {
            this.admin = admin;
            return this;
        }

        public Student.StudentBuilder withMeetsIrl(Boolean meetsIrl) {
            this.meetsIrl = meetsIrl;
            return this;
        }

        public Student.StudentBuilder withCurrentLvas(List<Lva> lvas) {
            this.currentLvas = lvas;
            return this;
        }

        public Student.StudentBuilder withCompletedLvas(List<Lva> lvas) {
            this.completedLvas = lvas;
            return this;
        }

        public Student.StudentBuilder withFilter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public Student.StudentBuilder withSettings(Settings settings) {
            this.settings = settings;
            return this;
        }

        public Student.StudentBuilder withWeight(SingleWeight weight) {
            this.weight = weight;
            return this;
        }

        public Student.StudentBuilder withGroupWeight(GroupWeight groupWeight) {
            this.groupWeight = groupWeight;
            return this;
        }

        public Student.StudentBuilder withPreference(Preference preference) {
            this.preference = preference;
            return this;
        }

        public Student.StudentBuilder withGroupPreference(GroupPreference groupPreference) {
            this.groupPreference = groupPreference;
            return this;
        }


        public Student.StudentBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Student build() {
            Student student = new Student();
            student.setId(id);
            student.setEmail(email);
            student.setPassword(password);
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setGender(gender);
            student.setPrefLanguage(prefLanguage);
            student.setDescription(description);
            student.setDateOfBirth(dateOfBirth);
            student.setAdmin(admin);
            student.setMeetsIrl(meetsIrl);
            student.setCurrentLvas(currentLvas);
            student.setCompletedLvas(completedLvas);
            student.setFilter(filter);
            student.setSettings(settings);
            student.setEnabled(enabled);
            student.setWeight(weight);
            student.setGroupWeight(groupWeight);
            student.setPreference(preference);
            student.setGroupPreference(groupPreference);
            student.setImageUrl(imageUrl);
            return student;
        }
    }
}
