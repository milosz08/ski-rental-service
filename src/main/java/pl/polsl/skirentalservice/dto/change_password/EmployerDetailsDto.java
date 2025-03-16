package pl.polsl.skirentalservice.dto.change_password;

public record EmployerDetailsDto(
    Long id,
    String login,
    String fullName,
    String emailAddress
) {
}
