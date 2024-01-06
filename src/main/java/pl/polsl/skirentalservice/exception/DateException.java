/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

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

    public static class BornAfterHiredDateException extends RuntimeException {
        public BornAfterHiredDateException() {
            super("Data zatrudnienia nie może być wcześniejsza niż data urodzenia pracownika.");
        }
    }

    public static class RentDateBeforeIssuedDateException extends RuntimeException {
        public RentDateBeforeIssuedDateException() {
            super("Data wypożyczenia sprzętu nie może być wcześniejsza od daty wystawienia nowego wypożyczenia.");
        }
    }

    public static class ReturnDateBeforeRentDateException extends RuntimeException {
        public ReturnDateBeforeRentDateException() {
            super("Data zwrotu wypożyczenia nie może być wcześniejsza niż data wypożyczenia sprzętu.");
        }
    }
}
