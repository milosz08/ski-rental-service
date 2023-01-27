/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerDeleteEquipmentBrandServlet.java
 *  Last modified: 25/01/2023, 08:40
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.attribute;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_BRANDS_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/delete-equipment-brand")
public class OwnerDeleteEquipmentBrandServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentBrandServlet.class);

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String brandId = req.getParameter("id");
        final String loggedUser = getLoggedUserLogin(req);

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jqplFindNameOfDeletingBrand= "SELECT b.name FROM EquipmentBrandEntity b WHERE b.id = :id";
                final String getDeletedName = session.createQuery(jqplFindNameOfDeletingBrand, String.class)
                    .setParameter("id", brandId).getSingleResultOrNull();
                if (isNull(getDeletedName)) throw new EquipmentBrandNotFoundException();

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);

                final String jpqlFindBrandHasConnections =
                    "SELECT COUNT(e.id) > 0 FROM EquipmentEntity e INNER JOIN e.equipmentBrand b WHERE b.id = :id";
                final Boolean attributeHasConnections = session.createQuery(jpqlFindBrandHasConnections, Boolean.class)
                    .setParameter("id", brandId)
                    .getSingleResult();
                if (attributeHasConnections) throw new EquipmentBrandHasConnectionsException();

                session.createMutationQuery("DELETE EquipmentBrandEntity b WHERE b.id = :id")
                    .setParameter("id", brandId).executeUpdate();
                alert.setType(INFO);
                alert.setMessage(
                    "Usuwanie marki sprzętu narciarskiego: <strong>" + getDeletedName + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful deleted equipment brand by: {}. Brand: {}", loggedUser, getDeletedName);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            LOGGER.error("Failure delete equipment brand by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(EQ_BRANDS_MODAL_DATA.getName(), resDto);
        res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
