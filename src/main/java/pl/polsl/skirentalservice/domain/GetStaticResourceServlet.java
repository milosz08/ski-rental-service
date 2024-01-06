/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/resources/*")
public class GetStaticResourceServlet extends HttpServlet {
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        if (httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName()) == null) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final String resourcePath = StringUtils.substringAfter(req.getRequestURI(), "/resources/");
        final String absPath = config.getUploadsDir() + File.separator + resourcePath;
        final ServletContext servletContext = req.getServletContext();
        final String mime = servletContext.getMimeType(absPath);
        if (mime == null) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        res.setContentType(mime);
        final File file = new File(absPath);
        if (!file.exists()) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
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
