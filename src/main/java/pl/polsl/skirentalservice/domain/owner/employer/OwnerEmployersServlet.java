/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerEmployersServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.util.*;
import java.io.IOException;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.sorter.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.search_filter.*;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.pagination.PaginationDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;

import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.AlertType.*;
import static pl.polsl.skirentalservice.sorter.SortDirection.*;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EMPLOYERS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.EMPLOYERS_PAGE_ALERT;
import static pl.polsl.skirentalservice.util.SessionAttribute.EMPLOYERS_LIST_SEARCH_BAR;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/employers")
public class OwnerEmployersServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerEmployersServlet.class);

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<SearchBySelectColumn> searchBy = new ArrayList<>();

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("id", new ServletSorterField("d.id "));
        sorterFieldMap.put("full-name", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName) "));
        sorterFieldMap.put("hired-date", new ServletSorterField("e.hiredDate "));
        sorterFieldMap.put("email", new ServletSorterField("d.emailAddress "));
        sorterFieldMap.put("gender", new ServletSorterField("d.gender "));
        searchBy.add(new SearchBySelectColumn(true, "fullName", "Imieniu i nazwisku", "CONCAT(d.firstName, ' ', d.lastName)"));
        searchBy.add(new SearchBySelectColumn("pesel", "Numerze PESEL", "d.pesel"));
        searchBy.add(new SearchBySelectColumn("emailAddress", "Adresie email", "d.emailAddress"));
        searchBy.add(new SearchBySelectColumn("phoneNumber", "Numerze telefonu", "d.phoneNumber"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, EMPLOYERS_PAGE_ALERT);
        final var searchFilter = (SearchFilterDto) req.getSession().getAttribute(EMPLOYERS_LIST_SEARCH_BAR.getName());
        final var searchByFullName = requireNonNullElse(searchFilter, new SearchFilterDto(searchBy));
        if (findPageableEmployer(req, alert, searchByFullName, false)) {
            res.sendRedirect("/owner/employers");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employers.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto();
        if (findPageableEmployer(req, alert, new SearchFilterDto(req, searchBy), true)) {
            res.sendRedirect("/owner/employers");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employers.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean findPageableEmployer(HttpServletRequest req, AlertTupleDto alert, SearchFilterDto filter, boolean isPost) {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        String jpqlSorterFragment = "e.id ASC";
        if (isPost) {
            final String columnName = requireNonNullElse(req.getParameter("sort-by"), "id");
            final ServletSorterField sorterField = sorterFieldMap.get(columnName);
            final String columnSorterDir = req.getParameter("sort-dir['" + columnName + "']");
            final SortDirection direction = requireNonNullElse(columnSorterDir, "ASC").equals("DESC") ? ASC : DESC;
            final ServletSorter servletSorter = new ServletSorter(sorterFieldMap, columnName);
            jpqlSorterFragment = servletSorter.setActiveFieldsAndSwapDirection();
        }
        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final Long totalEmployers = session
                    .createQuery("SELECT COUNT(e.id) FROM EmployerEntity e INNER JOIN e.role r WHERE r.id = 1", Long.class)
                    .getSingleResult();
                final PaginationDto pagination = new PaginationDto(page, total, totalEmployers);
                if (pagination.checkIfIsInvalid()) {
                    if (session.getTransaction().isActive()) session.getTransaction().rollback();
                    return true;
                }
                final String jpqlFindAllEmployers =
                    "SELECT new pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto(" +
                        "e.id, CONCAT(d.firstName, ' ', d.lastName), e.hiredDate, d.pesel, d.emailAddress," +
                        "CONCAT('+', d.phoneAreaCode, ' ', SUBSTRING(d.phoneNumber, 1, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 4, 3), ' ', SUBSTRING(d.phoneNumber, 7, 3)), d.gender" +
                    ") FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d INNER JOIN e.role r " +
                    "WHERE e.id <> 2 AND " + filter.getSearchColumn() + " LIKE :search " +
                    "ORDER BY " + jpqlSorterFragment;
                final List<EmployerRecordResDto> employersList = session
                    .createQuery(jpqlFindAllEmployers, EmployerRecordResDto.class)
                    .setParameter("search", "%" + filter.getSearchText() + "%")
                    .setFirstResult((page - 1) * total)
                    .setMaxResults(total)
                    .getResultList();
                if (employersList.isEmpty()) {
                    alert.setMessage("Nie znaleziono żadnych pracowników w systemie.");
                    alert.setType(WARN);
                    alert.setActive(true);
                    alert.setDisableContent(true);
                }
                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("employersData", employersList);
                httpSession.setAttribute(EMPLOYERS_LIST_SEARCH_BAR.getName(), filter);
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (Exception ex) {
            alert.setActive(true);
            alert.setDisableContent(true);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("title", OWNER_EMPLOYERS_PAGE.getName());
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filter);
        req.setAttribute("alertData", alert);
        return false;
    }
}
