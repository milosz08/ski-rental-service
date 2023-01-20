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
import org.apache.commons.lang3.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

import pl.polsl.skirentalservice.dto.AlertTupleDto;

import static java.util.Objects.isNull;

//----------------------------------------------------------------------------------------------------------------------

public class Utils {

    public static final String DEF_TITLE = "SkiRent System";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void generateDefPageTitle(HttpServletRequest req) {
        final String title = (String) req.getAttribute("title");
        req.setAttribute("pageTitle", !isNull(title) ? title + " | " + DEF_TITLE : DEF_TITLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getBaseReqPath(HttpServletRequest req) {
        final boolean isHttp = req.getScheme().equals("http") && req.getServerPort() == 80;
        final boolean isHttps = req.getScheme().equals("https") && req.getServerPort() == 443;
        return req.getScheme() + "://" + req.getServerName() + (isHttp || isHttps ? "" : ":" + req.getServerPort());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static AlertTupleDto getAndDestroySessionAlert(HttpServletRequest req, SessionAlert sessionAlert) {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = (AlertTupleDto) httpSession.getAttribute(sessionAlert.getName());
        if (isNull(alert)) return new AlertTupleDto();
        httpSession.removeAttribute(sessionAlert.getName());
        return alert;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean getAndDestroySessionBool(HttpServletRequest req, SessionAttribute attribute) {
        final HttpSession httpSession = req.getSession();
        final Object attrFromSession = httpSession.getAttribute(attribute.getName());
        if (isNull(attrFromSession)) return false;
        httpSession.removeAttribute(attribute.getName());
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String loginGenerator(String firstName, String lastName) {
        final String withoutAccents = StringUtils.stripAccents(firstName.substring(0, 3) + lastName.substring(0, 3));
        return withoutAccents + RandomStringUtils.randomNumeric(3);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String generateHash(String preCoded) {
        return BCrypt.withDefaults().hashToString(10, preCoded.toCharArray());
    }
}
