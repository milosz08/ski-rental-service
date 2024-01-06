/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ssh-configuration")
public class JAXBSshConfig {
    @XmlElement(name = "properties")
    private JAXBSshProperties properties;

    @XmlElement(name = "commands")
    private JAXBSshCommands commands;

    JAXBSshProperties getProperties() {
        return properties;
    }

    void setProperties(JAXBSshProperties properties) {
        this.properties = properties;
    }

    JAXBSshCommands getCommands() {
        return commands;
    }

    void setCommands(JAXBSshCommands commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "{" +
            "properties=" + properties +
            ", commands=" + commands +
            '}';
    }
}
