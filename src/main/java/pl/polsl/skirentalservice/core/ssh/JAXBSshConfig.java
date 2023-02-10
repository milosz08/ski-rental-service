/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: JAXBSshConfig.java
 *  Last modified: 21/01/2023, 09:29
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(FIELD)
@XmlRootElement(name = "ssh-configuration")
public class JAXBSshConfig {

    @XmlElement(name = "properties")    private JAXBSshProperties properties;
    @XmlElement(name = "commands")      private JAXBSshCommands commands;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "properties=" + properties +
            ", commands=" + commands +
            '}';
    }
}
