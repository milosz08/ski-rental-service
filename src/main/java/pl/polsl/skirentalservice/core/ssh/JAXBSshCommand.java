/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: JAXBSshCommand.java
 *  Last modified: 21/01/2023, 07:02
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.*;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@NoArgsConstructor
@XmlAccessorType(FIELD)
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
            "executableFor='" + executableFor + '\'' +
            ", execScript='" + execScript + '\'' +
            '}';
    }
}
