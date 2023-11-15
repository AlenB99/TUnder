package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupWeight")
public class GroupWeight implements Weight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne (cascade = CascadeType.REMOVE)
    @JoinColumn(name = "student_id", unique = true)
    private Student student;
    @Column(name = "current_lvas")
    private double currentLvas;
    @Column(name = "pref_language")
    private double prefLanguage;
    @Column(name = "meets_irl")
    private double meetsIrl;
    @Column(name = "group_size")
    private double groupSize;

    public GroupWeight() {
    }

    public GroupWeight(Student student, double value) {
        this.student = student;
        this.currentLvas = value;
        this.prefLanguage = value;
        this.meetsIrl = value;
        this.groupSize = value;
    }

    public GroupWeight(Student student, double currentLvas, double prefLanguage, double meetsIrl, double groupSize) {
        this.student = student;
        this.currentLvas = currentLvas;
        this.prefLanguage = prefLanguage;
        this.meetsIrl = meetsIrl;
        this.groupSize = groupSize;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public double getCurrentLvas() {
        return currentLvas;
    }

    public void setCurrentLvas(double currentLvas) {
        this.currentLvas = currentLvas;
    }

    public double getPrefLanguage() {
        return prefLanguage;
    }

    public void setPrefLanguage(double prefLanguage) {
        this.prefLanguage = prefLanguage;
    }

    public double getMeetsIrl() {
        return meetsIrl;
    }

    public void setMeetsIrl(double meetsIrl) {
        this.meetsIrl = meetsIrl;
    }

    public double getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(double groupSize) {
        this.groupSize = groupSize;
    }

    public double[] getVector() {
        return new double[] {currentLvas, prefLanguage, meetsIrl, groupSize};
    }

    public double get(int i) {
        switch (i) {
            case 0:
                return getCurrentLvas();
            case 1:
                return getPrefLanguage();
            case 2:
                return getMeetsIrl();
            case 3:
                return getGroupSize();
            default:
                return 0;
        }
    }

    public boolean set(int i, double val) {
        switch (i) {
            case 0:
                setPrefLanguage(val);
                return true;
            case 1:
                setCurrentLvas(val);
                return true;
            case 2:
                setMeetsIrl(val);
                return true;
            case 3:
                setGroupSize(val);
                return true;
            default:
                return false;
        }
    }
}
