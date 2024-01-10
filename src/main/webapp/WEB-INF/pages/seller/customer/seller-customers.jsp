<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="filterData" type="pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto" scope="request"/>
<jsp:useBean id="customersData" type="java.util.List<pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto>"
             scope="request"/>

<p:generic-seller.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Lista klientów</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">Lista klientów</li>
    </ol>
  </nav>
  <hr/>
  <form action="" method="post" novalidate>
    <div class="row mb-3 mx-0">
      <div class="col-md-6 px-0 mb-2">
        <div class="hstack">
          <label for="searchBar"></label>
          <input type="search" class="form-control form-control-sm search-max-width" name="searchText"
                 placeholder="Szukaj po:" id="searchBar" value="${filterData.searchText}">
          <label for="searchBy">
            <select class="form-select form-select-sm fit-content ms-2" name="searchBy" id="searchBy">
              <c:forEach items="${filterData.searchBy}" var="searchItem">
                <option value="${searchItem.value}" ${searchItem.isSelected}>${searchItem.text}</option>
              </c:forEach>
            </select>
          </label>
          <button type="submit" class="btn btn-dark btn-sm ms-2">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
      <div class="col-md-6 px-0 mb-2 text-end d-flex justify-content-end">
        <a class="btn btn-sm btn-dark w-100 max-width-fit"
           href="${pageContext.request.contextPath}/seller/add-customer">
          <i class="bi bi-plus-lg me-1"></i>
          Dodaj klienta
        </a>
      </div>
    </div>
    <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
    <c:if test="${empty customersData}">
      <div class="alert alert-warning lh-sm">
        Nie znaleziono żadnych klientów w systemie. Aby dodać nowego klienta, kliknij w przycisk "Dodaj
        klienta" lub w <a href="${pageContext.request.contextPath}/seller/add-customer" class="alert-link">
        ten link</a>.
      </div>
    </c:if>
    <c:if test="${not empty customersData}">
      <div class="table-responsive">
        <table class="table table-bordered table-striped table-sm table-hover bg-white">
          <jsp:include page="/WEB-INF/partials/common/customer/common-customers.partial.jsp"/>
          <tbody>
          <c:forEach items="${customersData}" var="customer">
            <tr>
              <td class="nowrap-tb align-middle">${customer.id}</td>
              <td class="nowrap-tb align-middle">${customer.fullName}</td>
              <td class="nowrap-tb align-middle">${customer.pesel}</td>
              <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                <a href="mailto:${customer.email}">${customer.email}</a>
              </td>
              <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                <a href="tel:${customer.phoneNumber}">${customer.phoneNumber}</a>
              </td>
              <td class="nowrap-tb align-middle d-none d-lg-table-cell">${customer.address}</td>
              <td class="nowrap-tb align-middle fit">
                <button type="button" class="btn btn-sm btn-danger py-0 px-1" data-bs-toggle="modal"
                        data-bs-target="#deleteCustomer${customer.id}">
                  <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                        data-bs-title="Usuń klienta">
                    <i class="bi bi-x-lg align-middle lh-sm"></i>
                  </span>
                </button>
                <a href="${pageContext.request.contextPath}/seller/customer-details?id=${customer.id}"
                   class="btn btn-sm btn-dark py-0">
                  Szczegóły
                </a>
                <a href="${pageContext.request.contextPath}/seller/edit-customer?id=${customer.id}"
                   class="btn btn-sm btn-outline-dark py-0">
                  Edytuj
                </a>
                <a href="${pageContext.request.contextPath}/seller/create-new-rent?id=${customer.id}"
                   class="btn btn-sm btn-success py-0 px-1" type="button" data-bs-toggle="tooltip"
                   data-bs-placement="top" data-bs-title="Stwórz nowe wypożyczenie">
                  <i class="bi bi-cart-plus align-middle lh-sm"></i>
                </a>
              </td>
            </tr>
            <jsp:include page="/WEB-INF/partials/seller/delete-customer-modal.partial.jsp">
              <jsp:param name="id" value="${customer.id}"/>
              <jsp:param name="fullName" value="${customer.fullName}"/>
            </jsp:include>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>
    </c:if>
  </form>
</p:generic-seller.wrapper>
