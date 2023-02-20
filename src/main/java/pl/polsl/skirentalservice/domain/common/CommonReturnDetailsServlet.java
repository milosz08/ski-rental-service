/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CommonReturnDetailsServlet.java
 *  Last modified: 31/01/2023, 10:22
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

import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dao.return_deliv.*;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;

import java.util.List;
import java.io.IOException;

import static java.lang.String.valueOf;

import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_RETURN_DETAILS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RETURNS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/return-details", "/owner/return-details" })
public class CommonReturnDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonReturnDetailsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String returnId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IReturnDao returnDao = new ReturnDao(session);
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final var returnDetails = returnDao
                    .findReturnDetails(returnId, userDataDto.getId(), valueOf(userDataDto.getRoleAlias()))
                    .orElseThrow(() -> { throw new ReturnNotFoundException(); });

                final List<RentEquipmentsDetailsResDto> allReturnEquipments = equipmentDao
                    .findAllEquipmentsConnectedWithReturn(returnId);
                final Integer totalSum = allReturnEquipments.stream()
                    .map(RentEquipmentsDetailsResDto::getCount).reduce(0, Integer::sum);

                session.getTransaction().commit();
                req.setAttribute("totalSum", totalSum);
                req.setAttribute("equipmentsReturnDetailsData", allReturnEquipments);
                req.setAttribute("returnDetailsData", returnDetails);
                req.setAttribute("title", COMMON_RETURN_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/deliv_return/" +
                    userDataDto.getRoleEng() + "-return-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(COMMON_RETURNS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/returns");
        }
    }
}
