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

import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;

import java.util.List;
import java.util.ArrayList;

import pl.polsl.skirentalservice.core.JAXBProperty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
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
