/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: DateException.java
 * Last modified: 2/10/23, 7:55 PM
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class RentDateBeforeIssuedDateException extends RuntimeException {
        public RentDateBeforeIssuedDateException() {
            super("Data wypożyczenia sprzętu nie może być wcześniejsza od daty wystawienia nowego wypożyczenia.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class ReturnDateBeforeRentDateException extends RuntimeException {
        public ReturnDateBeforeRentDateException() {
            super("Data zwrotu wypożyczenia nie może być wcześniejsza niż data wypożyczenia sprzętu.");
        }
    }
}
