/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerReturnsServlet.java
 *  Last modified: 31/01/2023, 03:36
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.paging.sorter.*;
import pl.polsl.skirentalservice.paging.filter.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;

import java.util.*;
import java.io.IOException;
import java.util.ArrayList;

import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_RETURNS_PAGE;
import static pl.polsl.skirentalservice.util.Utils.getAndDestroySessionAlert;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RETURNS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/returns")
public class OwnerReturnsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerReturnsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("r.id"));
        sorterFieldMap.put("issuedIdentifier", new ServletSorterField("r.issuedIdentifier"));
        sorterFieldMap.put("issuedDateTime", new ServletSorterField("r.issuedDateTime"));
        sorterFieldMap.put("totalPriceNetto", new ServletSorterField("r.totalPrice"));
        sorterFieldMap.put("totalPriceBrutto", new ServletSorterField("(rd.tax / 100) * r.totalPrice + r.totalPrice"));
        sorterFieldMap.put("employer", new ServletSorterField("CONCAT(ed.firstName, ' ', ed.lastName)"));
        sorterFieldMap.put("rentIssuedIdentifier", new ServletSorterField("rd.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedIdentifier", "Numerze zwrotu", "r.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedDateTime", "Dacie stworzenia zwrotu", "CAST(r.issuedDateTime AS string)"));
        filterFieldMap.add(new FilterColumn("rentIssuedIdentifier", "Numerze wypożyczenia", "rd.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("employer", "Po imieniu i nazwisku pracownika", "CONCAT(ed.firstName, ' ', ed.lastName)"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(RETURNS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(RETURNS_LIST_FILTER);

        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_RETURNS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlTotalReturnsCount =
                    "SELECT COUNT(r.id) FROM RentReturnEntity r " +
                    "INNER JOIN r.rent rd INNER JOIN rd.employer e INNER JOIN e.userDetails ed " +
                    "WHERE " + filterData.getSearchColumn() + " LIKE :search";
                final Long totalReturns = session.createQuery(jpqlTotalReturnsCount, Long.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .getSingleResult();

                final ServletPagination pagination = new ServletPagination(page, total, totalReturns);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final String jpqlFindAlReturns =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto(" +
                        "r.id, r.issuedIdentifier, r.issuedDateTime, r.totalPrice," +
                        "CAST((rd.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal), rd.id, rd.issuedIdentifier," +
                        "e.id, CONCAT(ed.firstName, ' ', ed.lastName)" +
                    ") FROM RentReturnEntity r " +
                    "INNER JOIN r.rent rd INNER JOIN rd.employer e INNER JOIN e.userDetails ed " +
                    "WHERE " + filterData.getSearchColumn() + " LIKE :search " +
                    "ORDER BY " + sorterData.getJpql();
                final List<OwnerRentReturnRecordResDto> returnsList = session
                    .createQuery(jpqlFindAlReturns, OwnerRentReturnRecordResDto.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .setFirstResult((page - 1) * total)
                    .setMaxResults(total)
                    .getResultList();

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("returnsData", returnsList);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setType(ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filterData);
        req.setAttribute("title", COMMON_RETURNS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/deliv_return/owner-returns.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(RETURNS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(RETURNS_LIST_FILTER);

        res.sendRedirect("/owner/returns?page=" + page + "&total=" + total);
    }
}
