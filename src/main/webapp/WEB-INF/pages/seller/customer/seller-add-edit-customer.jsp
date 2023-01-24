<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="addEditText" class="java.lang.String" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="addEditCustomerData" class="pl.polsl.skirentalservice.dto.customer.AddEditCustomerResDto" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">${addEditText} klienta</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/customers">Lista klientów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">${addEditText} klienta</li>
        </ol>
    </nav>
    <hr/>
    <c:if test="${alertData.active}">
        <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
            ${alertData.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <form action="" class="container-fluid px-0" method="post" novalidate>
        <div class="row">
            <div class="col-lg-4 mb-3">
                <label for="firstName" class="form-label mb-1 text-secondary">Imię klienta:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.firstName.errorStyle}"
                    id="firstName" value="${addEditCustomerData.firstName.value}" placeholder="np. Jan" name="firstName"
                    maxlength="30">
                <div class="invalid-feedback">${addEditCustomerData.firstName.message}</div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="lastName" class="form-label mb-1 text-secondary">Nazwisko klienta:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.lastName.errorStyle}"
                    id="lastName" value="${addEditCustomerData.lastName.value}" placeholder="np. Kowalski" name="lastName"
                    maxlength="30">
                <div class="invalid-feedback">${addEditCustomerData.lastName.message}</div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="pesel" class="form-label mb-1 text-secondary">Nr PESEL:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.pesel.errorStyle}"
                    id="pesel" value="${addEditCustomerData.pesel.value}" placeholder="np. 65052859767" name="pesel"
                    maxlength="11">
                <div class="invalid-feedback">${addEditCustomerData.pesel.message}</div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="emailAddress" class="form-label mb-1 text-secondary">Adres email:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.emailAddress.errorStyle}"
                    id="emailAddress" value="${addEditCustomerData.emailAddress.value}" name="emailAddress"
                    maxlength="80" placeholder="np. jankowalski@example.pl">
                <div class="invalid-feedback">${addEditCustomerData.emailAddress.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="bornDate" class="form-label mb-1 text-secondary">Data urodzenia:</label>
                <input type="date" class="form-control form-control-sm ${addEditCustomerData.bornDate.errorStyle}"
                    id="bornDate" value="${addEditCustomerData.bornDate.value}" name="bornDate">
                <div class="invalid-feedback">${addEditCustomerData.bornDate.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="gender" class="form-label mb-1 text-secondary">Płeć klienta:</label>
                <select id="gender" class="form-select form-select-sm" name="gender">
                    <c:forEach items="${addEditCustomerData.genders}" var="gender">
                        <option ${gender.isSelected} value="${gender.value}">${gender.text}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="phoneNumber" class="form-label mb-1 text-secondary">Nr telefonu klienta:</label>
                <div class="input-group input-group-sm has-validation">
                    <span class="input-group-text">+48</span>
                    <input type="tel" class="form-control form-control-sm ${addEditCustomerData.phoneNumber.errorStyle}"
                        id="phoneNumber" name="phoneNumber" placeholder="np. 123 456 789"
                        value="${addEditCustomerData.phoneNumber.value}">
                    <div class="invalid-feedback">${addEditCustomerData.phoneNumber.message}</div>
                </div>
            </div>
            <div class="col-lg-4 mb-3">
                <label for="street" class="form-label mb-1 text-secondary">Ulica zamieszkania:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.street.errorStyle}"
                    id="street" value="${addEditCustomerData.street.value}" name="street" placeholder="np. Długa"
                    maxlength="50">
                <div class="invalid-feedback">${addEditCustomerData.street.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="buildingNr" class="form-label mb-1 text-secondary">Nr budynku:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.buildingNr.errorStyle}"
                    id="buildingNr" value="${addEditCustomerData.buildingNr.value}" name="buildingNr" placeholder="np. 43c"
                    maxlength="5">
                <div class="invalid-feedback">${addEditCustomerData.buildingNr.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="apartmentNr" class="form-label mb-1 text-secondary">Nr mieszkania (opcjonalnie):</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.apartmentNr.errorStyle}"
                    id="apartmentNr" value="${addEditCustomerData.apartmentNr.value}" name="apartmentNr" placeholder="np. 12"
                    maxlength="5">
                <div class="invalid-feedback">${addEditCustomerData.apartmentNr.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="city" class="form-label mb-1 text-secondary">Miasto zamieszkania:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.city.errorStyle}"
                    id="city" value="${addEditCustomerData.city.value}" name="city" placeholder="np. Gliwice"
                    maxlength="70">
                <div class="invalid-feedback">${addEditCustomerData.city.message}</div>
            </div>
            <div class="col-lg-4 col-xl-2 mb-3">
                <label for="postCode" class="form-label mb-1 text-secondary">Kod pocztowy:</label>
                <input type="text" class="form-control form-control-sm ${addEditCustomerData.postalCode.errorStyle}"
                    id="postCode" value="${addEditCustomerData.postalCode.value}" name="postalCode" placeholder="np. 43-100"
                    maxlength="70">
                <div class="invalid-feedback">${addEditCustomerData.postalCode.message}</div>
            </div>
        </div>
        <hr/>
        <div class="hstack gap-3 justify-content-end">
            <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="modal" data-bs-target="#rejectChanges">
                <i class="bi bi-arrow-return-left me-1 lh-sm"></i>Odrzuć zmiany
            </button>
            <button type="submit" class="btn btn-sm btn-dark">${addEditText} klienta</button>
        </div>
    </form>
    <jsp:include page="/WEB-INF/partials/reject-changes.partial.jsp">
        <jsp:param name="redirectPath" value="/seller/customers"/>
    </jsp:include>
</p:generic-seller.wrapper>
