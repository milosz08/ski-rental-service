/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import pl.polsl.skirentalservice.core.JAXBProperty;

import java.util.List;

class JakartaMailAuthenticator extends Authenticator {
    private final String username;
    private final String password;

    JakartaMailAuthenticator(List<JAXBProperty> mappedProperties) {
        this.username = findProperty(mappedProperties, "mail.smtp.user");
        this.password = findProperty(mappedProperties, "mail.smtp.pass");
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

    static String findProperty(List<JAXBProperty> mappedProperties, String propertyName) {
        return mappedProperties.stream().filter(p -> p.getName().equals(propertyName))
            .findFirst()
            .orElse(new JAXBProperty(propertyName)).getValue();
    }
}
