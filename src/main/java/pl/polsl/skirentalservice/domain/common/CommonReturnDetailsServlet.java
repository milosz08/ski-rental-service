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
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.ReturnDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.ReturnDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;
import java.util.List;

import static pl.polsl.skirentalservice.exception.NotFoundException.ReturnNotFoundException;

@WebServlet(urlPatterns = { "/seller/return-details", "/owner/return-details" })
public class CommonReturnDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonReturnDetailsServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String returnId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final ReturnDao returnDao = new ReturnDaoHib(session);
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final var returnDetails = returnDao
                    .findReturnDetails(returnId, userDataDto.getId(), String.valueOf(userDataDto.getRoleAlias()))
                    .orElseThrow(ReturnNotFoundException::new);

                final List<RentEquipmentsDetailsResDto> allReturnEquipments = equipmentDao
                    .findAllEquipmentsConnectedWithReturn(returnId);
                final Integer totalSum = allReturnEquipments.stream()
                    .map(RentEquipmentsDetailsResDto::count).reduce(0, Integer::sum);

                session.getTransaction().commit();
                req.setAttribute("totalSum", totalSum);
                req.setAttribute("equipmentsReturnDetailsData", allReturnEquipments);
                req.setAttribute("returnDetailsData", returnDetails);
                req.setAttribute("title", PageTitle.COMMON_RETURN_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/deliv_return/" +
                    userDataDto.getRoleEng() + "-return-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/returns");
        }
    }
}
