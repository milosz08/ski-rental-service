/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.filter.parameter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.hibernate.RentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.RentNotFoundException;

@Slf4j
@WebFilter(urlPatterns = {
    "/seller/rent-details/*",
    "/seller/delete-rent/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectWhenRentIsNotCreatedByEmployerFilter extends HttpFilter {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

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

                final RentDao rentDao = new RentDaoHib(session);
                if (!rentDao.checkIfRentIsFromEmployer(rentId, loggedUserDataDto.getId())) {
                    throw new RentNotFoundException();
                }
                session.getTransaction().commit();
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
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
