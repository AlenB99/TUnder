package at.ac.tuwien.sepm.groupphase.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Language")
public class Language {
    @Id
    private String id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "prefLanguage", cascade = CascadeType.REMOVE)
    private List<Student> students;

    @OneToMany(mappedBy = "prefLanguage", cascade = CascadeType.REMOVE)
    private List<Filter> filters;

    @OneToMany(mappedBy = "prefLanguage")
    private List<Group> groups;

    public Language(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Language() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Language language = (Language) o;

        return Objects.equals(id, language.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
}