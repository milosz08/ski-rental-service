/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.polsl.skirentalservice.util.Gender;

@Data
@AllArgsConstructor
public class LoggedUserDataDto {
    private Long id;
    private String login;
    private String fullName;
    private String roleName;
    private Character roleAlias;
    private String roleEng;
    private Gender gender;
    private String emailAddress;
    private Boolean isFirstAccess;

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", login=" + login +
            ", fullName=" + fullName +
            ", roleName=" + roleName +
            ", roleAlias=" + roleAlias +
            ", roleEng=" + roleEng +
            ", gender=" + gender +
            ", emailAddress=" + emailAddress +
            ", isFirstAccess=" + isFirstAccess +
            '}';
    }
}
