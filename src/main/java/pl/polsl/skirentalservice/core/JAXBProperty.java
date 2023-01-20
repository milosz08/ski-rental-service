/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: JAXBProperty.java
 *  Last modified: 01/01/2023, 22:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(FIELD)
@XmlRootElement(name = "property")
public class JAXBProperty {

    @XmlAttribute(name = "name")    private String name;
    @XmlValue                       private String value;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public JAXBProperty(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}
