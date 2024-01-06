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
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentTypeDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentTypeDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentTypeHasConnectionsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentTypeNotFoundException;

@Slf4j
@WebServlet("/owner/delete-equipment-type")
public class OwnerDeleteEquipmentTypeServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String typeId = req.getParameter("id");
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);

        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EquipmentTypeDao equipmentDetailsDao = new EquipmentTypeDaoHib(session);

                final String deletedType = equipmentDetailsDao.getEquipmentTypeNameById(typeId)
                    .orElseThrow(EquipmentTypeNotFoundException::new);

                resDto.getActiveFirstPage().setActive(false);
                resDto.getActiveSecondPage().setActive(true);

                if (equipmentDetailsDao.checkIfEquipmentTypeHasAnyConnections(typeId)) {
                    throw new EquipmentTypeHasConnectionsException();
                }
                equipmentDetailsDao.deleteEquipmentTypeById(typeId);

                alert.setType(AlertType.INFO);
                alert.setMessage(
                    "Usuwanie typu sprzętu narciarskiego: <strong>" + deletedType + "</strong> zakończone sukcesem."
                );
                session.getTransaction().commit();
                log.info("Successful deleted equipment type by: {}. Type: {}", loggedUser, deletedType);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            log.error("Failure delete equipment type by: {}. Cause: {}", loggedUser, ex.getMessage());
        }
        httpSession.setAttribute(SessionAttribute.EQ_TYPES_MODAL_DATA.getName(), resDto);
        res.sendRedirect(StringUtils.defaultIfBlank(req.getParameter("redirect"), "/owner/add-equipment"));
    }
}
