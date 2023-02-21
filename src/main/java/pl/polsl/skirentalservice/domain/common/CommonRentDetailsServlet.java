/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CommonRentDetailsServlet.java
 *  Last modified: 31/01/2023, 08:08
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

import java.util.List;
import java.io.IOException;

import pl.polsl.skirentalservice.dao.rent.*;
import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.lang.String.valueOf;

import static pl.polsl.skirentalservice.util.PageTitle.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RENTS_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/rent-details", "/owner/rent-details" })
public class CommonRentDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRentDetailsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String rentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IRentDao rentDao = new RentDao(session);
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final var rentDetails = rentDao
                    .findRentDetails(rentId, userDataDto.getId(), valueOf(userDataDto.getRoleAlias()))
                    .orElseThrow(() -> { throw new RentNotFoundException(); });

                final List<RentEquipmentsDetailsResDto> allRentEquipments = equipmentDao
                    .findAllEquipmentsConnectedWithRent(rentId);
                final Integer totalSum = allRentEquipments.stream()
                    .map(RentEquipmentsDetailsResDto::count).reduce(0, Integer::sum);

                session.getTransaction().commit();
                req.setAttribute("totalSum", totalSum);
                req.setAttribute("equipmentsRentDetailsData", allRentEquipments);
                req.setAttribute("rentDetailsData", rentDetails);
                req.setAttribute("title", COMMON_RENT_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/rent/" +
                    userDataDto.getRoleEng() + "-rent-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(COMMON_RENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/rents");
        }
    }
}
