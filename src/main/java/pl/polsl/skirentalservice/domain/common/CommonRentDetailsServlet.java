/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CommonRentDetailsServlet.java
 *  Last modified: 30/01/2023, 18:17
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

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Objects.isNull;

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

                final String jpqlFindRentDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.rent.RentDetailsResDto(" +
                        "r.id, r.issuedIdentifier, r.issuedDateTime, r.rentDateTime, r.returnDateTime, " +
                        "IFNULL(r.description, '<i>Brak danych</i>'), " +
                        "r.tax, r.status, r.totalPrice, CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                        "r.totalDepositPrice, CAST((r.tax / 100) * r.totalDepositPrice + r.totalDepositPrice AS bigdecimal)," +
                        "CONCAT(d.firstName, ' ', d.lastName), d.pesel, d.bornDate, CONCAT('+', d.phoneAreaCode," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate), d.emailAddress," +
                        "d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
                    ") FROM RentEntity r " +
                    "LEFT OUTER JOIN r.employer e " +
                    "LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN c.locationAddress a " +
                    "WHERE r.id = :rid";
                final RentDetailsResDto equipmentDetails = session
                    .createQuery(jpqlFindRentDetails, RentDetailsResDto.class)
                    .setParameter("rid", rentId)
                    .getSingleResultOrNull();
                if (isNull(equipmentDetails)) throw new RentNotFoundException();

                final String jpqlFindAllEquipments =
                    "SELECT new pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto(" +
                        "re.id, IFNULL(e.name, '<i>sprzęt usunięty</i>'), re.count, e.barcode, re.description," +
                        "re.totalPrice, CAST((r.tax / 100) * re.totalPrice + re.totalPrice AS bigdecimal)," +
                        "re.depositPrice, CAST((r.tax / 100) * re.depositPrice + re.depositPrice AS bigdecimal)" +
                    ") FROM RentEquipmentEntity re " +
                    "INNER JOIN re.rent r LEFT OUTER JOIN re.equipment e " +
                    "WHERE r.id = :rid ORDER BY re.id";
                final List<RentEquipmentsDetailsResDto> allRentEquipments = session
                    .createQuery(jpqlFindAllEquipments, RentEquipmentsDetailsResDto.class)
                    .setParameter("rid", rentId)
                    .getResultList();

                final Integer totalSum = allRentEquipments.stream()
                    .map(RentEquipmentsDetailsResDto::getCount).reduce(0, Integer::sum);

                session.getTransaction().commit();
                req.setAttribute("totalSum", totalSum);
                req.setAttribute("equipmentsRentDetailsData", allRentEquipments);
                req.setAttribute("rentDetailsData", equipmentDetails);
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
