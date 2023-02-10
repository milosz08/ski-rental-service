/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: JAXBSshCommands.java
 *  Last modified: 21/01/2023, 09:28
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

import java.util.*;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(FIELD)
@XmlRootElement(name = "commands")
public class JAXBSshCommands {

    @XmlElement(name = "command") private List<JAXBSshCommand> commands = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    List<JAXBSshCommand> getCommands() {
        return commands;
    }

    void setCommands(List<JAXBSshCommand> commands) {
        this.commands = commands;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "commands=" + commands +
            '}';
    }
}
