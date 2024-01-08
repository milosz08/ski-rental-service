/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Getter
public abstract class AbstractXMLProperties {
    @XmlElement(name = "property")
    protected List<XMLProperty> properties = new ArrayList<>();

    public Properties mapToProperties() {
        final Properties javaProperties = new Properties();
        for (final XMLProperty property : properties) {
            javaProperties.put(property.getName(), property.getValue());
        }
        return javaProperties;
    }
}
