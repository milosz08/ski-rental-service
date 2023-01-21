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

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("id", new ServletSorterField("d.id "));
        sorterFieldMap.put("full-name", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName) "));
        sorterFieldMap.put("hired-date", new ServletSorterField("e.hiredDate "));
        sorterFieldMap.put("email", new ServletSorterField("d.emailAddress "));
        sorterFieldMap.put("gender", new ServletSorterField("d.gender "));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, EMPLOYERS_PAGE_ALERT);
        final String searhText = (String) req.getSession().getAttribute(EMPLOYERS_LIST_SEARCH_BAR.getName());
        final String searchByFullName = requireNonNullElse(searhText, "");
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
        final String searchByFullName = req.getParameter("search-bar");
        if (findPageableEmployer(req, alert, searchByFullName, true)) {
            res.sendRedirect("/owner/employers");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-employers.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean findPageableEmployer(HttpServletRequest req, AlertTupleDto alert, String search, boolean isPost) {
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
                    "WHERE e.id <> 2 AND CONCAT(d.firstName, ' ', d.lastName) LIKE :search " +
                    "ORDER BY " + jpqlSorterFragment;
                final List<EmployerRecordResDto> employersList = session
                    .createQuery(jpqlFindAllEmployers, EmployerRecordResDto.class)
                    .setParameter("search", "%" + search + "%")
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
                req.setAttribute("searchBarData", search);
                req.setAttribute("employersData", employersList);
                httpSession.setAttribute(EMPLOYERS_LIST_SEARCH_BAR.getName(), search);
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
        req.setAttribute("alertData", alert);
        return false;
    }
}
