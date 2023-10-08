/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.mail;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.JAXBProperty;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mail-configuration")
class JAXBMailConfig {

    @XmlElement(name = "property")
    private List<JAXBProperty> properties = new ArrayList<>();

    List<JAXBProperty> getProperties() {
        return properties;
    }

    void setProperties(List<JAXBProperty> properties) {
        this.properties = properties;
    }
}
