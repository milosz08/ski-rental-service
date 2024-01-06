/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner;

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
import pl.polsl.skirentalservice.dao.RentDao;
import pl.polsl.skirentalservice.dao.hibernate.RentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
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

@WebServlet("/owner/rents")
public class OwnerRentsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerRentsServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("r.id"));
        sorterFieldMap.put("issuedIdentifier", new ServletSorterField("r.issuedIdentifier"));
        sorterFieldMap.put("issuedDateTime", new ServletSorterField("r.issuedDateTime"));
        sorterFieldMap.put("status", new ServletSorterField("r.status"));
        sorterFieldMap.put("totalPriceNetto", new ServletSorterField("r.totalPrice"));
        sorterFieldMap.put("totalPriceBrutto", new ServletSorterField("(r.tax / 100) * r.totalPrice + r.totalPrice"));
        sorterFieldMap.put("client", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"));
        sorterFieldMap.put("employer", new ServletSorterField("CONCAT(ed.firstName, ' ', ed.lastName)"));
        filterFieldMap.add(new FilterColumn("issuedIdentifier", "Numerze wypożyczenia", "r.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedDateTime", "Dacie stworzenia wypożyczenia", "CAST(r.issuedDateTime AS string)"));
        filterFieldMap.add(new FilterColumn("status", "Statusie wypożyczenia", "CAST(r.status AS string)"));
        filterFieldMap.add(new FilterColumn("client", "Po imieniu i nazwisku klienta", "CONCAT(d.firstName, ' ', d.lastName)"));
        filterFieldMap.add(new FilterColumn("employer", "Po imieniu i nazwisku pracownika", "CONCAT(ed.firstName, ' ', ed.lastName)"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(SessionAttribute.RENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(SessionAttribute.RENTS_LIST_FILTER);

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.COMMON_RENTS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();
                final RentDao rentDao = new RentDaoHib(session);

                final Long totalRents = rentDao.findAllRentsCount(filterData);
                final ServletPagination pagination = new ServletPagination(page, total, totalRents);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<OwnerRentRecordResDto> rentsList = rentDao
                    .findAllPageableRents(new PageableDto(filterData, sorterData, page, total));

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("rentsData", rentsList);
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
        req.setAttribute("title", PageTitle.COMMON_RENTS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/rent/owner-rents.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);
        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(SessionAttribute.RENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(SessionAttribute.RENTS_LIST_FILTER);
        res.sendRedirect("/owner/rents?page=" + page + "&total=" + total);
    }
}
