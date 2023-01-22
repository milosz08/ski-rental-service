/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: Utils.java
 *  Last modified: 29/12/2022, 01:38
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import jakarta.servlet.http.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

import pl.polsl.skirentalservice.dto.AlertTupleDto;

import static java.util.Objects.isNull;

//----------------------------------------------------------------------------------------------------------------------

public class Utils {

    public static AlertTupleDto getAndDestroySessionAlert(HttpServletRequest req, SessionAlert sessionAlert) {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = (AlertTupleDto) httpSession.getAttribute(sessionAlert.getName());
        if (isNull(alert)) return new AlertTupleDto();
        httpSession.removeAttribute(sessionAlert.getName());
        return alert;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String generateHash(String preCoded) {
        return BCrypt.withDefaults().hashToString(10, preCoded.toCharArray());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean invalidPassword(String hash, String decoded) {
        return !BCrypt.verifyer().verify(hash.toCharArray(), decoded).verified;
    }
}
