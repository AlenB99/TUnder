package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record SettingsDto(
    Long id,
    boolean isSleeping,
    boolean isSubscribing,
    boolean hideLastName,
    boolean hideFirstName,
    boolean hideAge,
    boolean hideEmail,
    boolean hideGender,
    long studentId,
    SimpleStudentDto student) {

    @Override
    public Long id() {
        return id;
    }

    @Override
    public SimpleStudentDto student() {
        return student;
    }

    @Override
    public boolean isSleeping() {
        return isSleeping;
    }

    @Override
    public boolean isSubscribing() {
        return isSubscribing;
    }

    @Override
    public boolean hideLastName() {
        return hideLastName;
    }

    @Override
    public boolean hideFirstName() {
        return hideFirstName;
    }

    @Override
    public boolean hideAge() {
        return hideAge;
    }

    @Override
    public boolean hideEmail() {
        return hideEmail;
    }

    @Override
    public boolean hideGender() {
        return hideGender;
    }

    @Override
    public long studentId() {
        return studentId;
    }
}
