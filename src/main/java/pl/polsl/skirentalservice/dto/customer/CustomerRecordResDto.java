package pl.polsl.skirentalservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerRecordResDto {
    private Long id;
    private String fullName;
    private String email;
    private String pesel;
    private String phoneNumber;
    private String address;
}
