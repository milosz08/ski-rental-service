/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;
import java.util.Objects;

@WebServlet("/seller/reject-new-rent")
public class SellerRejectNewRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerRejectNewRentServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final HttpSession httpSession = req.getSession();
        final var inMemoryRentData = (InMemoryRentDataDto) httpSession
            .getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());

        if (Objects.isNull(inMemoryRentData)) {
            alert.setType(AlertType.WARN);
            alert.setMessage(
                "Usuwane robocze wypożyczenie zapisane w pamięci systemu nie zawierało żadnych zapisanych danych.");
        } else {
            LOGGER.info("Successful removed temporary saved rent data by: {}. In memory data: {}", loggedUser,
                inMemoryRentData);
            httpSession.removeAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
            alert.setType(AlertType.INFO);
            alert.setMessage("Pomyślnie usunięto z pamięci systemu nowe robocze wypożyczenie.");
        }
        httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/customers");
    }
}
