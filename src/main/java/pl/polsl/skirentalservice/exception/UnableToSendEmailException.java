/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UnableToSendEmailException.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import org.slf4j.*;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class UnableToSendEmailException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnableToSendEmailException.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UnableToSendEmailException(String emailAddress, MailRequestPayload payload) {
        super("Nieudane wysłanie wiadomości email. Spróbuj ponownie później.");
        LOGGER.error("Unable to send email message to user email: {}. Message payload: {}", emailAddress, payload);
    }
}
