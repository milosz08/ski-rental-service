/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerCompleteRentEquipmentsServlet.java
 *  Last modified: 28/01/2023, 14:14
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.time.LocalDateTime;
import java.util.*;
import java.time.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.paging.filter.*;
import pl.polsl.skirentalservice.paging.sorter.*;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;

import static java.util.Objects.isNull;
import static java.lang.Long.parseLong;
import static java.lang.Integer.parseInt;
import static java.time.Duration.between;
import static java.util.Comparator.comparing;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.PageTitle.*;
import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/complete-rent-equipments")
public class SellerCompleteRentEquipmentsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerCompleteRentEquipmentsServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        sorterFieldMap.put("identity", new ServletSorterField("e.id"));
        sorterFieldMap.put("name", new ServletSorterField("e.name"));
        sorterFieldMap.put("type", new ServletSorterField("t.name"));
        sorterFieldMap.put("countInStore", new ServletSorterField("e.countInStore"));
        sorterFieldMap.put("pricePerHour", new ServletSorterField("e.pricePerHour"));
        sorterFieldMap.put("priceForNextHour", new ServletSorterField("e.priceForNextHour"));
        sorterFieldMap.put("pricePerDay", new ServletSorterField("e.pricePerDay"));
        filterFieldMap.add(new FilterColumn("name", "nazwie", "e.name"));
        filterFieldMap.add(new FilterColumn("type", "typie sprzętu", "t.name"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final var addModalResDto = getFromSessionAndDestroy(req, EQ_ADD_CART_MODAL_DATA.getName(),
            AddEditEquipmentCartResDto.class);
        final var editModalResDto = getFromSessionAndDestroy(req, EQ_EDIT_CART_MODAL_DATA.getName(),
            AddEditEquipmentCartResDto.class);

        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
        if (isNull(rentData) || !rentData.isAllGood()) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final ServletSorter servletSorter = new ServletSorter(req, "e.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(RENT_EQUIPMENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(RENT_EQUIPMENTS_LIST_FILTER);

        final AlertTupleDto alert = getAndDestroySessionAlert(req, SELLER_COMPLETE_RENT_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final String jpqlFindAll =
                    "SELECT COUNT(e.id) FROM EquipmentEntity e WHERE " + filterData.getSearchColumn() + " LIKE :search";
                final Long totalEquipments = session.createQuery(jpqlFindAll, Long.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .getSingleResult();

                final ServletPagination pagination = new ServletPagination(page, total, totalEquipments);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final String jpqlFindAllEquipments =
                    "SELECT new pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto(" +
                        "e.id, e.name, t.name, e.barcode, IFNULL(CAST(e.countInStore - SUM(ed.count) AS integer)," +
                        "e.countInStore), e.pricePerHour, e.priceForNextHour, e.pricePerDay, ''" +
                    ") FROM EquipmentEntity e " +
                    "LEFT OUTER JOIN e.rentsEquipments ed " +
                    "INNER JOIN e.equipmentType t " +
                    "WHERE " + filterData.getSearchColumn() + " LIKE :search GROUP BY e.id " +
                    "ORDER BY " + sorterData.getJpql();
                final List<EquipmentRentRecordResDto> preFilterEquipmentsList = session
                    .createQuery(jpqlFindAllEquipments, EquipmentRentRecordResDto.class)
                    .setParameter("search", "%" + filterData.getSearchText() + "%")
                    .setFirstResult((page - 1) * total)
                    .setMaxResults(total)
                    .getResultList();

                final List<EquipmentRentRecordResDto> equipmentsList = preFilterEquipmentsList.stream()
                    .filter(l -> l.getTotalCount() > 0).collect(Collectors.toList());

                final LocalDateTime startTruncated = truncateToTotalHour(rentData.getParsedRentDateTime());
                final LocalDateTime endTruncated = truncateToTotalHour(rentData.getParsedReturnDateTime());
                final Duration elapsed = between(startTruncated, endTruncated);
                final long totalRentHours = elapsed.toHours();
                final long rentDays = totalRentHours / 24;
                rentData.setDays(rentDays);
                rentData.setHours(totalRentHours % 24);

                final PriceUnitsDto priceUnits = new PriceUnitsDto();
                int totalCounts = 0;

                for (final CartSingleEquipmentDataDto cartDto : rentData.getEquipments()) {
                    final EquipmentRentRecordResDto recordDto = equipmentsList.stream()
                        .filter(e -> e.getId().equals(cartDto.getId())).findFirst()
                        .orElseThrow(() -> { throw new EquipmentNotFoundException(cartDto.getId().toString()); });

                    recordDto.setDisabled(true);
                    final BigDecimal totalPriceDays = recordDto.getPricePerDay().multiply(new BigDecimal(rentDays));
                    BigDecimal totalPriceHoursSum = new BigDecimal(0);
                    if ((totalRentHours % 24) > 0) {
                        totalPriceHoursSum = recordDto.getPricePerHour();
                        for (int i = 0; i < (totalRentHours % 24) - 1; i++) {
                            totalPriceHoursSum = totalPriceHoursSum.add(recordDto.getPriceForNextHour());
                        }
                    }
                    final BigDecimal totalPrice = totalPriceDays.add(totalPriceHoursSum);
                    final BigDecimal sumPriceNetto = totalPrice.multiply(new BigDecimal(cartDto.getCount()));

                    final BigDecimal taxValue = new BigDecimal(rentData.getTax());
                    final BigDecimal sumPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, HALF_UP).multiply(sumPriceNetto).add(sumPriceNetto)
                        .setScale(2, HALF_UP);

                    final BigDecimal depositPriceNetto = cartDto.getPriceUnits().getTotalDepositPriceNetto();
                    final BigDecimal depositPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, HALF_UP).multiply(depositPriceNetto).add(depositPriceNetto)
                        .setScale(2, HALF_UP);

                    if (!isNull(editModalResDto) && parseLong(editModalResDto.getEqId()) == recordDto.getId()) {
                        cartDto.setResDto(editModalResDto);
                    }
                    cartDto.setName(recordDto.getName());
                    cartDto.setPrices(sumPriceNetto, sumPriceBrutto, depositPriceBrutto);
                    totalCounts += parseInt(cartDto.getCount());
                    priceUnits.add(sumPriceNetto, sumPriceBrutto, depositPriceNetto, depositPriceBrutto);
                }
                rentData.setTotalCount(totalCounts);
                rentData.setPriceUnits(priceUnits);
                rentData.getEquipments().sort(comparing(CartSingleEquipmentDataDto::getId));

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("equipmentsData", equipmentsList);
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
        req.setAttribute("addModalResDto", addModalResDto);
        req.setAttribute("title", SELLER_CREATE_NEW_RENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/rent/seller-complete-rent-equipments.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = toInt(requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = toInt(requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "e.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(RENT_EQUIPMENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(RENT_EQUIPMENTS_LIST_FILTER);

        res.sendRedirect("/seller/complete-rent-equipments?page=" + page + "&total=" + total);
    }
}
