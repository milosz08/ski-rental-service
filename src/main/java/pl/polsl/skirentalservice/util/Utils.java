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

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

//----------------------------------------------------------------------------------------------------------------------

public class Utils {

    private static final String DEF_TITLE = "SkiRent System";

    //------------------------------------------------------------------------------------------------------------------

    public static void generateDefPageTitle(HttpServletRequest req) {
        final String title = (String) req.getAttribute("title");
        req.setAttribute("pageTitle", !Objects.isNull(title) ? title + " | " + DEF_TITLE : DEF_TITLE);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static String getBaseReqPath(HttpServletRequest req) {
        final boolean isHttp = req.getScheme().equals("http") && req.getServerPort() == 80;
        final boolean isHttps = req.getScheme().equals("https") && req.getServerPort() == 443;
        return req.getScheme() + "://" + req.getServerName() + (isHttp || isHttps ? "" : ":" + req.getServerPort());
    }
}
