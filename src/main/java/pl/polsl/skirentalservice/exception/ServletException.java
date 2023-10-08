/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

import org.slf4j.Logger;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;

public class ServletException {

    public static class UnableToSendEmailException extends RuntimeException {
        public UnableToSendEmailException(String emailAddress, MailRequestPayload payload, Logger logger) {
            super("Nieudane wysłanie wiadomości email. Spróbuj ponownie później.");
            logger.error("Unable to send email message to user email: {}. Message payload: {}", emailAddress, payload);
        }
    }
}
