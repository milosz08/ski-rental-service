/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerCustomersServlet.java
 *  Last modified: 25/01/2023, 14:25
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common.customer;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.paging.filter.*;
import pl.polsl.skirentalservice.paging.sorter.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;

import java.util.*;
import java.io.IOException;

import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_CUSTOMERS_PAGE;
import static pl.polsl.skirentalservice.util.Utils.getAndDestroySessionAlert;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/customers", "/owner/customers" })
public class CommonCustomersServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonCustomersServlet.class);

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    private final String addressColumn =
        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))";

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("c.id"));
        sorterFieldMap.put("fullName", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"));
        sorterFieldMap.put("pesel", new ServletSorterField("d.pesel"));
        sorterFieldMap.put("email", new ServletSorterField("d.emailAddress"));
        sorterFieldMap.put("phoneNumber", new ServletSorterField("CONCAT('+', d.phoneAreaCode, ' ', d.phoneNumber)"));
        sorterFieldMap.put("address", new ServletSorterField(addressColumn));
        filterFieldMap.add(new FilterColumn("fullName", "Imieniu i nazwisku", "CONCAT(d.firstName, ' ', d.lastName)"));
        filterFieldMap.add(new FilterColumn("pesel", "Numerze PESEL", "d.pesel"));
        filterFieldMap.add(new FilterColumn("email", "Adresie email", "d.emailAddress"));
        filterFieldMap.add(new FilterColumn("phoneNumber", "Numerze telefonu", "d.phoneNumber"));
        filterFieldMap.add(new FilterColumn("address", "Adresie zamieszkania", addressColumn));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final ServletSorter servletSorter = new ServletSorter(req, "c.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(CUSTOMERS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(CUSTOMERS_LIST_FILTER);

        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_CUSTOMERS_PAGE_ALERT);
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final Long totalEmployers = session.createQuery("SELECT COUNT(c.id) FROM CustomerEntity c", Long.class)
                    .getSingleResult();

                final ServletPagination pagination = new ServletPagination(page, total, totalEmployers);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final String jpqlFindAllCustomers =
                    "SELECT new pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto(" +
                        "c.id, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress, d.pesel," +
                        "CONCAT('+', d.phoneAreaCode, ' ', SUBSTRING(d.phoneNumber, 1, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 4, 3), ' ', SUBSTRING(d.phoneNumber, 7, 3)), " + addressColumn +
                    ") FROM CustomerEntity c " +
                    "INNER JOIN c.userDetails d INNER JOIN c.locationAddress a " +
                    "WHERE " + filterData.getSearchColumn() + " LIKE :search " +
                    "ORDER BY " + sorterData.getJpql();
                final List<EmployerRecordResDto> customersList = session
                    .createQuery(jpqlFindAllCustomers, EmployerRecordResDto.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .setFirstResult((page - 1) * total)
                    .setMaxResults(total)
                    .getResultList();

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("customersData", customersList);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setType(ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filterData);
        req.setAttribute("title", COMMON_CUSTOMERS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/customer/" +
            userDataDto.getRoleEng() + "-customers.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final ServletSorter servletSorter = new ServletSorter(req, "c.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(CUSTOMERS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(CUSTOMERS_LIST_FILTER);

        res.sendRedirect("/" + userDataDto.getRoleEng() + "/customers?page=" + page + "&total=" + total);
    }
}
