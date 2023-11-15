package at.ac.tuwien.sepm.groupphase.backend.entity;


import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;

import java.util.List;

/**
 * Entity class for Filters.
 **/
@Entity
@Table(name = "filter")
public class Filter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", unique = true)
    private Student student;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @ManyToOne
    @JoinColumn(name = "pref_language")
    private Language prefLanguage;

    @Column(name = "meets_irl")
    private Boolean meetsIrl;

    @Column(name = "group_recom_mode", columnDefinition = "boolean default false")
    private Boolean groupRecomMode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "filter_lva",
        joinColumns = @JoinColumn(name = "filter_id"),
        inverseJoinColumns = @JoinColumn(name = "lva_filter_id"))
    private List<Lva> lvas;


    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

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

    public List<Lva> getLvas() {
        return lvas;
    }

    public void setLvas(List<Lva> lvas) {
        this.lvas = lvas;
    }

    public Language getPrefLanguage() {
        return prefLanguage;
    }

    public void setPrefLanguage(Language prefLanguage) {
        this.prefLanguage = prefLanguage;
    }

    public Boolean getMeetsIrl() {
        return meetsIrl;
    }

    public void setMeetsIrl(Boolean meetsIrl) {
        this.meetsIrl = meetsIrl;
    }

    @PreRemove
    private void removeFilterFromStudent() {
        this.student.setFilter(null);
    }

    public Boolean getGroupRecomMode() {
        return groupRecomMode;
    }

    public void setGroupRecomMode(Boolean searchGroups) {
        this.groupRecomMode = searchGroups;
    }


    public static final class FilterBuilder {
        private Filter filter = new Filter();

        private FilterBuilder() {
        }

        public static FilterBuilder aFilter() {
            return new FilterBuilder();
        }

        public FilterBuilder withId(Long id) {
            this.filter.id = id;
            return this;
        }

        public FilterBuilder withMinAge(Integer age) {
            this.filter.minAge = age;
            return this;
        }

        public FilterBuilder withMaxAge(Integer age) {
            this.filter.maxAge = age;
            return this;
        }

        public FilterBuilder withStudent(Student student) {
            this.filter.student = student;
            return this;
        }

        public FilterBuilder withStudent(Long studentId) {
            this.filter.student = Student.StudentBuilder.aStudent().withId(studentId).build();
            return this;
        }

        public FilterBuilder withPrefLanguage(Language prefLanguage) {
            this.filter.prefLanguage = prefLanguage;
            return this;
        }

        public FilterBuilder withMeetsIrl(Boolean meetsIrl) {
            this.filter.meetsIrl = meetsIrl;
            return this;
        }

        public FilterBuilder withLvas(List<Lva> lvas) {
            this.filter.lvas = lvas;
            return this;
        }

        public FilterBuilder withGroupRecomMode(Boolean groupRecomMode) {
            this.filter.groupRecomMode = groupRecomMode;
            return this;
        }

        public Filter build() {
            return filter;
        }
    }
}
