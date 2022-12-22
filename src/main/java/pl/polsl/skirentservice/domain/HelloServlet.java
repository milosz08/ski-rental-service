/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: HelloServlet.java
 *  Last modified: 22.12.2022, 20:19
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentservice.domain;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/hello-servlet")
public class HelloServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        final PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Hello Web!</h1>");
        out.println("</body></html>");
    }
}