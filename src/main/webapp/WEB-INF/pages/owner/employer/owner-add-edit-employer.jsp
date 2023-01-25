<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="addEditText" class="java.lang.String" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="addEditEmployerData" class="pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">${addEditText} pracownika</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/employers">Lista pracowników</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">${addEditText} pracownika</li>
        </ol>
    </nav>
    <hr/>
    <c:if test="${addEditText.equals('Dodaj')}">
        <div class="alert alert-primary lh-sm" role="alert">
            Przy tworzeniu pracownika login, adres email i hasło do konta jest generowanie automatycznie przez system.
            Tworzona jest również skrzynka pocztowa pracownika z domeną <strong>@ski.miloszgilga.pl</strong>. Do tak
            stworzonego konta przychodzi wiadomość email z hasłem wygenerowanym przez system, które pracownik przy
            pierwszym logowaniu musi zmienić (hasło do skrzynki pocztowej zostanie wysłane w wiadomości do kierownika).
        </div>
    </c:if>
    <c:if test="${alertData.active}">
        <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show lh-sm" role="alert">
            ${alertData.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <form action="" class="container-fluid px-0" method="post" novalidate>
        <div class="row">
            <div class="col-lg-4 mb-3">
                <label for="firstName" class="form-label mb-1 text-secondary">Imię pracownika:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.firstName.errorStyle}"
                    id="firstName" value="${addEditEmployerData.firstName.value}" placeholder="np. Jan" name="firstName"
                    maxlength="30">
                <div class="invalid-feedback">${addEditEmployerData.firstName.message}</div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="lastName" class="form-label mb-1 text-secondary">Nazwisko pracownika:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.lastName.errorStyle}"
                    id="lastName" value="${addEditEmployerData.lastName.value}" placeholder="np. Kowalski" name="lastName"
                    maxlength="30">
                <div class="invalid-feedback">${addEditEmployerData.lastName.message}</div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="pesel" class="form-label mb-1 text-secondary">Nr PESEL:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.pesel.errorStyle}"
                    id="pesel" value="${addEditEmployerData.pesel.value}" placeholder="np. 65052859767" name="pesel"
                    maxlength="11">
                <div class="invalid-feedback">${addEditEmployerData.pesel.message}</div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="phoneNumber" class="form-label mb-1 text-secondary">Nr telefonu pracownika:</label>
                <div class="input-group input-group-sm has-validation">
                    <span class="input-group-text">+48</span>
                    <input type="tel" class="form-control form-control-sm ${addEditEmployerData.phoneNumber.errorStyle}"
                        id="phoneNumber" name="phoneNumber" placeholder="np. 123 456 789"
                        value="${addEditEmployerData.phoneNumber.value}">
                    <div class="invalid-feedback">${addEditEmployerData.phoneNumber.message}</div>
                </div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="bornDate" class="form-label mb-1 text-secondary">Data urodzenia:</label>
                <input type="date" class="form-control form-control-sm ${addEditEmployerData.bornDate.errorStyle}"
                    id="bornDate" value="${addEditEmployerData.bornDate.value}" name="bornDate">
                <div class="invalid-feedback">${addEditEmployerData.bornDate.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="hiredDate" class="form-label mb-1 text-secondary">Data zatrudnienia:</label>
                <input type="date" class="form-control form-control-sm ${addEditEmployerData.hiredDate.errorStyle}"
                    id="hiredDate" value="${addEditEmployerData.hiredDate.value}" name="hiredDate">
                <div class="invalid-feedback">${addEditEmployerData.hiredDate.message}</div>
            </div>
            <div class="col-md-4 mb-3">
                <label for="street" class="form-label mb-1 text-secondary">Ulica zamieszkania:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.street.errorStyle}"
                    id="street" value="${addEditEmployerData.street.value}" name="street" placeholder="np. Długa"
                    maxlength="50">
                <div class="invalid-feedback">${addEditEmployerData.street.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="buildingNr" class="form-label mb-1 text-secondary">Nr budynku:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.buildingNr.errorStyle}"
                    id="buildingNr" value="${addEditEmployerData.buildingNr.value}" name="buildingNr" placeholder="np. 43c"
                    maxlength="5">
                <div class="invalid-feedback">${addEditEmployerData.buildingNr.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="apartmentNr" class="form-label mb-1 text-secondary">Nr mieszkania (opcjonalnie):</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.apartmentNr.errorStyle}"
                    id="apartmentNr" value="${addEditEmployerData.apartmentNr.value}" name="apartmentNr" placeholder="np. 12"
                    maxlength="5">
                <div class="invalid-feedback">${addEditEmployerData.apartmentNr.message}</div>
            </div>
            <div class="col-md-4 mb-3">
                <label for="city" class="form-label mb-1 text-secondary">Miasto zamieszkania:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.city.errorStyle}"
                    id="city" value="${addEditEmployerData.city.value}" name="city" placeholder="np. Gliwice"
                    maxlength="70">
                <div class="invalid-feedback">${addEditEmployerData.city.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="postCode" class="form-label mb-1 text-secondary">Kod pocztowy:</label>
                <input type="text" class="form-control form-control-sm ${addEditEmployerData.postalCode.errorStyle}"
                    id="postCode" value="${addEditEmployerData.postalCode.value}" name="postalCode" placeholder="np. 43-100"
                    maxlength="70">
                <div class="invalid-feedback">${addEditEmployerData.postalCode.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="gender" class="form-label mb-1 text-secondary">Płeć pracownika:</label>
                <select id="gender" class="form-select form-select-sm" name="gender">
                    <c:forEach items="${addEditEmployerData.genders}" var="gender">
                        <option ${gender.isSelected} value="${gender.value}">${gender.text}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <hr/>
        <div class="hstack gap-3 justify-content-end">
            <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="modal" data-bs-target="#rejectChanges">
                <i class="bi bi-arrow-return-left me-1 lh-sm"></i>Odrzuć zmiany
            </button>
            <button type="submit" class="btn btn-sm btn-dark">${addEditText} pracownika</button>
        </div>
    </form>
    <jsp:include page="/WEB-INF/partials/reject-changes.partial.jsp">
        <jsp:param name="redirectPath" value="/owner/employers"/>
    </jsp:include>
</p:generic-owner.wrapper>
