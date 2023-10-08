/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.change_password;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordEmployerDetailsDto {
    private Long id;
    private Long tokenId;
    private String fullName;

    @Override
    public String toString() {
        return '{' +
            "id=" + id +
            ", fullName='" + fullName +
            '}';
    }
}
