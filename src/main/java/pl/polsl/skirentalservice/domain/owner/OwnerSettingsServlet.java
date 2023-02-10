/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerSettingsServlet.java
 *  Last modified: 08/02/2023, 22:06
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner;

import org.slf4j.*;
import jakarta.ejb.EJB;
import org.hibernate.*;
import org.modelmapper.ModelMapper;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.io.IOException;
import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.UserRole.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_SETTINGS_PAGE;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/settings")
public class OwnerSettingsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerSettingsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private ValidatorBean validator;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_SETTINGS_PAGE_ALERT);
        var resDto = (AddEditEmployerResDto) httpSession.getAttribute(getClass().getName());
        final var ownerDetailsDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        if (isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();

                    final String jpqlFindEmployerBaseId =
                        "SELECT new pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto(" +
                            "d.firstName, d.lastName, d.pesel," +
                            "CONCAT(SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                            "SUBSTRING(d.phoneNumber, 7, 3)), CAST(d.bornDate AS string), CAST(e.hiredDate AS string)," +
                            "a.street, a.buildingNr, a.apartmentNr, a.city, a.postalCode, d.gender" +
                        ") FROM EmployerEntity e " +
                        "INNER JOIN e.userDetails d INNER JOIN e.locationAddress a INNER JOIN e.role r " +
                        "WHERE e.id = :uid AND r.id = 2";
                    final AddEditEmployerReqDto employerDetails = session
                        .createQuery(jpqlFindEmployerBaseId, AddEditEmployerReqDto.class)
                        .setParameter("uid", ownerDetailsDto.getId())
                        .getSingleResultOrNull();
                    if (isNull(employerDetails)) throw new UserNotFoundException(OWNER);

                    resDto = new AddEditEmployerResDto(validator, employerDetails);
                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    if (!isNull(session)) onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(OWNER_SETTINGS_PAGE_ALERT.getName(), alert);
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditOwnerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("title", OWNER_SETTINGS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/owner-settings.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();
        final var ownerDetailsDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/settings");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            reqDto.validateDates(config);
            try {
                session.beginTransaction();

                final EmployerEntity updatableOwner = session.get(EmployerEntity.class, ownerDetailsDto.getId());
                if (isNull(updatableOwner)) throw new UserNotFoundException(httpSession.getId());

                final String jpqlFindPesel =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d " +
                    "WHERE d.pesel = :pesel AND e.id <> :oid";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel())
                    .setParameter("oid", ownerDetailsDto.getId())
                    .getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel(), SELLER);

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d " +
                    "WHERE d.phoneNumber = :phoneNumber AND e.id <> :oid";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber())
                    .setParameter("oid", ownerDetailsDto.getId())
                    .getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), SELLER);

                onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, updatableOwner.getUserDetails());
                modelMapper.map(reqDto, updatableOwner.getLocationAddress());
                modelMapper.map(reqDto, updatableOwner);
                onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(INFO);
                httpSession.removeAttribute(getClass().getName());
                alert.setMessage("Twoje dane osobowe zostały pomyślnie zaktualizowane.");
                httpSession.setAttribute(COMMON_PROFILE_PAGE_ALERT.getName(), alert);
                LOGGER.info("Owner with id: {} was successfuly updated. Data: {}", ownerDetailsDto.getId(), reqDto);
                res.sendRedirect("/owner/profile");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(OWNER_SETTINGS_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to update owner personal data with id: {}. Cause: {}", ownerDetailsDto.getId(),
                ex.getMessage());
            res.sendRedirect("/owner/settings");
        }
    }
}
