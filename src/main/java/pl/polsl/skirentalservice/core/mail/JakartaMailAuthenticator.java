package pl.polsl.skirentalservice.core.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import java.util.Properties;

class JakartaMailAuthenticator extends Authenticator {
    private final String username;
    private final String password;

    JakartaMailAuthenticator(Properties properties) {
        this.username = properties.getProperty("mail.smtp.user");
        this.password = properties.getProperty("mail.smtp.pass");
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
