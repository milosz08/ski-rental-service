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
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.paging.filter.FilterColumn;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.filter.ServletFilter;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;
import pl.polsl.skirentalservice.paging.sorter.ServletSorter;
import pl.polsl.skirentalservice.paging.sorter.ServletSorterField;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.util.*;

@WebServlet("/owner/employers")
public class OwnerEmployersServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEmployersServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("e.id"));
        sorterFieldMap.put("fullName", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"));
        sorterFieldMap.put("pesel", new ServletSorterField("d.pesel"));
        sorterFieldMap.put("hiredDate", new ServletSorterField("e.hiredDate"));
        sorterFieldMap.put("email", new ServletSorterField("d.emailAddress"));
        sorterFieldMap.put("phoneNumber", new ServletSorterField("CONCAT('+', d.phoneAreaCode, ' ', d.phoneNumber)"));
        sorterFieldMap.put("gender", new ServletSorterField("d.gender"));
        filterFieldMap.add(new FilterColumn("fullName", "Imieniu i nazwisku", "CONCAT(d.firstName, ' ', d.lastName)"));
        filterFieldMap.add(new FilterColumn("pesel", "Numerze PESEL", "d.pesel"));
        filterFieldMap.add(new FilterColumn("emailAddress", "Adresie email", "d.emailAddress"));
        filterFieldMap.add(new FilterColumn("phoneNumber", "Numerze telefonu", "d.phoneNumber"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "e.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(SessionAttribute.EMPLOYERS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(SessionAttribute.EMPLOYERS_LIST_FILTER);

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final EmployerDao employerDao = new EmployerDaoHib(session);

                final Long totalEmployers = employerDao.findAllEmployersCount(filterData);
                final ServletPagination pagination = new ServletPagination(page, total, totalEmployers);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<EmployerRecordResDto> employersList = employerDao
                    .findAllPageableEmployersRecords(new PageableDto(filterData, sorterData, page, total));

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("employersData", employersList);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filterData);
        req.setAttribute("title", PageTitle.OWNER_EMPLOYERS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employers.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);
        final ServletSorter servletSorter = new ServletSorter(req, "e.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(SessionAttribute.EMPLOYERS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(SessionAttribute.EMPLOYERS_LIST_FILTER);
        res.sendRedirect("/owner/employers?page=" + page + "&total=" + total);
    }
}
