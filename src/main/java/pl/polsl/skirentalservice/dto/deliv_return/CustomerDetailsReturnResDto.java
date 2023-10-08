/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.deliv_return;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDetailsReturnResDto {
    private String fullName;
    private String pesel;
    private String phoneNumber;
    private String email;
    private String address;
}
