<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="inmemoryNewRentData" type="pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto" scope="session"/>

<td colspan="2" class="nowrap-tb align-middle">
  Podsumowanie (dni: ${inmemoryNewRentData.days}, godziny: ${inmemoryNewRentData.hours}):
</td>
<td class="nowrap-tb align-middle">${inmemoryNewRentData.totalCount} szt.</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${inmemoryNewRentData.priceUnits.totalPriceNetto}" minFractionDigits="2" type="currency"/>
</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${inmemoryNewRentData.priceUnits.totalPriceBrutto}" minFractionDigits="2" type="currency"/>
</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${inmemoryNewRentData.priceUnits.totalDepositPriceNetto}" minFractionDigits="2"
                    type="currency"/>
</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${inmemoryNewRentData.priceUnits.totalDepositPriceBrutto}" minFractionDigits="2"
                    type="currency"/>
</td>
