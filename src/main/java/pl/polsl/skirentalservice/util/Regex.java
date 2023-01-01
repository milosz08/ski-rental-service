/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: Regex.java
 *  Last modified: 01/01/2023, 23:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

//----------------------------------------------------------------------------------------------------------------------

public class Regex {
    public static final String LOGIN_EMAIL = "^[a-z0-9@.]{5,80}$";
    public static final String PASSWORD_REQ = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*]).{8,}$";
    public static final String PASSWORD_AVLB = "^[a-zA-Z0-9#?!@$%^&*]{8,}$";
}
