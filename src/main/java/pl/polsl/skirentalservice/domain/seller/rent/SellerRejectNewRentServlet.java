/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerRejectNewRentServlet.java
 *  Last modified: 28/01/2023, 14:27
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;

import java.io.IOException;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.AlertType.*;
import static pl.polsl.skirentalservice.util.Utils.getLoggedUserLogin;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.SessionAttribute.INMEMORY_NEW_RENT_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/reject-new-rent")
public class SellerRejectNewRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerRejectNewRentServlet.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);

        final HttpSession httpSession = req.getSession();
        final var inMemoryRentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());

        if (isNull(inMemoryRentData)) {
            alert.setType(WARN);
            alert.setMessage(
                "Usuwane robocze wypożyczenie zapisane w pamięci systemu nie zawierało żadnych zapisanych danych."
            );
        } else {
            LOGGER.info("Successful removed temporary saved rent data by: {}. In memory data: {}", loggedUser,
                inMemoryRentData);
            httpSession.removeAttribute(INMEMORY_NEW_RENT_DATA.getName());
            alert.setType(INFO);
            alert.setMessage("Pomyślnie usunięto z pamięci systemu nowe robocze wypożyczenie.");
        }
        httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/customers");
    }
}
