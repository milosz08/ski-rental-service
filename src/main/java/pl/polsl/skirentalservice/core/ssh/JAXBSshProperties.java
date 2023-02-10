/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: JAXBSshProperties.java
 *  Last modified: 21/01/2023, 08:57
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
@XmlRootElement(name = "properties")
public class JAXBSshProperties {

    @XmlElement(name = "ssh-host")              private String sshHost;
    @XmlElement(name = "ssh-login")             private String sshLogin;
    @XmlElement(name = "ssh-rsa")               private String sshRsa;
    @XmlElement(name = "ssh-knownHosts")        private String sshKnownHosts;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getSshHost() {
        return sshHost;
    }

    void setSshHost(String sshHost) {
        this.sshHost = sshHost;
    }

    String getSshLogin() {
        return sshLogin;
    }

    void setSshLogin(String sshLogin) {
        this.sshLogin = sshLogin;
    }

    String getSshRsa() {
        return sshRsa;
    }

    void setSshRsa(String sshRsa) {
        this.sshRsa = sshRsa;
    }

    String getSshKnownHosts() {
        return sshKnownHosts;
    }

    void setSshKnownHosts(String sshKnownHosts) {
        this.sshKnownHosts = sshKnownHosts;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "sshHost='" + sshHost + '\'' +
            ", sshLogin='" + sshLogin + '\'' +
            ", sshRsa='" + sshRsa + '\'' +
            ", sshKnownHosts='" + sshKnownHosts + '\'' +
            '}';
    }
}
