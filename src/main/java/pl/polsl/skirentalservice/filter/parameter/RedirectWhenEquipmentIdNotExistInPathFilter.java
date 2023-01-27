/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RedirectWhenEmployerIdNotExistInPathFilter.java
 *  Last modified: 22/01/2023, 10:46
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.parameter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNumeric;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/owner/edit-equipment/*",
    "/owner/delete-equipment/*",
    "/owner/equipment-details/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectWhenEquipmentIdNotExistInPathFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String equipmentId = req.getParameter("id");
        if (!isNumeric(equipmentId)) {
            res.sendRedirect("/owner/equipments");
            return;
        }
        chain.doFilter(req, res);
    }
}
