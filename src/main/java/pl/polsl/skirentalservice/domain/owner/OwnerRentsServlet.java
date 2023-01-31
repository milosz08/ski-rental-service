/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerRentsServlet.java
 *  Last modified: 30/01/2023, 18:13
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

import pl.polsl.skirentalservice.paging.sorter.*;
import pl.polsl.skirentalservice.paging.filter.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;

import java.util.*;
import java.io.IOException;

import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_RENTS_PAGE;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RENTS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/rents")
public class OwnerRentsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerRentsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(RENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(RENTS_LIST_FILTER);

        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_RENTS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlTotalRentsCount =
                    "SELECT COUNT(r.id) FROM RentEntity r " +
                    "LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c " +
                    "LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN e.userDetails ed " +
                    "WHERE " + filterData.getSearchColumn() + " LIKE :search";
                final Long totalRents = session.createQuery(jpqlTotalRentsCount, Long.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .getSingleResult();

                final ServletPagination pagination = new ServletPagination(page, total, totalRents);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final String jpqlFindAllRents =
                    "SELECT new pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto(" +
                        "r.id, r.issuedIdentifier, r.issuedDateTime, r.status, r.totalPrice," +
                        "CAST((r.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal)," +
                        "IFNULL(CONCAT(d.firstName, ' ', d.lastName), '<i>klient usunięty</i>'), c.id," +
                        "IFNULL(CONCAT(ed.firstName, ' ', ed.lastName), '<i>pracownik usunięty</i>'), ed.id" +
                    ") FROM RentEntity r " +
                    "LEFT OUTER JOIN r.employer e LEFT OUTER JOIN r.customer c " +
                    "LEFT OUTER JOIN c.userDetails d LEFT OUTER JOIN e.userDetails ed " +
                    "WHERE " + filterData.getSearchColumn() + " LIKE :search " +
                    "ORDER BY " + sorterData.getJpql();
                final List<OwnerRentRecordResDto> rentsList = session
                    .createQuery(jpqlFindAllRents, OwnerRentRecordResDto.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .setFirstResult((page - 1) * total)
                    .setMaxResults(total)
                    .getResultList();

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("rentsData", rentsList);
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
        req.setAttribute("title", COMMON_RENTS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/rent/owner-rents.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);
        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(RENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(RENTS_LIST_FILTER);
        res.sendRedirect("/owner/rents?page=" + page + "&total=" + total);
    }
}
