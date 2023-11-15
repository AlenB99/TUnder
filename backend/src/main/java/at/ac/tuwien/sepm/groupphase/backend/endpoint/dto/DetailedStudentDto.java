package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.Gender;
import at.ac.tuwien.sepm.groupphase.backend.entity.Language;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record DetailedStudentDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    Gender gender,
    Language prefLanguage,
    String description,
    LocalDate dateOfBirth,
    Boolean admin,
    Boolean meetsIrl,
    SettingsDto settings,
    @JsonProperty("currentLvas")
    List<LvaDto> currentLvas,
    List<LvaDto> completedLvas,
    FilterDto filter,
    String imageUrl
) {

    @Override
    public Long id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String lastName() {
        return lastName;
    }

    @Override
    public Gender gender() {
        return gender;
    }

    @Override
    public Language prefLanguage() {
        return prefLanguage;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String imageUrl() {
        return imageUrl;
    }

    @Override
    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public Boolean admin() {
        return admin;
    }

    @Override
    public Boolean meetsIrl() {
        return meetsIrl;
    }

    @Override
    public SettingsDto settings() {
        return settings;
    }

    @Override
    public List<LvaDto> currentLvas() {
        return currentLvas;
    }

    @Override
    public List<LvaDto> completedLvas() {
        return completedLvas;
    }


    @Override
    public FilterDto filter() {
        return filter;
    }

    public DetailedStudentDto withId(long newId) {
        return new DetailedStudentDto(
            newId,
            email,
            firstName,
            lastName,
            gender,
            prefLanguage,
            description,
            dateOfBirth,
            admin,
            meetsIrl,
            settings,
            currentLvas,
            completedLvas,
            filter,
            imageUrl
        );
    }
}
