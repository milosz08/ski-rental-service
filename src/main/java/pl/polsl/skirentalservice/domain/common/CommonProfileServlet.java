/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CommonProfileServlet.java
 *  Last modified: 30/01/2023, 18:36
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;

import java.io.IOException;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.UserRole.SELLER;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.PageTitle.SELLER_PROFILE_PAGE;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/profile", "/owner/profile" })
public class CommonProfileServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonProfileServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_PROFILE_PAGE_ALERT);
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto(" +
                        "e.id, CONCAT(d.firstName, ' ', d.lastName), e.login, d.emailAddress, CAST(d.bornDate AS string)," +
                        "CAST(e.hiredDate AS string), d.pesel, CONCAT('+', d.phoneAreaCode, ' '," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate)," +
                        "YEAR(NOW()) - YEAR(e.hiredDate), d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                        "CAST(IF(e.firstAccess, 'nieaktywowane', 'aktywowane') AS string)," +
                        "CAST(IF(e.firstAccess, 'text-danger', 'text-success') AS string)," +
                        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
                    ") FROM EmployerEntity e INNER JOIN e.userDetails d INNER JOIN e.locationAddress a " +
                    "WHERE e.id = :id";
                final EmployerDetailsResDto employerDetails = session
                    .createQuery(jpqlFindEmployerDetails, EmployerDetailsResDto.class)
                    .setParameter("id", userDataDto.getId())
                    .getSingleResultOrNull();
                if (isNull(employerDetails)) throw new UserNotFoundException(SELLER);

                session.getTransaction().commit();
                req.setAttribute("alertData", alert);
                req.setAttribute("employerData", employerDetails);
                req.setAttribute("title", SELLER_PROFILE_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/" + userDataDto.getRoleEng() +
                    "-profile.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SELLER_DASHBOARD_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/dashboard");
        }
    }
}
