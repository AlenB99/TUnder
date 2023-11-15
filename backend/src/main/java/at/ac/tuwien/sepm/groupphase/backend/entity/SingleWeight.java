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
@Table(name = "SingleWeight")
public class SingleWeight implements Weight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne (cascade = CascadeType.REMOVE)
    @JoinColumn(name = "student_id", unique = true)
    private Student student;
    @Column(name = "age")
    private double age;
    @Column(name = "current_lvas")
    private double currentLvas;
    @Column(name = "completed_lvas")
    private double completedLvas;
    @Column(name = "pref_language")
    private double prefLanguage;
    @Column(name = "gender")
    private double gender;
    @Column(name = "meets_irl")
    private double meetsIrl;

    public SingleWeight() {

    }

    public SingleWeight(Student student, double value) {
        this.student = student;
        this.age = value;
        this.currentLvas = value;
        this.completedLvas = value;
        this.prefLanguage = value;
        this.gender = value;
        this.meetsIrl = value;
    }

    public SingleWeight(Student student, double age, double currentLvas, double completedLvas, double prefLanguage, double gender, double meetsIrl) {
        this.student = student;
        this.age = age;
        this.currentLvas = currentLvas;
        this.completedLvas = completedLvas;
        this.prefLanguage = prefLanguage;
        this.gender = gender;
        this.meetsIrl = meetsIrl;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public double getCurrentLvas() {
        return currentLvas;
    }

    public void setCurrentLvas(double courses) {
        this.currentLvas = courses;
    }

    public double getCompletedLvas() {
        return completedLvas;
    }

    public void setCompletedLvas(double completedLvas) {
        this.completedLvas = completedLvas;
    }

    public double getPrefLanguage() {
        return prefLanguage;
    }

    public void setPrefLanguage(double prefLanguage) {
        this.prefLanguage = prefLanguage;
    }

    public double getGender() {
        return gender;
    }

    public void setGender(double gender) {
        this.gender = gender;
    }

    public double getMeetsIrl() {
        return meetsIrl;
    }

    public void setMeetsIrl(double meetsIrl) {
        this.meetsIrl = meetsIrl;
    }

    public double[] getVector() {
        return new double[] {age, currentLvas, completedLvas, prefLanguage, gender, meetsIrl};
    }

    public double get(int i) {
        switch (i) {
            case 0:
                return getAge();
            case 1:
                return getCurrentLvas();
            case 2:
                return getCompletedLvas();
            case 3:
                return getPrefLanguage();
            case 4:
                return getGender();
            case 5:
                return getMeetsIrl();
            default:
                return 0;
        }
    }

    public boolean set(int i, double val) {
        switch (i) {
            case 0:
                setAge(val);
                return true;
            case 1:
                setCurrentLvas(val);
                return true;
            case 2:
                setCompletedLvas(val);
                return true;
            case 3:
                setPrefLanguage(val);
                return true;
            case 4:
                setGender(val);
                return true;
            case 5:
                setMeetsIrl(val);
                return true;
            default:
                return false;
        }
    }
}
