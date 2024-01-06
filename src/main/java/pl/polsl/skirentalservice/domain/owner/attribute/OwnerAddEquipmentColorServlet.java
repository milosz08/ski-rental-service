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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentColorDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentColorDaoHib;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.entity.EquipmentColorEntity;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentColorAlreadyExistException;

@Slf4j
@WebServlet("/owner/add-equipment-color")
public class OwnerAddEquipmentColorServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AttributeValidatorPayloadDto payload = Utils.validateEquipmentAttribute(req, validator);
        final String loggedUser = Utils.getLoggedUserLogin(req);
        final HttpSession httpSession = req.getSession();
        if (payload.isInvalid()) {
            httpSession.setAttribute(SessionAttribute.EQ_COLORS_MODAL_DATA.getName(), payload.resDto());
            res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentColorDao equipmentDetailsDao = new EquipmentColorDaoHib(session);

                if (equipmentDetailsDao.checkIfEquipmentColorExistByName(payload.reqDto().getName())) {
                    throw new EquipmentColorAlreadyExistException();
                }
                final EquipmentColorEntity colorEntity = new EquipmentColorEntity(payload.reqDto().getName());
                session.persist(colorEntity);

                payload.alert().setType(AlertType.INFO);
                payload.alert().setMessage(
                    "Nastąpiło pomyślne dodanie nowego koloru sprzętu narciarskiego: <strong>" +
                        payload.reqDto().getName() + "</strong>."
                );
                session.getTransaction().commit();
                log.info("Successful added new equipment color by: {}. Color: {}", loggedUser,
                    payload.reqDto().getName());
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            payload.alert().setMessage(ex.getMessage());
            log.error("Failure add new equipment color by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        payload.alert().setActive(true);
        payload.resDto().setAlert(payload.alert());
        payload.resDto().setModalImmediatelyOpen(true);
        payload.resDto().getName().setValue(StringUtils.EMPTY);
        httpSession.setAttribute(SessionAttribute.EQ_COLORS_MODAL_DATA.getName(), payload.resDto());
        res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
