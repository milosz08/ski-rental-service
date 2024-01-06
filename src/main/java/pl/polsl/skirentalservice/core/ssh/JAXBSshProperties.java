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
@XmlRootElement(name = "properties")
public class JAXBSshProperties {
    @XmlElement(name = "ssh-host")
    private String sshHost;

    @XmlElement(name = "ssh-login")
    private String sshLogin;

    @XmlElement(name = "ssh-rsa")
    private String sshRsa;

    @XmlElement(name = "ssh-knownHosts")
    private String sshKnownHosts;

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

    @Override
    public String toString() {
        return "{" +
            "sshHost=" + sshHost +
            ", sshLogin=" + sshLogin +
            ", sshRsa=" + sshRsa +
            ", sshKnownHosts=" + sshKnownHosts +
            '}';
    }
}
