/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AttributeModalReqDto.java
 *  Last modified: 24/01/2023, 13:21
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.attribute;

import lombok.Data;
import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletRequest;

import pl.polsl.skirentalservice.core.IReqValidatePojo;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class AttributeModalReqDto implements IReqValidatePojo {

    @NotEmpty(message = "Pole nie może być puste.")
    @Size(min = 5, max = 50, message = "Pole musi zawierać od 5 do 50 znaków.")
    private String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AttributeModalReqDto(HttpServletRequest req) {
        this.name = trimToEmpty(req.getParameter("name"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name + '\'' +
            '}';
    }
}
