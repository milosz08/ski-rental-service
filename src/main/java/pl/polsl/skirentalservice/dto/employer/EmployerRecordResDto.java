package pl.polsl.skirentalservice.dto.employer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.util.Gender;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerRecordResDto {
    private Long id;
    private String fullName;
    private LocalDate hiredDate;
    private String pesel;
    private String email;
    private String phoneNumber;
    private Gender gender;
}
