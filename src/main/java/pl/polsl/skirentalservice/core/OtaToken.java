/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OtaGenerator.java
 *  Last modified: 01/01/2023, 04:19
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import java.util.Random;

//----------------------------------------------------------------------------------------------------------------------

public class OtaToken {

    private static final Random RANDOM = new Random();
    private static final String CHARACTERS = "abcdefghijklmnoprstquvwxyzABCDEFGHIJKLMNOPRSTQUWXYZ0123456789";

    //------------------------------------------------------------------------------------------------------------------

    public static String generate() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            builder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return builder.toString();
    }
}
