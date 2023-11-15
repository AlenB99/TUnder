package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;


/**
 * Entity class for lvas.
 **/
@Entity
@Table(name = "lva")
public class Lva {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "currentLvas", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Student> currentStudents;

    @ManyToMany(mappedBy = "completedLvas", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Student> completedStudents;

    @ManyToMany(mappedBy = "lvas", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Group> groups;

    public Lva(String id, String name, List<Student> currentStudents, List<Student> completedStudents) {
        this.id = id;
        this.name = name;
        this.currentStudents = currentStudents;
        this.completedStudents = completedStudents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getCurrentStudents() {
        return currentStudents;
    }

    public void setCurrentStudents(List<Student> currentStudents) {
        this.currentStudents = currentStudents;
    }

    public List<Student> getCompletedStudents() {
        return completedStudents;
    }

    public void setCompletedStudents(List<Student> completedStudents) {
        this.completedStudents = completedStudents;
    }

    public Lva() {
    }

    @ManyToMany(mappedBy = "lvas")
    private List<Filter> filters;

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lva lva = (Lva) o;

        return Objects.equals(id, lva.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static class LvaBuilder {
        private Lva lva = new Lva();

        public static LvaBuilder aLvaBuilder() {
            return new LvaBuilder();
        }

        public LvaBuilder withId(String id) {
            lva.id = id;
            return this;
        }

        public LvaBuilder withName(String name) {
            lva.name = name;
            return this;
        }

        public LvaBuilder withFilters(List<Filter> filters) {
            lva.filters = filters;
            return this;
        }

        public LvaBuilder withGroups(List<Group> groups) {
            lva.groups = groups;
            return this;
        }

        public Lva build() {
            return lva;
        }
    }
}
