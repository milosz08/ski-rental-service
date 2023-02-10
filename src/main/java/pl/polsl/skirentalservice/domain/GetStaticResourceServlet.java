/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: GetStaticResourceServlet.java
 *  Last modified: 26/01/2023, 22:54
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import jakarta.ejb.EJB;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import pl.polsl.skirentalservice.core.ConfigBean;

import java.io.*;

import static java.io.File.separator;
import static java.util.Objects.isNull;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/resources/*")
public class GetStaticResourceServlet extends HttpServlet {

    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        if (isNull(httpSession.getAttribute(LOGGED_USER_DETAILS.getName()))) {
            res.sendError(SC_FORBIDDEN);
            return;
        }
        final String resourcePath = substringAfter(req.getRequestURI(), "/resources/");
        final String absPath = config.getUploadsDir() + separator + resourcePath;
        final ServletContext servletContext = req.getServletContext();
        final String mime = servletContext.getMimeType(absPath);
        if (isNull(mime)) {
            res.sendError(SC_INTERNAL_SERVER_ERROR);
            return;
        }
        res.setContentType(mime);
        final File file = new File(absPath);
        if (!file.exists()) {
            res.sendError(SC_NOT_FOUND);
            return;
        }
        res.setContentLength((int) file.length());

        final FileInputStream in = new FileInputStream(file);
        final OutputStream out = res.getOutputStream();
        byte[] buf = new byte[1024];
        int count;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        out.close();
        in.close();
    }
}
