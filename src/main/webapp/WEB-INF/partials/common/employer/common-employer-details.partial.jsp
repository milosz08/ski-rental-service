<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="employerData" class="pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto" scope="request"/>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Imię i nazwisko:</td>
                    <td>${employerData.fullName()}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Data urodzenia:</td>
                    <td><p:date-formatter date="${employerData.bornDate()}"/></td>
                </tr>
                <tr>
                    <td class="fw-bold">Numer PESEL:</td>
                    <td>${employerData.pesel()}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Wiek:</td>
                    <td>${employerData.yearsAge()} lat(a)</td>
                </tr>
                <tr>
                    <td class="fw-bold">Status konta:</td>
                    <td class="fw-bold ${employerData.accountStateColor()}">${employerData.accountState()}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Adres email:</td>
                    <td><a href="mailto:${employerData.email()}">${employerData.email()}</a></td>
                </tr>
                <tr>
                    <td class="fw-bold">Adres zamieszkania:</td>
                    <td>${employerData.address()}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="col-md-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Login:</td>
                    <td>${employerData.login()}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Data zatrudnienia:</td>
                    <td><p:date-formatter date="${employerData.hiredDate()}"/></td>
                </tr>
                <tr>
                    <td class="fw-bold">Numer telefonu:</td>
                    <td><a href="tel:${employerData.phoneNumber()}">${employerData.phoneNumber()}</a></td>
                </tr>
                <tr>
                    <td class="fw-bold">Ilość lat w firmie:</td>
                    <td>${employerData.yearsHired()} lat(a)</td>
                </tr>
                <tr>
                    <td class="fw-bold">Płeć:</td>
                    <td>${employerData.gender().name}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Miejscowość zamieszkania:</td>
                    <td>${employerData.cityWithPostCode()}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
