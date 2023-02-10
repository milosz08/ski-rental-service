/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: JAXBMailConfig.java
 *  Last modified: 20/01/2023, 05:08
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.mail;

import lombok.*;
import java.util.*;
import jakarta.xml.bind.annotation.*;

import pl.polsl.skirentalservice.core.JAXBProperty;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(FIELD)
@XmlRootElement(name = "mail-configuration")
class JAXBMailConfig {

    @XmlElement(name = "property") private List<JAXBProperty> properties = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    List<JAXBProperty> getProperties() {
        return properties;
    }

    void setProperties(List<JAXBProperty> properties) {
        this.properties = properties;
    }
}
