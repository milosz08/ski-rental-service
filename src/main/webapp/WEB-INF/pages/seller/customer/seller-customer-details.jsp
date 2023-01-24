<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="customerData" class="pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły klienta #1</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/customers">Lista klientów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły klienta #1</li>
        </ol>
    </nav>
    <hr/>
    <div class="container-fluid">
        <div class="row">
            <div class="table-responsive col-lg-12 col-xl-6">
                <table class="table table-fixed-width">
                    <tbody>
                    <tr>
                        <td class="fw-bold">Imię i nazwisko:</td>
                        <td>${customerData.fullName}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Data urodzenia:</td>
                        <td>
                            <fmt:parseDate value="${customerData.bornDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                            <fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Wiek klienta:</td>
                        <td>${customerData.yearsAge} lat(a)</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Adres email:</td>
                        <td><a href="mailto:${customerData.email}">${customerData.email}</a></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Adres zamieszkania:</td>
                        <td>${customerData.address}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="table-responsive col-lg-12 col-xl-6">
                <table class="table table-fixed-width">
                    <tbody>
                    <tr>
                        <td class="fw-bold">Numer PESEL:</td>
                        <td>${customerData.pesel}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Numer telefonu:</td>
                        <td><a href="tel:${customerData.phoneNumber}">${customerData.phoneNumber}</a></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Płeć:</td>
                        <td>${customerData.gender.name}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Miejscowość zamieszkania:</td>
                        <td>${customerData.cityWithPostCode}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</p:generic-seller.wrapper>
