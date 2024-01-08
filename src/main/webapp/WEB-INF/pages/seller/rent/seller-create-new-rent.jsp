<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="rentDetails" class="pl.polsl.skirentalservice.dto.rent.NewRentDetailsResDto" scope="request"/>
<jsp:useBean id="customerData" type="pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto" scope="request"/>
<jsp:useBean id="employerData" type="pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto" scope="request"/>

<p:generic-seller.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Kreator nowego wypożyczenia</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
      </li>
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/customers">Lista klientów</a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">Kreator nowego wypożyczenia</li>
    </ol>
  </nav>
  <hr/>
  <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
  <form action="" method="post" class="container-fluid px-0" novalidate>
    <div class="row">
      <div class="col-md-6">
        <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
          <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
            Dane klienta:
          </legend>
          <div class="row mt-0">
            <div class="col-12 col-md mb-3">
              <p class="mb-1 text-secondary">Tworzenie nowego wypożyczenia dla klienta:</p>
              <p class="fw-bold mb-0 lh-sm">${customerData.fullName()}, Nr PESEL: ${customerData.pesel()}</p>
              <p class="fw-bold mb-0 lh-sm">${customerData.address()}, ${customerData.cityWithPostCode()}</p>
            </div>
            <div class="col-12 col-md-auto mb-3 d-flex align-items-center">
              <button class="btn btn-dark btn-sm w-100" type="button" data-bs-toggle="modal"
                      data-bs-target="#customerMoreDetails">
                Więcej szczegółów
              </button>
            </div>
          </div>
        </fieldset>
      </div>
      <div class="col-md-6">
        <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
          <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
            Dane pracownika:
          </legend>
          <div class="row mt-0">
            <div class="col-12 col-md mb-3">
              <p class="mb-1 text-secondary">Tworzenie nowego wypożyczenia przez pracownika:</p>
              <p class="fw-bold mb-0 lh-sm">${employerData.fullName()}, Nr PESEL: ${employerData.pesel()}</p>
              <p class="fw-bold mb-0 lh-sm">${employerData.address()}, ${employerData.cityWithPostCode()}</p>
            </div>
            <div class="col-12 col-md-auto mb-3 d-flex align-items-center">
              <button class="btn btn-dark btn-sm w-100" type="button" data-bs-toggle="modal"
                      data-bs-target="#employerMoreDetails">
                Więcej szczegółów
              </button>
            </div>
          </div>
        </fieldset>
      </div>
    </div>
    <div class="row">
      <div class="col-xl-6 d-flex">
        <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3 flex-grow-1">
          <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
            Podstawowe informacje:
          </legend>
          <div class="row mt-0">
            <div class="col-md-6 mb-3">
              <label for="rentNumber" class="form-label mb-1 text-secondary micro-font">
                Nr wypożyczenia:
              </label>
              <input type="text" class="form-control form-control-sm" id="rentNumber"
                     value="${rentDetails.issuedIdentifier}" disabled>
            </div>
            <div class="col-md-6 mb-3">
              <label for="issueDateTime" class="form-label mb-1 text-secondary micro-font">
                Data wystawienia:
              </label>
              <input type="datetime-local" class="form-control form-control-sm" id="issueDateTime"
                     value="${rentDetails.issuedDateTime}" disabled>
            </div>
            <div class="col-md-6 mb-3">
              <label for="rentDateTime" class="form-label mb-1 text-secondary micro-font">
                Data wypożyczenia:
              </label>
              <input class="form-control form-control-sm ${rentDetails.rentDateTime.errorStyle} onlyNextTimeSelect"
                     id="rentDateTime" value="${rentDetails.rentDateTime.value}" name="rentDateTime"
                     type="datetime-local">
              <div class="invalid-feedback lh-sm">${rentDetails.rentDateTime.message}</div>
            </div>
            <div class="col-md-6 mb-3">
              <label for="returnDateTime" class="form-label mb-1 text-secondary micro-font">
                Przewidywana data zwrotu:
              </label>
              <input class="form-control form-control-sm ${rentDetails.returnDateTime.errorStyle} onlyNextTimeSelect"
                     id="returnDateTime" value="${rentDetails.returnDateTime.value}" name="returnDateTime"
                     type="datetime-local">
              <div class="invalid-feedback lh-sm">${rentDetails.returnDateTime.message}</div>
            </div>
          </div>
        </fieldset>
      </div>
      <div class="col-xl-6">
        <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
          <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
            Dodatkowe informacje:
          </legend>
          <div class="row mt-0">
            <div class="col-md-6 mb-3">
              <label for="rentStatus" class="form-label mb-1 text-secondary micro-font">
                Status wypożyczenia:
              </label>
              <input type="text" class="form-control form-control-sm" id="rentStatus"
                     value="${rentDetails.rentStatus.status}" disabled>
            </div>
            <div class="col-md-6 mb-3">
              <label for="tax" class="form-label mb-1 text-secondary micro-font">Podatek:</label>
              <div class="input-group input-group-sm has-validation">
                <input class="form-control form-control-sm ${rentDetails.tax.errorStyle}"
                       id="tax" name="tax" placeholder="np. 23" type="number"
                       value="${rentDetails.tax.value}" min="1" max="100" maxlength="3">
                <span class="input-group-text">%</span>
                <div class="invalid-feedback lh-sm">${rentDetails.tax.message}</div>
              </div>
            </div>
            <div class="col-md-12 mb-3">
              <label for="description" class="form-label mb-1 text-secondary micro-font">
                Dodatkowy opis (uwagi do wypożyczenia):
              </label>
              <textarea class="form-control form-control-sm ${rentDetails.description.errorStyle}"
                        rows="4" id="description" name="description" maxlength="200"
                        placeholder="Wprowadź tutaj dodatkowe uwagi do wypożyczenia. Maksymalnie 200 znaków."
              >${rentDetails.description.value}</textarea>
              <div class="invalid-feedback lh-sm">${rentDetails.description.message}</div>
            </div>
          </div>
        </fieldset>
      </div>
    </div>
    <hr/>
    <div class="hstack gap-3 justify-content-end">
      <button class="btn btn-sm btn-outline-danger" type="button" data-bs-toggle="modal"
              data-bs-target="#deleteInMemoryRentData">
        <i class="bi bi-arrow-return-left me-1 align-middle lh-sm"></i>Odrzuć zmiany
      </button>
      <button type="submit" class="btn btn-sm btn-dark">Zapisz zmiany i przejdź do kompletowania sprzętów</button>
    </div>
  </form>
  <jsp:include page="/WEB-INF/partials/seller/delete-in-memory-rent-data-modal.partial.jsp"/>
  <div class="modal fade" id="customerMoreDetails" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5">Szczegóły klienta</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body lh-sm">
          <jsp:include page="/WEB-INF/partials/common/customer/common-customer-details.partial.jsp"/>
        </div>
      </div>
    </div>
  </div>
  <div class="modal fade" id="employerMoreDetails" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5">Szczegóły pracownika</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body lh-sm">
          <jsp:include page="/WEB-INF/partials/common/employer/common-employer-details.partial.jsp"/>
        </div>
      </div>
    </div>
  </div>
</p:generic-seller.wrapper>
