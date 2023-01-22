<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="employerData" class="pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły pracownika #${employerData.id}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/employers">Lista pracowników</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły pracownika #${employerData.id}</li>
        </ol>
    </nav>
    <hr/>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-6">
                <table class="table table-fixed-width">
                    <tbody>
                    <tr>
                        <td class="fw-bold">Imię i nazwisko:</td>
                        <td>${employerData.fullName}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Data urodzenia:</td>
                        <td>
                            <fmt:parseDate value="${employerData.bornDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                            <fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Numer PESEL:</td>
                        <td>${employerData.pesel}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Wiek pracownika:</td>
                        <td>${employerData.yearsAge} lat(a)</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Status konta:</td>
                        <td class="fw-bold ${employerData.accountStateColor}">${employerData.accountState}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Adres email:</td>
                        <td><a href="mailto:${employerData.email}">${employerData.email}</a></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Adres zamieszkania:</td>
                        <td>${employerData.address}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="col-md-6">
                <table class="table table-fixed-width">
                    <tbody>
                    <tr>
                        <td class="fw-bold">Login:</td>
                        <td>${employerData.login}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Data zatrudnienia:</td>
                        <td>
                            <fmt:parseDate value="${employerData.hiredDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                            <fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Numer telefonu:</td>
                        <td><a href="tel:${employerData.phoneNumber}">${employerData.phoneNumber}</a></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Ilość lat w firmie:</td>
                        <td>${employerData.yearsHired} lat(a)</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Blokada konta:</td>
                        <td class="fw-bold ${employerData.lockedStateColor}">${employerData.lockedState}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Płeć:</td>
                        <td>${employerData.gender.name}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Miejscowość zamieszkania:</td>
                        <td>${employerData.cityWithPostCode}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</p:generic-owner.wrapper>
