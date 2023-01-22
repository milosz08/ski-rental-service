/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: DateException.java
 *  Last modified: 22/01/2023, 09:40
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.time.LocalDate;

public class DateException {

    public static class DateInFutureException extends RuntimeException {
        public DateInFutureException(String formField, int circaYears) {
            super("Wartość daty w polu <strong>" + formField + "</strong> musi być przed <strong>" +
                LocalDate.now().minusYears(circaYears) + "</strong>.");
        }
        public DateInFutureException(String formField) {
            super("Wartość daty w polu <strong>" + formField + "</strong> musi być przed <strong>" +
                LocalDate.now() + "</strong>.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class BornAfterHiredDateException extends RuntimeException {
        public BornAfterHiredDateException() {
            super("Data zatrudnienia nie może być wcześniejsza niż data urodzenia pracownika.");
        }
    }
}
