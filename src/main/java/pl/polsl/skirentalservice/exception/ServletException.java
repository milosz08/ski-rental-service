/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;

public class ServletException {
    @Slf4j
    public static class UnableToSendEmailException extends AbstractAppException {
        public UnableToSendEmailException(String emailAddress, MailRequestPayload payload) {
            super("Nieudane wysłanie wiadomości email. Spróbuj ponownie później.");
            log.error("Unable to send email message to user email: {}. Message payload: {}", emailAddress, payload);
        }
    }

    @Slf4j
    public static class PdfGeneratorException extends AbstractAppException {
        public PdfGeneratorException(Throwable ex) {
            super("Nieudana generacja dokumentu PDF. Spróbuj ponownie.");
            log.error("Unable to generated PDF document. Cause: {}", ex.getMessage());
        }
    }
}
