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
@Table(name = "GroupPreference")
public class GroupPreference {
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

    public GroupPreference() {

    }

    public GroupPreference(Student student) {
        this.student = student;
        this.meetsIrl = 0.5;
        this.groupSize = 8;
        setDefaults();

    }

    public GroupPreference(Student student, double value) {
        this.student = student;
        this.meetsIrl = value;
        this.groupSize = value;
        setDefaults();
    }

    public GroupPreference(Student student, double meetsIrl, double groupSize) {
        this.meetsIrl = meetsIrl;
        this.groupSize = groupSize;
        setDefaults();
    }

    public static boolean isUpdatableFeature(int feature) {
        if (feature == 0 || feature == 2 || feature == 3) {
            return true;
        }
        return false;
    }

    private void setDefaults() {
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
                return -1;
        }
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

    public boolean set(int i, double val) {
        switch (i) {
            case 0:
                setPrefLanguage(val);
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

    public boolean set(int i, double val, boolean setDefaultTypes) {
        switch (i) {
            case 0:
                if (setDefaultTypes) {
                    setCurrentLvas(val);
                } else {
                    setCurrentLvas(student.getCurrentLvas().size());
                }
                return true;
            case 1:
                if (setDefaultTypes) {
                    setPrefLanguage(val);
                } else {
                    setPrefLanguage(1);
                }
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
