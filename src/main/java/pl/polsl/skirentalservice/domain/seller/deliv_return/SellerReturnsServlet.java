/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.deliv_return;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.ReturnDao;
import pl.polsl.skirentalservice.dao.hibernate.ReturnDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
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

@WebServlet("/seller/returns")
public class SellerReturnsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerReturnsServlet.class);
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(SessionAttribute.RETURNS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(SessionAttribute.RETURNS_LIST_FILTER);

        final HttpSession httpSession = req.getSession();
        final var loggedEmployer = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.COMMON_RETURNS_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final ReturnDao returnDao = new ReturnDaoHib(session);
                final Long totalReturns = returnDao.findAllReturnsFromEmployerCount(filterData, loggedEmployer.getId());

                final ServletPagination pagination = new ServletPagination(page, total, totalReturns);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<SellerRentReturnRecordResDto> returnsList = returnDao
                    .findAllPageableReturnsFromEmployerRecords(
                        new PageableDto(filterData, sorterData, page, total),
                        loggedEmployer.getId()
                    );

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("returnsData", returnsList);
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
        req.setAttribute("title", PageTitle.COMMON_RETURNS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/deliv_return/seller-returns.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "r.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(SessionAttribute.RETURNS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(SessionAttribute.RETURNS_LIST_FILTER);

        res.sendRedirect("/seller/returns?page=" + page + "&total=" + total);
    }
}
