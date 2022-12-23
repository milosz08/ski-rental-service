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
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.ServletException;

import org.hibernate.Session;

import pl.polsl.skirentservice.dto.UserDetailsDto;
import pl.polsl.skirentservice.core.HibernateFactory;

import static pl.polsl.skirentservice.util.PageTitle.HELLO_PAGE;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/hello-servlet")
public class HelloServlet extends HttpServlet {

    @EJB private HibernateFactory factory;

    //------------------------------------------------------------------------------------------------------------------

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        final Session session = factory.open();

        final String query = "SELECT new pl.polsl.skirentservice.dto.UserDetailsDto(u) FROM UserEntity u";
        final List<UserDetailsDto> allUsers = session.createQuery(query, UserDetailsDto.class).list();

        session.close();
        req.setAttribute("users", allUsers);
        req.setAttribute("title", HELLO_PAGE.getName());
        req.getRequestDispatcher("WEB-INF/pages/owner-dashboard.jsp").forward(req, res);
    }
}
