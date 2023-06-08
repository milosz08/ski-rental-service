/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JakartaMailAuthenticator.java
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

package pl.polsl.skirentalservice.core.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import java.util.List;

import pl.polsl.skirentalservice.core.JAXBProperty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class JakartaMailAuthenticator extends Authenticator {

    private final String username;
    private final String password;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    JakartaMailAuthenticator(List<JAXBProperty> mappedProperties) {
        this.username = findProperty(mappedProperties, "mail.smtp.user");
        this.password = findProperty(mappedProperties, "mail.smtp.pass");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static String findProperty(List<JAXBProperty> mappedProperties, String propertyName) {
        return mappedProperties.stream().filter(p -> p.getName().equals(propertyName))
            .findFirst()
            .orElse(new JAXBProperty(propertyName)).getValue();
    }
}
