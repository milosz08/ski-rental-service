/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerEditEmployerServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dao.*;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.exception.*;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.dao.EmployerEntity;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.HibernateBean;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EDIT_EMPLOYER_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.EMPLOYERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/edit-employer")
public class OwnerEditEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEditEmployerServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        try (final Session session = database.open()) {
            try {
                session.getTransaction().begin();

                final String jpqlFindEmployerBaseId =
                    "SELECT new pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto(" +
                        "d.firstName, d.lastName, d.pesel," +
                        "CONCAT(SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), d.bornDate, e.hiredDate, a.street, a.buildingNr," +
                        "a.apartmentNr, a.city, a.postalCode, d.gender" +
                    ") FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "INNER JOIN e.locationAddress a " +
                    "WHERE e.id = :uid";
                final AddEditEmployerReqDto employerDetails = session
                    .createQuery(jpqlFindEmployerBaseId, AddEditEmployerReqDto.class)
                    .setParameter("uid", userId)
                    .getSingleResultOrNull();
                if (isNull(employerDetails)) throw new UserNotFoundException(userId);

                session.getTransaction().commit();
                req.setAttribute("addEditEmployerData", new AddEditEmployerResDto(validator, employerDetails));
                req.setAttribute("addEditText", "Edytuj");
                req.setAttribute("title", OWNER_EDIT_EMPLOYER_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(EMPLOYERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/owner/employers");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            selfRedirect(req, res, resDto, null);
            return;
        }
        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.getTransaction().begin();

                final EmployerEntity updatableEmployer = session.get(EmployerEntity.class, userId);
                if (isNull(updatableEmployer)) throw new UserNotFoundException(userId);
                if (reqDto.getBornDate().isAfter(reqDto.getHiredDate())) {
                    throw new EmployerBornHiredDateCollationException();
                }
                final String jpqlFindPesel =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d " +
                        "WHERE d.pesel = :pesel AND e.id <> :employeId";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel())
                    .setParameter("employeId", updatableEmployer.getId())
                    .getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel());

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e " +
                        "INNER JOIN e.userDetails d WHERE d.phoneNumber = :phoneNumber AND e.id <> :employeId";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber())
                    .setParameter("employeId", updatableEmployer.getId())
                    .getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber());

                final UserDetailsEntity userDetails = updatableEmployer.getUserDetails();
                userDetails.setFirstName(reqDto.getFirstName());
                userDetails.setLastName(reqDto.getLastName());
                userDetails.setPesel(reqDto.getPesel());
                userDetails.setPhoneNumber(reqDto.getPhoneNumber());
                userDetails.setBornDate(reqDto.getBornDate());
                userDetails.setGender(reqDto.getGender());

                final LocationAddressEntity locationAddress = updatableEmployer.getLocationAddress();
                locationAddress.setStreet(reqDto.getStreet());
                locationAddress.setBuildingNr(reqDto.getBuildingNr());
                locationAddress.setApartmentNr(reqDto.getApartmentNr());
                locationAddress.setCity(reqDto.getCity());
                locationAddress.setPostalCode(reqDto.getPostalCode());

                updatableEmployer.setHiredDate(reqDto.getHiredDate());
                session.getTransaction().commit();

                alert.setType(INFO);
                httpSession.setAttribute(EMPLOYERS_PAGE_ALERT.getName(), alert);
                LOGGER.info("Employer with id: {} was successfuly updated. Data: {}", userId, updatableEmployer);
                alert.setMessage(
                    "Dane pracownika z ID <strong>#" + userId + "</strong> zostały pomyślnie zaktualizowane."
                );
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (UserNotFoundException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to update employer with id: {}. Cause: {}", userId, ex.getMessage());
            httpSession.setAttribute(EMPLOYERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/owner/employers");
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            selfRedirect(req, res, resDto, alert);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, AddEditEmployerResDto resDto,
                              AlertTupleDto alert) throws ServletException, IOException {
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("alertData", alert);
        req.setAttribute("title", OWNER_EDIT_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }
}
