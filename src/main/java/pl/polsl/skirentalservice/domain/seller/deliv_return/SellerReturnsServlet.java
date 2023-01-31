/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerReturnsServlet.java
 *  Last modified: 30/01/2023, 23:31
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.deliv_return;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.paging.filter.*;
import pl.polsl.skirentalservice.paging.sorter.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;

import java.util.*;
import java.io.IOException;

import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.COMMON_RETURNS_PAGE;
import static pl.polsl.skirentalservice.util.Utils.getAndDestroySessionAlert;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_RETURNS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/returns")
public class SellerReturnsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerReturnsServlet.class);
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
        sorterFieldMap.put("rentIssuedIdentifier", new ServletSorterField("rd.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedIdentifier", "Numerze zwrotu", "r.issuedIdentifier"));
        filterFieldMap.add(new FilterColumn("issuedDateTime", "Dacie stworzenia zwrotu", "CAST(r.issuedDateTime AS string)"));
        filterFieldMap.add(new FilterColumn("rentIssuedIdentifier", "Numerze wypożyczenia", "rd.issuedIdentifier"));
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

        final HttpSession httpSession = req.getSession();
        final var loggedEmployer = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = getAndDestroySessionAlert(req, COMMON_RETURNS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlTotalReturnsCount =
                    "SELECT COUNT(r.id) FROM RentReturnEntity r " +
                    "INNER JOIN r.rent rd INNER JOIN rd.employer e " +
                    "WHERE e.id = :eid AND " + filterData.getSearchColumn() + " LIKE :search";
                final Long totalReturns = session.createQuery(jpqlTotalReturnsCount, Long.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .setParameter("eid", loggedEmployer.getId())
                    .getSingleResult();

                final ServletPagination pagination = new ServletPagination(page, total, totalReturns);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final String jpqlFindAlReturnsConnectedWithEmployer =
                    "SELECT new pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto(" +
                        "r.id, r.issuedIdentifier, r.issuedDateTime, r.totalPrice," +
                        "CAST((rd.tax / 100) * r.totalPrice + r.totalPrice AS bigdecimal), rd.id, rd.issuedIdentifier" +
                    ") FROM RentReturnEntity r " +
                    "INNER JOIN r.rent rd INNER JOIN rd.employer e " +
                    "WHERE e.id = :eid AND " + filterData.getSearchColumn() + " LIKE :search " +
                    "ORDER BY " + sorterData.getJpql();
                final List<SellerRentReturnRecordResDto> returnsList = session
                    .createQuery(jpqlFindAlReturnsConnectedWithEmployer, SellerRentReturnRecordResDto.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .setParameter("eid", loggedEmployer.getId())
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
        req.getRequestDispatcher("/WEB-INF/pages/seller/deliv_return/seller-returns.jsp").forward(req, res);
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

        res.sendRedirect("/seller/returns?page=" + page + "&total=" + total);
    }
}
