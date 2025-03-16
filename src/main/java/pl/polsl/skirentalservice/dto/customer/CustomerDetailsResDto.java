package pl.polsl.skirentalservice.dto.customer;

import pl.polsl.skirentalservice.util.Gender;

public record CustomerDetailsResDto(
    Long id,
    String fullName,
    String email,
    String bornDate,
    String pesel,
    String phoneNumber,
    Integer yearsAge,
    Gender gender,
    String cityWithPostCode,
    String address
) {
}
