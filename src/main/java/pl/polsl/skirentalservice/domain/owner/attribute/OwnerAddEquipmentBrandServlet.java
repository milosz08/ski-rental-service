/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerAddEquipmentBrandServlet.java
 *  Last modified: 30/01/2023, 18:15
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.attribute;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dao.equipment_brand.*;
import pl.polsl.skirentalservice.entity.EquipmentBrandEntity;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;

import static org.apache.commons.lang3.StringUtils.*;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_BRANDS_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-equipment-brand")
public class OwnerAddEquipmentBrandServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEquipmentBrandServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AttributeValidatorPayloadDto payload = validateEquipmentAttribute(req, validator);
        final String loggedUser = getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();
        if (payload.isInvalid()) {
            httpSession.setAttribute(EQ_BRANDS_MODAL_DATA.getName(), payload.getResDto());
            res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final IEquipmentBrandDao equipmentDetailsDao = new EquipmentBrandDao(session);

                if (!equipmentDetailsDao.checkIfEquipmentBrandExistByName(payload.getReqDto().getName())) {
                    throw new EquipmentBrandAlreadyExistException();
                }
                final EquipmentBrandEntity brandEntity = new EquipmentBrandEntity(payload.getReqDto().getName());
                session.persist(brandEntity);

                payload.getAlert().setType(INFO);
                payload.getAlert().setMessage(
                    "Nastąpiło pomyślne dodanie nowej marki sprzętu narciarskiego: <strong>" +
                    payload.getReqDto().getName() + "</strong>."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful added new equipment brand by: {}. Brand: {}", loggedUser,
                    payload.getReqDto().getName());
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            payload.getAlert().setMessage(ex.getMessage());
            LOGGER.error("Failure add new equipment brand by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        payload.getAlert().setActive(true);
        payload.getResDto().setAlert(payload.getAlert());
        payload.getResDto().setModalImmediatelyOpen(true);
        payload.getResDto().getName().setValue(EMPTY);
        httpSession.setAttribute(EQ_BRANDS_MODAL_DATA.getName(), payload.getResDto());
        res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
