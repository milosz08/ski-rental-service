/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.paging.filter.FilterColumn;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.filter.ServletFilter;
import pl.polsl.skirentalservice.paging.pagination.ServletPagination;
import pl.polsl.skirentalservice.paging.sorter.ServletSorter;
import pl.polsl.skirentalservice.paging.sorter.ServletSorterField;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;

@Slf4j
@WebServlet("/seller/complete-rent-equipments")
public class SellerCompleteRentEquipmentsServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();

    private final Map<String, ServletSorterField> sorterFieldMap = new HashMap<>();
    private final List<FilterColumn> filterFieldMap = new ArrayList<>();

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final var addModalResDto = Utils.getFromSessionAndDestroy(req, SessionAttribute.EQ_ADD_CART_MODAL_DATA.getName(),
            AddEditEquipmentCartResDto.class);
        final var editModalResDto = Utils.getFromSessionAndDestroy(req, SessionAttribute.EQ_EDIT_CART_MODAL_DATA.getName(),
            AddEditEquipmentCartResDto.class);

        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
        if (Objects.isNull(rentData) || !rentData.isAllGood()) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final ServletSorter servletSorter = new ServletSorter(req, "e.id", sorterFieldMap);
        final SorterDataDto sorterData = servletSorter.generateSortingJPQuery(SessionAttribute.RENT_EQUIPMENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        final FilterDataDto filterData = servletFilter.generateFilterJPQuery(SessionAttribute.RENT_EQUIPMENTS_LIST_FILTER);

        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT);
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final Long totalEquipments = equipmentDao.findAllEquipmentsCount(filterData);
                final ServletPagination pagination = new ServletPagination(page, total, totalEquipments);
                if (pagination.checkIfIsInvalid()) throw new RuntimeException();

                final List<EquipmentRentRecordResDto> equipmentsList = equipmentDao
                    .findAllPageableEquipments(new PageableDto(filterData, sorterData, page, total))
                    .stream()
                    .filter(l -> l.getTotalCount() > 0).collect(Collectors.toList());

                final LocalDateTime startTruncated = Utils.truncateToTotalHour(rentData.getParsedRentDateTime());
                final LocalDateTime endTruncated = Utils.truncateToTotalHour(rentData.getParsedReturnDateTime());
                final long totalRentHours = Duration.between(startTruncated, endTruncated).toHours();
                final long rentDays = totalRentHours / 24;
                rentData.setDays(rentDays);
                rentData.setHours(totalRentHours % 24);

                final PriceUnitsDto priceUnits = new PriceUnitsDto();
                int totalCounts = 0;

                for (final CartSingleEquipmentDataDto cartDto : rentData.getEquipments()) {
                    final EquipmentRentRecordResDto recordDto = equipmentsList.stream()
                        .filter(e -> e.getId().equals(cartDto.getId())).findFirst()
                        .orElseThrow(() -> new EquipmentNotFoundException(cartDto.getId().toString()));

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
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(sumPriceNetto).add(sumPriceNetto)
                        .setScale(2, RoundingMode.HALF_UP);

                    final BigDecimal depositPriceNetto = cartDto.getPriceUnits().getTotalDepositPriceNetto();
                    final BigDecimal depositPriceBrutto = taxValue
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).multiply(depositPriceNetto).add(depositPriceNetto)
                        .setScale(2, RoundingMode.HALF_UP);

                    if (!Objects.isNull(editModalResDto) && Long.parseLong(editModalResDto.getEqId()) == recordDto.getId()) {
                        cartDto.setResDto(editModalResDto);
                    }
                    cartDto.setName(recordDto.getName());
                    cartDto.setPrices(sumPriceNetto, sumPriceBrutto, depositPriceBrutto);
                    totalCounts += Integer.parseInt(cartDto.getCount());
                    priceUnits.add(sumPriceNetto, sumPriceBrutto, depositPriceNetto, depositPriceBrutto);
                }
                rentData.setTotalCount(totalCounts);
                rentData.setPriceUnits(priceUnits);
                rentData.getEquipments().sort(Comparator.comparing(CartSingleEquipmentDataDto::getId));

                session.getTransaction().commit();
                req.setAttribute("pagesData", pagination);
                req.setAttribute("equipmentsData", equipmentsList);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("sorterData", sorterFieldMap);
        req.setAttribute("filterData", filterData);
        req.setAttribute("addModalResDto", addModalResDto);
        req.setAttribute("title", PageTitle.SELLER_CREATE_NEW_RENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/rent/seller-complete-rent-equipments.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final int page = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("page"), "1"), 1);
        final int total = NumberUtils.toInt(Objects.requireNonNullElse(req.getParameter("total"), "10"), 10);

        final ServletSorter servletSorter = new ServletSorter(req, "e.id", sorterFieldMap);
        servletSorter.generateSortingJPQuery(SessionAttribute.RENT_EQUIPMENTS_LIST_SORTER);
        final ServletFilter servletFilter = new ServletFilter(req, filterFieldMap);
        servletFilter.generateFilterJPQuery(SessionAttribute.RENT_EQUIPMENTS_LIST_FILTER);

        res.sendRedirect("/seller/complete-rent-equipments?page=" + page + "&total=" + total);
    }
}
