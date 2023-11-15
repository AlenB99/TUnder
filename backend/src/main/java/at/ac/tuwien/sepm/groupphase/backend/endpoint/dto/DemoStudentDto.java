package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record DemoStudentDto(
    String firstname,
    String lastname,
    double ageDistance,
    double currentLvaDistance,
    double completedLvaDistance,
    double languageDistance,
    double genderDistance,
    double meetsIrlDistance,
    double overallDistance
) {
    @Override
    public String firstname() {
        return firstname;
    }

    @Override
    public String lastname() {
        return lastname;
    }

    @Override
    public double ageDistance() {
        return ageDistance;
    }

    @Override
    public double currentLvaDistance() {
        return currentLvaDistance;
    }

    @Override
    public double completedLvaDistance() {
        return completedLvaDistance;
    }

    @Override
    public double languageDistance() {
        return languageDistance;
    }

    @Override
    public double genderDistance() {
        return genderDistance;
    }

    @Override
    public double meetsIrlDistance() {
        return meetsIrlDistance;
    }

    @Override
    public double overallDistance() {
        return overallDistance;
    }
}
