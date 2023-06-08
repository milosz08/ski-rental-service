/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SellerAddCustomerServlet.java
 * Last modified: 6/3/23, 12:30 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.domain.seller.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.io.IOException;
import java.time.LocalDate;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerResDto;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.dao.user_details.UserDetailsDao;
import pl.polsl.skirentalservice.dao.user_details.IUserDetailsDao;
import pl.polsl.skirentalservice.entity.CustomerEntity;
import pl.polsl.skirentalservice.entity.UserDetailsEntity;
import pl.polsl.skirentalservice.entity.LocationAddressEntity;

import static pl.polsl.skirentalservice.exception.DateException.DateInFutureException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PeselAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PhoneNumberAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.EmailAddressAlreadyExistException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/add-customer")
public class SellerAddCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerAddCustomerServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.SELLER_ADD_CUSTOMER_PAGE_ALERT);
        var resDto = Utils.getFromSessionAndDestroy(req, getClass().getName(), AddEditCustomerResDto.class);
        if (Objects.isNull(resDto)) resDto = new AddEditCustomerResDto();

        req.setAttribute("alertData", alert);
        req.setAttribute("addEditCustomerData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("title", PageTitle.SELLER_ADD_CUSTOMER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-add-edit-customer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/seller/add-customer");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            if (reqDto.getParsedBornDate().isAfter(LocalDate.now().minusYears(config.getCircaDateYears()))) {
                throw new DateInFutureException("data urodzenia", config.getCircaDateYears());
            }
            try {
                session.beginTransaction();
                final IUserDetailsDao userDetailsDao = new UserDetailsDao(session);

                if (userDetailsDao.checkIfCustomerWithSamePeselExist(reqDto.getPesel(), null)) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), UserRole.USER);
                }
                if (userDetailsDao.checkIfCustomerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), null)) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.USER);
                }
                if (userDetailsDao.checkIfCustomerWithSameEmailExist(reqDto.getEmailAddress(), null)) {
                    throw new EmailAddressAlreadyExistException(reqDto.getEmailAddress(), UserRole.USER);
                }

                final LocationAddressEntity locationAddress = modelMapper.map(reqDto, LocationAddressEntity.class);
                final UserDetailsEntity userDetails = modelMapper.map(reqDto, UserDetailsEntity.class);
                final CustomerEntity customer = new CustomerEntity(userDetails, locationAddress);

                session.persist(customer);
                session.getTransaction().commit();

                alert.setType(AlertType.INFO);
                alert.setMessage("Procedura dodawania nowego klienta do systemu zakończona sukcesem.");
                httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Successfully added new customer by: {}. Customer data: {}", loggedUser, reqDto);
                res.sendRedirect("/seller/customers");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.SELLER_ADD_CUSTOMER_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to create new customer. Cause: {}", ex.getMessage());
            res.sendRedirect("/seller/add-customer");
        }
    }
}
