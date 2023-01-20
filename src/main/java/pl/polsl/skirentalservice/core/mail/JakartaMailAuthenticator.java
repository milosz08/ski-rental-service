/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: JakartaMailAuthenticator.java
 *  Last modified: 01/01/2023, 18:06
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.mail;

import jakarta.mail.*;
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
