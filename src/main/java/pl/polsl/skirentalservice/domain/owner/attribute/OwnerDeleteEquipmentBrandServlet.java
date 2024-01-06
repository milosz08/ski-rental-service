/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.attribute;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentBrandDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentBrandDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentBrandHasConnectionsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentBrandNotFoundException;

@WebServlet("/owner/delete-equipment-brand")
public class OwnerDeleteEquipmentBrandServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentBrandServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String brandId = req.getParameter("id");
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentBrandDao equipmentDetailsDao = new EquipmentBrandDaoHib(session);

                final String deletedBrand = equipmentDetailsDao.getEquipmentBrandNameById(brandId)
                    .orElseThrow(EquipmentBrandNotFoundException::new);

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);

                if (equipmentDetailsDao.checkIfEquipmentBrandHasAnyConnections(brandId)) {
                    throw new EquipmentBrandHasConnectionsException();
                }
                equipmentDetailsDao.deleteEquipmentBrandById(brandId);

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Usuwanie marki sprzętu narciarskiego: <strong>" + deletedBrand + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful deleted equipment brand by: {}. Brand: {}", loggedUser, deletedBrand);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            LOGGER.error("Failure delete equipment brand by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA.getName(), resDto);
        res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
