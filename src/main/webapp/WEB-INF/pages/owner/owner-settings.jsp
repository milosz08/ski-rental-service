<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="addEditOwnerData" class="pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto"
             scope="request"/>

<p:generic-owner.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Ustawienia konta</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">Ustawienia konta kierownika</li>
    </ol>
  </nav>
  <hr/>
  <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
  <form action="" class="container-fluid px-0" method="post" novalidate>
    <div class="row">
      <div class="col-md-6">
        <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
          <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
            Twoje podstawowe dane:
          </legend>
          <div class="row mt-0">
            <div class="col-xl-6 mb-3">
              <label for="firstName" class="form-label mb-1 text-secondary micro-font">Imię:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.firstName.errorStyle}"
                     id="firstName" value="${addEditOwnerData.firstName.value}" placeholder="np. Jan" name="firstName"
                     maxlength="30">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.firstName.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="lastName" class="form-label mb-1 text-secondary micro-font">Nazwisko:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.lastName.errorStyle}"
                     id="lastName" value="${addEditOwnerData.lastName.value}" placeholder="np. Kowalski" name="lastName"
                     maxlength="30">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.lastName.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="pesel" class="form-label mb-1 text-secondary micro-font">Nr PESEL:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.pesel.errorStyle}"
                     id="pesel" value="${addEditOwnerData.pesel.value}" placeholder="np. 65052859767" name="pesel"
                     maxlength="11">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.pesel.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="phoneNumber" class="form-label mb-1 text-secondary micro-font">Nr telefonu:</label>
              <div class="input-group input-group-sm has-validation">
                <span class="input-group-text">+48</span>
                <input type="tel" class="form-control form-control-sm ${addEditOwnerData.phoneNumber.errorStyle}"
                       id="phoneNumber" name="phoneNumber" placeholder="np. 123 456 789"
                       value="${addEditOwnerData.phoneNumber.value}">
                <div class="invalid-feedback lh-sm">${addEditOwnerData.phoneNumber.message}</div>
              </div>
            </div>
            <div class="col-xl-6 col-xl-2 mb-3">
              <label for="bornDate" class="form-label mb-1 text-secondary micro-font">Data urodzenia:</label>
              <input type="date"
                     class="form-control form-control-sm ${addEditOwnerData.bornDate.errorStyle} onlyPreTimeSelect"
                     id="bornDate" value="${addEditOwnerData.bornDate.value}" name="bornDate">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.bornDate.message}</div>
            </div>
            <div class="col-xl-6 col-xl-2 mb-3">
              <label for="hiredDate" class="form-label mb-1 text-secondary micro-font">Data zatrudnienia:</label>
              <input type="date"
                     class="form-control form-control-sm ${addEditOwnerData.hiredDate.errorStyle} onlyPreTimeSelect"
                     id="hiredDate" value="${addEditOwnerData.hiredDate.value}" name="hiredDate">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.hiredDate.message}</div>
            </div>
          </div>
        </fieldset>
      </div>
      <div class="col-md-6">
        <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
          <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
            Twoje dodatkowe dane:
          </legend>
          <div class="row mt-0">
            <div class="col-xl-6 mb-3">
              <label for="street" class="form-label mb-1 text-secondary micro-font">Ulica zamieszkania:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.street.errorStyle}"
                     id="street" value="${addEditOwnerData.street.value}" name="street" placeholder="np. Długa"
                     maxlength="50">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.street.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="buildingNr" class="form-label mb-1 text-secondary micro-font">Nr budynku:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.buildingNr.errorStyle}"
                     id="buildingNr" value="${addEditOwnerData.buildingNr.value}" name="buildingNr"
                     placeholder="np. 43c"
                     maxlength="5">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.buildingNr.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="apartmentNr" class="form-label mb-1 text-secondary micro-font">
                Nr mieszkania (opcjonalnie):
              </label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.apartmentNr.errorStyle}"
                     id="apartmentNr" value="${addEditOwnerData.apartmentNr.value}" name="apartmentNr"
                     placeholder="np. 12"
                     maxlength="5">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.apartmentNr.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="city" class="form-label mb-1 text-secondary micro-font">Miasto zamieszkania:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.city.errorStyle}"
                     id="city" value="${addEditOwnerData.city.value}" name="city" placeholder="np. Gliwice"
                     maxlength="70">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.city.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="postCode" class="form-label mb-1 text-secondary micro-font">Kod pocztowy:</label>
              <input type="text" class="form-control form-control-sm ${addEditOwnerData.postalCode.errorStyle}"
                     id="postCode" value="${addEditOwnerData.postalCode.value}" name="postalCode"
                     placeholder="np. 43-100"
                     maxlength="70">
              <div class="invalid-feedback lh-sm">${addEditOwnerData.postalCode.message}</div>
            </div>
            <div class="col-xl-6 mb-3">
              <label for="gender" class="form-label mb-1 text-secondary micro-font">Płeć:</label>
              <select id="gender" class="form-select form-select-sm" name="gender">
                <c:forEach items="${addEditOwnerData.genders}" var="gender">
                  <option ${gender.isSelected} value="${gender.value}">${gender.text}</option>
                </c:forEach>
              </select>
            </div>
          </div>
        </fieldset>
      </div>
    </div>
    <hr/>
    <div class="hstack gap-3 justify-content-end">
      <button class="btn btn-sm btn-outline-danger" type="button" data-bs-toggle="modal"
              data-bs-target="#rejectChanges">
        <i class="bi bi-arrow-return-left me-1 lh-sm"></i>Odrzuć zmiany
      </button>
      <button type="submit" class="btn btn-sm btn-dark">Edytuj dane osobowe</button>
    </div>
  </form>
  <jsp:include page="/WEB-INF/partials/reject-changes.partial.jsp">
    <jsp:param name="redirectPath" value="/owner/profile"/>
  </jsp:include>
</p:generic-owner.wrapper>
