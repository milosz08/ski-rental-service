/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JAXBSshConfig.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.core.ssh;

import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
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
