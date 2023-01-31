/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CommonReturnDetailsServlet.java
 *  Last modified: 30/01/2023, 21:58
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

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;

import java.util.List;
import java.io.IOException;

import static java.util.Objects.isNull;

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

                final String jpqlFindReturnDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto(" +
                        "rr.id, rr.issuedIdentifier, r.issuedIdentifier, rr.issuedDateTime, rr.description, r.tax," +
                        "rr.totalPrice, CAST((r.tax / 100) * rr.totalPrice + rr.totalPrice AS bigdecimal), rr.totalDepositPrice," +
                        "CAST((r.tax / 100) * rr.totalDepositPrice + rr.totalDepositPrice AS bigdecimal)," +
                        "CONCAT(d.firstName, ' ', d.lastName), d.pesel, d.bornDate, CONCAT('+', d.phoneAreaCode," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate), d.emailAddress," +
                        "d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
                    ") FROM RentReturnEntity rr " +
                    "LEFT OUTER JOIN rr.rent r " +
                    "LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c LEFT OUTER JOIN c.userDetails d " +
                    "LEFT OUTER JOIN c.locationAddress a WHERE rr.id = :rid";
                final ReturnRentDetailsResDto returnDetails = session
                    .createQuery(jpqlFindReturnDetails, ReturnRentDetailsResDto.class)
                    .setParameter("rid", returnId)
                    .getSingleResultOrNull();
                if (isNull(returnDetails)) throw new ReturnNotFoundException();

                final String jpqlFindAllEquipments =
                    "SELECT new pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto(" +
                        "re.id, IFNULL(e.name, '<i>sprzęt usunięty</i>'), rer.count, e.barcode, re.description," +
                        "re.totalPrice, CAST((r.tax / 100) * re.totalPrice + re.totalPrice AS bigdecimal)," +
                        "re.depositPrice, CAST((r.tax / 100) * re.depositPrice + re.depositPrice AS bigdecimal)" +
                    ") FROM RentReturnEquipmentEntity re " +
                    "INNER JOIN re.rentEquipment rer " +
                    "INNER JOIN rer.rent r INNER JOIN re.rentReturn rrer LEFT OUTER JOIN re.equipment e " +
                    "WHERE rrer.id = :rid";
                final List<RentEquipmentsDetailsResDto> allReturnEquipments = session
                    .createQuery(jpqlFindAllEquipments, RentEquipmentsDetailsResDto.class)
                    .setParameter("rid", returnId)
                    .getResultList();

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
