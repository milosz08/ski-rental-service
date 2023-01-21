/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: PhoneNumberAlreadyExistException.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class PhoneNumberAlreadyExistException extends RuntimeException {

    public PhoneNumberAlreadyExistException(String phoneNumber) {
        super("Użytkownik z numerem telefonu <strong>" + phoneNumber + "</strong> istnieje już w systemie.");
    }
}
