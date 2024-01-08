/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.polsl.skirentalservice.core.AbstractXMLProperties;

@Setter
@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ssh-configuration")
public class XMLSshConfig extends AbstractXMLProperties {
    @XmlElement(name = "commands")
    private XMLSshCommands commands;

    @Override
    public String toString() {
        return "{" +
            "properties=" + properties +
            ", commands=" + commands +
            '}';
    }
}
