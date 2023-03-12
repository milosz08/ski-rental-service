/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RedirectWhenRentIsNotCreatedByEmployerFilter.java
 *  Last modified: 30/01/2023, 18:12
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.core.db.HibernateUtil;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static pl.polsl.skirentalservice.exception.NotFoundException.RentNotFoundException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/seller/rent-details/*",
    "/seller/delete-rent/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectWhenRentIsNotCreatedByEmployerFilter extends HttpFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectWhenRentIsNotCreatedByEmployerFilter.class);
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String rentId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        final var loggedUserDataDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindRentEmployer =
                    "SELECT COUNT(r.id) > 0 FROM RentEntity r INNER JOIN r.employer e WHERE e.id = :eid";
                final Boolean rentIsCreatedByEmployer = session.createQuery(jpqlFindRentEmployer, Boolean.class)
                    .setParameter("eid", loggedUserDataDto.getId()).getSingleResult();
                if (!rentIsCreatedByEmployer) throw new RentNotFoundException();

                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(PageTitle.COMMON_RENTS_PAGE.getName(), alert);
            res.sendRedirect("/seller/rents");
            return;
        }
        chain.doFilter(req, res);
    }
}
