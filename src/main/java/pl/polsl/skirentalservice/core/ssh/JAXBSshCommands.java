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

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "commands")
public class JAXBSshCommands {

    @XmlElement(name = "command")
    private List<JAXBSshCommand> commands = new ArrayList<>();

    List<JAXBSshCommand> getCommands() {
        return commands;
    }

    void setCommands(List<JAXBSshCommand> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "{" +
            "commands=" + commands +
            '}';
    }
}
