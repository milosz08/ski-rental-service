<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="customerData" type="pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto" scope="request"/>

<div class="container-fluid">
  <div class="row">
    <div class="table-responsive col-lg-12 col-xl-6">
      <table class="table table-fixed-width">
        <tbody>
        <tr>
          <td class="fw-bold">Imię i nazwisko:</td>
          <td>${customerData.fullName()}</td>
        </tr>
        <tr>
          <td class="fw-bold">Data urodzenia:</td>
          <td><p:date-formatter date="${customerData.bornDate()}"/></td>
        </tr>
        <tr>
          <td class="fw-bold">Wiek klienta:</td>
          <td>${customerData.yearsAge()} lat(a)</td>
        </tr>
        <tr>
          <td class="fw-bold">Adres email:</td>
          <td><a href="mailto:${customerData.email()}">${customerData.email()}</a></td>
        </tr>
        <tr>
          <td class="fw-bold">Adres zamieszkania:</td>
          <td>${customerData.address()}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="table-responsive col-lg-12 col-xl-6">
      <table class="table table-fixed-width">
        <tbody>
        <tr>
          <td class="fw-bold">Numer PESEL:</td>
          <td>${customerData.pesel()}</td>
        </tr>
        <tr>
          <td class="fw-bold">Numer telefonu:</td>
          <td><a href="tel:${customerData.phoneNumber()}">${customerData.phoneNumber()}</a></td>
        </tr>
        <tr>
          <td class="fw-bold">Płeć:</td>
          <td>${customerData.gender().name}</td>
        </tr>
        <tr>
          <td class="fw-bold">Miejscowość zamieszkania:</td>
          <td>${customerData.cityWithPostCode()}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
