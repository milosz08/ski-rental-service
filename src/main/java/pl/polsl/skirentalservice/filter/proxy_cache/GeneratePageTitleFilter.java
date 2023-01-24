/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: GeneratePageTitleFilter.java
 *  Last modified: 22/01/2023, 12:02
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.proxy_cache;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.*;
import jakarta.servlet.ServletException;

import java.io.IOException;

import pl.polsl.skirentalservice.core.ConfigBean;

import static java.util.Objects.isNull;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = { "/*" }, initParams = @WebInitParam(name = "mood", value = "awake"))
public class GeneratePageTitleFilter extends HttpFilter {

    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String title = (String) req.getAttribute("title");
        final String pageTitle = !isNull(title) ? title + " | " + config.getDefPageTitle() : config.getDefPageTitle();
        req.setAttribute("pageTitle", pageTitle);
        chain.doFilter(req, res);
    }
}
