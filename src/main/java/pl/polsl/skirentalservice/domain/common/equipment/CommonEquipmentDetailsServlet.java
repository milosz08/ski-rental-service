/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.common.equipment;

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
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;

@WebServlet(urlPatterns = { "/owner/equipment-details", "/seller/equipment-details" })
public class CommonEquipmentDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonEquipmentDetailsServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String equipmentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final var equipmentDetails = equipmentDao.findEquipmentDetailsPage(equipmentId)
                    .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));

                req.setAttribute("equipmentData", equipmentDetails);
                req.setAttribute("title", PageTitle.COMMON_EQUIPMENT_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/equipment/" +
                    userDataDto.getRoleEng() + "-equipment-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/equipments");
        }
    }
}
