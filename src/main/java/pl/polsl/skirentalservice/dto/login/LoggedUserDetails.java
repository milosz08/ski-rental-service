/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LoggedUserDetails.java
 *  Last modified: 28/12/2022, 03:40
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.login;

import lombok.Data;

import pl.polsl.skirentalservice.dao.UserEntity;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class LoggedUserDetails {
    private String login;
    private String fullName;
    private String roleName;
    private Character roleAlias;

    //------------------------------------------------------------------------------------------------------------------

    public LoggedUserDetails(UserEntity user) {
        this.login = user.getLogin();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.roleName = user.getRole().getRoleName();
        this.roleAlias = user.getRole().getAlias();
    }
}
