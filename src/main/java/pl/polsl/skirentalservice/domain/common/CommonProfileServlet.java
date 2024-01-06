/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@WebServlet(urlPatterns = { "/seller/profile", "/owner/profile" })
public class CommonProfileServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonProfileServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.COMMON_PROFILE_PAGE_ALERT);
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EmployerDao employerDao = new EmployerDaoHib(session);

                final var employerDetails = employerDao.findEmployerPageDetails(userDataDto.getId())
                    .orElseThrow(() -> new UserNotFoundException(String.valueOf(userDataDto.getId())));

                session.getTransaction().commit();
                req.setAttribute("alertData", alert);
                req.setAttribute("employerData", employerDetails);
                req.setAttribute("title", PageTitle.SELLER_PROFILE_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/" + userDataDto.getRoleEng() +
                    "-profile.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.SELLER_DASHBOARD_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/dashboard");
        }
    }
}
