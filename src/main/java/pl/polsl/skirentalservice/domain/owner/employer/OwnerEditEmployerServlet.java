/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerEditEmployerServlet.java
 *  Last modified: 09/02/2023, 01:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;
import org.modelmapper.ModelMapper;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dao.employer.*;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.dao.user_details.*;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.entity.EmployerEntity;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.UserRole.SELLER;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EDIT_EMPLOYER_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/edit-employer")
public class OwnerEditEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEditEmployerServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private ValidatorBean validator;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_EDIT_EMPLOYER_PAGE_ALERT);
        var resDto = (AddEditEmployerResDto) httpSession.getAttribute(getClass().getName());
        if (isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();
                    final IEmployerDao employerDao = new EmployerDao(session);

                    final var employerDetails = employerDao.findEmployerEditPageDetails(userId).orElseThrow(() -> {
                        throw new UserNotFoundException(userId);
                    });
                    resDto = new AddEditEmployerResDto(validator, employerDetails);

                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    if (!isNull(session)) onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(OWNER_EDIT_EMPLOYER_PAGE_ALERT.getName(), alert);
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("title", OWNER_EDIT_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String employerId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/edit-employer?id=" + employerId);
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            reqDto.validateDates(config);
            try {
                session.beginTransaction();
                final IUserDetailsDao userDetailsDao = new UserDetailsDao(session);

                final EmployerEntity updatableEmployer = session.get(EmployerEntity.class, employerId);
                if (isNull(updatableEmployer)) throw new UserNotFoundException(employerId);

                if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), updatableEmployer.getId())) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), SELLER);
                }
                if (userDetailsDao.checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), updatableEmployer.getId())) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), SELLER);
                }

                onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, updatableEmployer.getUserDetails());
                modelMapper.map(reqDto, updatableEmployer.getLocationAddress());
                modelMapper.map(reqDto, updatableEmployer);
                onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(INFO);
                httpSession.removeAttribute(getClass().getName());
                alert.setMessage(
                    "Dane pracownika z ID <strong>#" + employerId + "</strong> zostały pomyślnie zaktualizowane."
                );
                httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
                LOGGER.info("Employer with id: {} was successfuly updated. Data: {}", employerId, reqDto);
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to update existing employer with id: {}. Cause: {}", employerId, ex.getMessage());
            res.sendRedirect("/owner/edit-employer?id=" + employerId);
        }
    }
}
