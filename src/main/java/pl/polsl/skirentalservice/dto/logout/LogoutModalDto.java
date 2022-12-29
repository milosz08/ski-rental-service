/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LogoutModalDto.java
 *  Last modified: 29/12/2022, 22:26
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.logout;

import lombok.*;

//----------------------------------------------------------------------------------------------------------------------

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutModalDto {
    private boolean visible;
}
