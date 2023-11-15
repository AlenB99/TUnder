package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entity class for settings.
 **/
@Entity
@Table(name = "Settings")
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Other fields

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private boolean isSleeping;
    private boolean isSubscribing;

    private boolean hideLastname;
    private boolean hideFirstname;
    private boolean hideAge;
    private boolean hideEmail;
    private boolean hideGender;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public void setSleeping(boolean sleeping) {
        isSleeping = sleeping;
    }

    public boolean isSubscribing() {
        return isSubscribing;
    }

    public void setSubscribing(boolean subscribing) {
        isSubscribing = subscribing;
    }

    public boolean isHideLastname() {
        return hideLastname;
    }

    public void setHideLastname(boolean hideLastname) {
        this.hideLastname = hideLastname;
    }

    public boolean isHideFirstname() {
        return hideFirstname;
    }

    public void setHideFirstname(boolean hideFirstname) {
        this.hideFirstname = hideFirstname;
    }

    public boolean isHideAge() {
        return hideAge;
    }

    public void setHideAge(boolean hideAge) {
        this.hideAge = hideAge;
    }

    public boolean isHideEmail() {
        return hideEmail;
    }

    public void setHideEmail(boolean hideEmail) {
        this.hideEmail = hideEmail;
    }

    public boolean isHideGender() {
        return hideGender;
    }

    public void setHideGender(boolean hideGender) {
        this.hideGender = hideGender;
    }
}
