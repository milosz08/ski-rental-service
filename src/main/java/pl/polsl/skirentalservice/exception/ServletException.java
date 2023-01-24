/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ServletException.java
 *  Last modified: 22/01/2023, 10:08
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import org.slf4j.Logger;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ServletException {

    public static class PaginationException extends RuntimeException {
        public PaginationException() {
            super();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class UnableToSendEmailException extends RuntimeException {
        public UnableToSendEmailException(String emailAddress, MailRequestPayload payload, Logger logger) {
            super("Nieudane wysłanie wiadomości email. Spróbuj ponownie później.");
            logger.error("Unable to send email message to user email: {}. Message payload: {}", emailAddress, payload);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class InvalidFieldsExistsException extends RuntimeException {
        public InvalidFieldsExistsException() {
            super();
        }
    }
}
