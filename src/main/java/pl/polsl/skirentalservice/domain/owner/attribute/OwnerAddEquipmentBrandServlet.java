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
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentBrandDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentBrandDaoHib;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.entity.EquipmentBrandEntity;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentBrandAlreadyExistException;

@WebServlet("/owner/add-equipment-brand")
public class OwnerAddEquipmentBrandServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEquipmentBrandServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AttributeValidatorPayloadDto payload = Utils.validateEquipmentAttribute(req, validator);
        final String loggedUser = Utils.getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();
        if (payload.isInvalid()) {
            httpSession.setAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA.getName(), payload.resDto());
            res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentBrandDao equipmentDetailsDao = new EquipmentBrandDaoHib(session);

                if (!equipmentDetailsDao.checkIfEquipmentBrandExistByName(payload.reqDto().getName())) {
                    throw new EquipmentBrandAlreadyExistException();
                }
                final EquipmentBrandEntity brandEntity = new EquipmentBrandEntity(payload.reqDto().getName());
                session.persist(brandEntity);

                payload.alert().setType(AlertType.INFO);
                payload.alert().setMessage(
                    "Nastąpiło pomyślne dodanie nowej marki sprzętu narciarskiego: <strong>" +
                        payload.reqDto().getName() + "</strong>."
                );
                session.getTransaction().commit();
                LOGGER.info("Successful added new equipment brand by: {}. Brand: {}", loggedUser,
                    payload.reqDto().getName());
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            payload.alert().setMessage(ex.getMessage());
            LOGGER.error("Failure add new equipment brand by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        payload.alert().setActive(true);
        payload.resDto().setAlert(payload.alert());
        payload.resDto().setModalImmediatelyOpen(true);
        payload.resDto().getName().setValue(StringUtils.EMPTY);
        httpSession.setAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA.getName(), payload.resDto());
        res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
