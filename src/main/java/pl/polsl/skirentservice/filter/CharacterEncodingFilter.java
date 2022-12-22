/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CharacterEncodingFilter.java
 *  Last modified: 22.12.2022, 17:46
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

//----------------------------------------------------------------------------------------------------------------------

@WebFilter(urlPatterns = "/*", initParams = @WebInitParam(name = "mood", value = "awake"))
public class CharacterEncodingFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void destroy() {
    }
}
