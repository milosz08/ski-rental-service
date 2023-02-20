/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CommonEquipmentDetailsServlet.java
 *  Last modified: 06/02/2023, 19:26
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common.equipment;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_EQUIPMENT_DETAILS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/owner/equipment-details", "/seller/equipment-details" })
public class CommonEquipmentDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonEquipmentDetailsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final var equipmentDetails = equipmentDao.findEquipmentDetailsPage(equipmentId).orElseThrow(() -> {
                    throw new EquipmentNotFoundException(equipmentId);
                });

                req.setAttribute("equipmentData", equipmentDetails);
                req.setAttribute("title", COMMON_EQUIPMENT_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/equipment/" +
                    userDataDto.getRoleEng() + "-equipment-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/equipments");
        }
    }
}
