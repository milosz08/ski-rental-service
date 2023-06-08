/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: GetStaticResourceServlet.java
 * Last modified: 6/3/23, 12:11 AM
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

package pl.polsl.skirentalservice.domain;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.util.Objects;

import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.util.SessionAttribute;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/resources/*")
public class GetStaticResourceServlet extends HttpServlet {

    private final ConfigSingleton config = ConfigSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        if (Objects.isNull(httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName()))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        final String resourcePath = StringUtils.substringAfter(req.getRequestURI(), "/resources/");
        final String absPath = config.getUploadsDir() + File.separator + resourcePath;
        final ServletContext servletContext = req.getServletContext();
        final String mime = servletContext.getMimeType(absPath);
        if (Objects.isNull(mime)) {
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
