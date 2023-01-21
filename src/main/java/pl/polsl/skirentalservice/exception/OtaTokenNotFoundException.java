/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OtaTokenNotFoundException.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.exception;

import org.slf4j.*;
import jakarta.servlet.http.HttpServletRequest;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class OtaTokenNotFoundException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaTokenNotFoundException.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public OtaTokenNotFoundException(HttpServletRequest req, String token) {
        super("Podany token nie istnieje, został już wykorzystany lub uległ przedawnieniu. Przejdź " +
            "<a class='alert-link' href='" + req.getContextPath() + "/forgot-password-request'>tutaj</a>, " +
            "aby wygenerować nowy token.");
        LOGGER.warn("Attempt to change password with non-existing, expired or already used token. Token: {}", token);
    }
}
