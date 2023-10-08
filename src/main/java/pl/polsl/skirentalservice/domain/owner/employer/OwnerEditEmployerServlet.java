/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.employer.EmployerDao;
import pl.polsl.skirentalservice.dao.employer.IEmployerDao;
import pl.polsl.skirentalservice.dao.user_details.IUserDetailsDao;
import pl.polsl.skirentalservice.dao.user_details.UserDetailsDao;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.util.Objects;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.PeselAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PhoneNumberAlreadyExistException;
import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;

@WebServlet("/owner/edit-employer")
public class OwnerEditEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEditEmployerServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_EDIT_EMPLOYER_PAGE_ALERT);
        var resDto = (AddEditEmployerResDto) httpSession.getAttribute(getClass().getName());
        if (Objects.isNull(resDto)) {
            try (final Session session = sessionFactory.openSession()) {
                try {
                    session.beginTransaction();
                    final IEmployerDao employerDao = new EmployerDao(session);

                    final var employerDetails = employerDao.findEmployerEditPageDetails(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));
                    resDto = new AddEditEmployerResDto(validator, employerDetails);

                    session.getTransaction().commit();
                } catch (RuntimeException ex) {
                    if (!Objects.isNull(session)) Utils.onHibernateException(session, LOGGER, ex);
                }
            } catch (RuntimeException ex) {
                alert.setMessage(ex.getMessage());
                httpSession.setAttribute(SessionAlert.OWNER_EDIT_EMPLOYER_PAGE_ALERT.getName(), alert);
            }
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Edytuj");
        req.setAttribute("title", PageTitle.OWNER_EDIT_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }

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
                if (Objects.isNull(updatableEmployer)) throw new UserNotFoundException(employerId);

                if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), updatableEmployer.getId())) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), UserRole.SELLER);
                }
                if (userDetailsDao.checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), updatableEmployer.getId())) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.SELLER);
                }

                ModelMapperGenerator.onUpdateNullableTransactTurnOn();
                modelMapper.map(reqDto, updatableEmployer.getUserDetails());
                modelMapper.map(reqDto, updatableEmployer.getLocationAddress());
                modelMapper.map(reqDto, updatableEmployer);
                ModelMapperGenerator.onUpdateNullableTransactTurnOff();

                session.getTransaction().commit();

                alert.setType(AlertType.INFO);
                httpSession.removeAttribute(getClass().getName());
                alert.setMessage(
                    "Dane pracownika z ID <strong>#" + employerId + "</strong> zostały pomyślnie zaktualizowane."
                );
                httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
                LOGGER.info("Employer with id: {} was successfuly updated. Data: {}", employerId, reqDto);
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to update existing employer with id: {}. Cause: {}", employerId, ex.getMessage());
            res.sendRedirect("/owner/edit-employer?id=" + employerId);
        }
    }
}
