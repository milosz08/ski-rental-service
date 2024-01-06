/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@Slf4j
@WebServlet("/owner/employer-details")
public class OwnerEmployerDetailsServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EmployerDao employerDao = new EmployerDaoHib(session);

                final var employerDetails = employerDao.findEmployerPageDetails(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

                session.getTransaction().commit();
                req.setAttribute("employerData", employerDetails);
                req.setAttribute("title", PageTitle.OWNER_EMPLOYER_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employer-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/owner/employers");
        }
    }
}
