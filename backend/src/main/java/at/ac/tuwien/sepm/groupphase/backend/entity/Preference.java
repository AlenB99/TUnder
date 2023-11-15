package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "Preferene")
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne ()
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

    public Preference() {

    }

    public Preference(Student student) {
        this.student = student;
        age = Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
        this.gender = 0.5;
        this.meetsIrl = 0.5;
        setDefaults();
    }

    public Preference(Student student, double value) {
        this.student = student;
        this.age = value;
        this.gender = value;
        this.meetsIrl = value;
        setDefaults();
    }

    public Preference(Student student, double age, double gender, double meetsIrl) {
        this.student = student;
        this.age = age;
        this.gender = gender;
        this.meetsIrl = meetsIrl;
        setDefaults();
    }

    public static boolean isUpdatableFeature(int i) {
        if (i == 0 || i == 4 || i == 5) {
            return true;
        }
        return false;
    }

    private void setDefaults() {
        this.completedLvas = student.getCompletedLvas() == null ? 0 : student.getCompletedLvas().size();
        this.currentLvas = student.getCurrentLvas() == null ? 0 : student.getCurrentLvas().size();
        this.prefLanguage = 1;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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
                return -1;
        }
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

    public void setCurrentLvas(double currentLvas) {
        this.currentLvas = currentLvas;
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

    public boolean set(int i, double val) {
        switch (i) {
            case 0:
                setAge(val);
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

    public boolean set(int i, double val, boolean setDefaultTypes) {
        switch (i) {
            case 0:
                setAge(val);
                return true;
            case 1:
                if (setDefaultTypes) {
                    setCurrentLvas(val);
                } else {
                    setCurrentLvas(student.getCurrentLvas().size());
                }
                return true;
            case 2:
                if (setDefaultTypes) {
                    setCompletedLvas(val);
                } else {
                    setCompletedLvas(student.getCompletedLvas().size());
                }
                return true;
            case 3:
                if (setDefaultTypes) {
                    setPrefLanguage(val);
                } else {
                    setPrefLanguage(1);
                }
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
