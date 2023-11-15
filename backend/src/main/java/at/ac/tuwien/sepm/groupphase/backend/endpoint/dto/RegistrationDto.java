package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDate;

/**
 * DTO to register new user.
 *
 * @param email email of the new student.
 * @param password password of the new student.
 * @param repeatedPassword repeated password of the new student.
 */
public record RegistrationDto(
    String email,
    String password,
    String repeatedPassword,
    String firstName,
    String lastName,
    LocalDate dateOfBirth
){
}
