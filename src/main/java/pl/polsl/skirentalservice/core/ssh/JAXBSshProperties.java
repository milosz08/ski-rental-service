/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JAXBSshProperties.java
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
            "sshHost='" + sshHost +
            ", sshLogin='" + sshLogin +
            ", sshRsa='" + sshRsa +
            ", sshKnownHosts='" + sshKnownHosts +
            '}';
    }
}
