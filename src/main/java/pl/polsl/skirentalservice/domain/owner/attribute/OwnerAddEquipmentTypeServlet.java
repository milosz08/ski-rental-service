/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerAddEquipmentTypeServlet.java
 *  Last modified: 24/01/2023, 13:01
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

import pl.polsl.skirentalservice.dto.attribute.*;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.entity.EquipmentTypeEntity;

import static org.apache.commons.lang3.StringUtils.*;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.EQ_TYPES_MODAL_DATA;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-equipment-type")
public class OwnerAddEquipmentTypeServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerDeleteEquipmentTypeServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AttributeValidatorPayloadDto payload = validateEquipmentAttribute(req, validator);
        final String loggedUser = getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();
        if (payload.isInvalid()) {
            httpSession.setAttribute(EQ_TYPES_MODAL_DATA.getName(), payload.getResDto());
            res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindTypeAlreadyExist =
                    "SELECT COUNT(t.id) > 0 FROM EquipmentTypeEntity t WHERE LOWER(t.name) = LOWER(:name)";
                final Boolean typeAlreadyExist = session.createQuery(jpqlFindTypeAlreadyExist, Boolean.class)
                    .setParameter("name", payload.getReqDto().getName())
                    .getSingleResult();
                if (typeAlreadyExist) throw new EquipmentTypeAlreadyExistException();

                final EquipmentTypeEntity typeEntity = new EquipmentTypeEntity(payload.getReqDto().getName());
                session.persist(typeEntity);

                payload.getAlert().setType(INFO);
                payload.getAlert().setMessage(
                    "Nastąpiło pomyślne dodanie nowego typu sprzętu narciarskiego: <strong>" +
                    payload.getReqDto().getName() + "</strong>."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful added new equipment type by: {}. Type: {}", loggedUser,
                    payload.getReqDto().getName());
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            payload.getAlert().setMessage(ex.getMessage());
            LOGGER.error("Failure add new equipment type by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        payload.getAlert().setActive(true);
        payload.getResDto().setAlert(payload.getAlert());
        payload.getResDto().setModalImmediatelyOpen(true);
        payload.getResDto().getName().setValue(EMPTY);
        httpSession.setAttribute(EQ_TYPES_MODAL_DATA.getName(), payload.getResDto());
        res.sendRedirect(defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
