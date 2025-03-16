package pl.polsl.skirentalservice.dto.employer;

import pl.polsl.skirentalservice.util.Gender;

public record EmployerDetailsResDto(
    Long id,
    String fullName,
    String login,
    String email,
    String bornDate,
    String hiredDate,
    String pesel,
    String phoneNumber,
    Integer yearsAge,
    Integer yearsHired,
    Gender gender,
    String cityWithPostCode,
    String accountState,
    String accountStateColor,
    String address
) {
}
