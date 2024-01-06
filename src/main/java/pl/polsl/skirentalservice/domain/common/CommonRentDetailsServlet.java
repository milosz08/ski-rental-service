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
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.RentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;
import java.util.List;

import static pl.polsl.skirentalservice.exception.NotFoundException.RentNotFoundException;

@Slf4j
@WebServlet(urlPatterns = { "/seller/rent-details", "/owner/rent-details" })
public class CommonRentDetailsServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String rentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = new AlertTupleDto(true);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final RentDao rentDao = new RentDaoHib(session);
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final var rentDetails = rentDao
                    .findRentDetails(rentId, userDataDto.getId(), String.valueOf(userDataDto.getRoleAlias()))
                    .orElseThrow(RentNotFoundException::new);

                final List<RentEquipmentsDetailsResDto> allRentEquipments = equipmentDao
                    .findAllEquipmentsConnectedWithRent(rentId);
                final Integer totalSum = allRentEquipments.stream()
                    .map(RentEquipmentsDetailsResDto::count).reduce(0, Integer::sum);

                session.getTransaction().commit();
                req.setAttribute("totalSum", totalSum);
                req.setAttribute("equipmentsRentDetailsData", allRentEquipments);
                req.setAttribute("rentDetailsData", rentDetails);
                req.setAttribute("title", PageTitle.COMMON_RENT_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/rent/" +
                    userDataDto.getRoleEng() + "-rent-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/" + userDataDto.getRoleEng() + "/rents");
        }
    }
}
