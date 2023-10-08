/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "command")
public class JAXBSshCommand {

    @XmlAttribute(name = "executableFor")
    private String executableFor;

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String execScript;

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

    @Override
    public String toString() {
        return "{" +
            "executableFor='" + executableFor +
            ", execScript='" + execScript +
            '}';
    }
}
