<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="cartEq" type="pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto" scope="request"/>

<td class="nowrap-tb align-middle">${cartEq.id}</td>
<td class="nowrap-tb align-middle">${cartEq.name}</td>
<td class="nowrap-tb align-middle">${cartEq.count} szt.</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${cartEq.priceUnits.totalPriceNetto}" minFractionDigits="2" type="currency"/>
</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${cartEq.priceUnits.totalPriceBrutto}" minFractionDigits="2" type="currency"/>
</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${cartEq.priceUnits.totalDepositPriceNetto}" minFractionDigits="2" type="currency"/>
</td>
<td class="nowrap-tb align-middle">
  <fmt:formatNumber value="${cartEq.priceUnits.totalDepositPriceBrutto}" minFractionDigits="2" type="currency"/>
</td>
