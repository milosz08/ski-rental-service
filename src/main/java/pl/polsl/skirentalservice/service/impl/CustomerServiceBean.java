/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.ModelMapperBean;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.ServletPagination;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dao.CustomerDao;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.UserDetailsDao;
import pl.polsl.skirentalservice.dao.hibernate.CustomerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.RentDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.UserDetailsDaoHib;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.UserRole;

import java.util.List;
import java.util.Objects;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class CustomerServiceBean implements CustomerService {
    private final PersistenceBean persistenceBean;
    private final ModelMapperBean modelMapperBean;

    @Inject
    public CustomerServiceBean(
        PersistenceBean persistenceBean,
        ModelMapperBean modelMapperBean
    ) {
        this.persistenceBean = persistenceBean;
        this.modelMapperBean = modelMapperBean;
    }

    @Override
    public Slice<CustomerRecordResDto> getPageableCustomers(PageableDto pageableDto, String addressColumn) {
        return persistenceBean.startNonTransactQuery(session -> {
            final CustomerDao customerDao = new CustomerDaoHib(session);
            final Long totalCustomers = customerDao.findAllCustomersCount(pageableDto.filterData());

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalCustomers);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<CustomerRecordResDto> customersList = customerDao
                .findAllPageableCustomers(pageableDto, addressColumn);
            return new Slice<>(pagination, customersList);
        });
    }

    @Override
    public AddEditCustomerReqDto getCustomerDetails(Long customerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final CustomerDao customerDao = new CustomerDaoHib(session);
            return customerDao
                .findCustomerEditPageDetails(customerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(customerId));
        });
    }

    @Override
    public CustomerDetailsResDto getCustomerFullDetails(Long customerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final CustomerDao customerDao = new CustomerDaoHib(session);
            return customerDao
                .findCustomerDetails(customerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(customerId));
        });
    }

    @Override
    public void createNewCustomer(AddEditCustomerReqDto reqDto, LoggedUserDataDto loggedUser) {
        persistenceBean.startTransaction(session -> {
            final UserDetailsDao userDetailsDao = new UserDetailsDaoHib(session);

            if (userDetailsDao.checkIfCustomerWithSamePeselExist(reqDto.getPesel(), null)) {
                throw new AlreadyExistException.PeselAlreadyExistException(reqDto.getPesel(), UserRole.USER);
            }
            if (userDetailsDao.checkIfCustomerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), null)) {
                throw new AlreadyExistException.PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.USER);
            }
            if (userDetailsDao.checkIfCustomerWithSameEmailExist(reqDto.getEmailAddress(), null)) {
                throw new AlreadyExistException.EmailAddressAlreadyExistException(reqDto.getEmailAddress(), UserRole.USER);
            }
            final LocationAddressEntity locationAddress = modelMapperBean.map(reqDto, LocationAddressEntity.class);
            final UserDetailsEntity userDetails = modelMapperBean.map(reqDto, UserDetailsEntity.class);
            final CustomerEntity customer = new CustomerEntity(userDetails, locationAddress);

            session.persist(customer);
            session.getTransaction().commit();

            log.info("Successfully added new customer by: {}. Customer data: {}", loggedUser.getLogin(), reqDto);
        });
    }

    @Override
    public void editCustomerDetails(AddEditCustomerReqDto reqDto, Long customerId) {
        persistenceBean.startTransaction(session -> {
            final UserDetailsDao userDetailsDao = new UserDetailsDaoHib(session);

            final CustomerEntity updatableCustomer = session.get(CustomerEntity.class, customerId);
            if (updatableCustomer == null) {
                throw new NotFoundException.UserNotFoundException(customerId);
            }
            if (userDetailsDao.checkIfCustomerWithSamePeselExist(reqDto.getPesel(), customerId)) {
                throw new AlreadyExistException.PeselAlreadyExistException(reqDto.getPesel(), UserRole.USER);
            }
            if (userDetailsDao.checkIfCustomerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), customerId)) {
                throw new AlreadyExistException.PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.USER);
            }
            if (userDetailsDao.checkIfCustomerWithSameEmailExist(reqDto.getEmailAddress(), customerId)) {
                throw new AlreadyExistException.EmailAddressAlreadyExistException(reqDto.getEmailAddress(), UserRole.USER);
            }
            modelMapperBean.map(reqDto, updatableCustomer.getUserDetails());
            modelMapperBean.map(reqDto, updatableCustomer.getLocationAddress());

            session.getTransaction().commit();
            log.info("Customer with id: {} was successfuly updated. Data: {}", customerId, reqDto);
        });
    }

    @Override
    public void deleteCustomer(Long customerId, WebServletRequest req) {
        persistenceBean.startTransaction(session -> {
            final EquipmentDao equipmentDao = new EquipmentDaoHib(session);
            final CustomerDao customerDao = new CustomerDaoHib(session);
            final RentDao rentDao = new RentDaoHib(session);

            final CustomerEntity customerEntity = session.get(CustomerEntity.class, customerId);
            if (customerEntity == null) {
                throw new NotFoundException.UserNotFoundException(UserRole.SELLER);
            }
            if (customerDao.checkIfCustomerHasAnyActiveRents(customerId)) {
                throw new AlreadyExistException.CustomerHasOpenedRentsException();
            }
            for (final RentEntity rentEntity : rentDao.findAllRentsBaseCustomerId(customerId)) {
                if (rentEntity.getStatus().equals(RentStatus.RENTED)) {
                    for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                        if (equipment.getEquipment() != null) {
                            equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                                equipment.getCount());
                        }
                    }
                }
            }
            session.remove(customerEntity);
            final InMemoryRentDataDto rentData = req
                .getFromSession(SessionAttribute.IN_MEMORY_NEW_RENT_DATA, InMemoryRentDataDto.class);
            if (rentData != null && Objects.equals(rentData.getCustomerId(), customerId)) {
                req.deleteSessionAttribute(SessionAttribute.IN_MEMORY_NEW_RENT_DATA);
            }
            session.getTransaction().commit();
            log.info("Customer with id: {} was succesfuly removed from system.", customerId);
        });
    }

    @Override
    public boolean checkIfCustomerExist(Long customerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final CustomerDao customerDao = new CustomerDaoHib(session);
            return customerDao.checkIfCustomerExist(customerId);
        });
    }
}
