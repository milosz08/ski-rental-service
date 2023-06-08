/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JAXBSshCommand.java
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

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "command")
public class JAXBSshCommand {

    @XmlAttribute(name = "executableFor")                           private String executableFor;
    @XmlValue @XmlJavaTypeAdapter(NormalizedStringAdapter.class)    private String execScript;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getExecutableFor() {
        return executableFor;
    }

    void setExecutableFor(String executableFor) {
        this.executableFor = executableFor;
    }

    String getExecScript() {
        return execScript;
    }

    void setExecScript(String execScript) {
        this.execScript = execScript;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "executableFor='" + executableFor +
            ", execScript='" + execScript +
            '}';
    }
}
