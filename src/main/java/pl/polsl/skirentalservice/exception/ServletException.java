/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ServletException.java
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
}
