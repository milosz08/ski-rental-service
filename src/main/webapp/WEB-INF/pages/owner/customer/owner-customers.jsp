<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="filterData" type="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="customersData" type="java.util.List<pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto>"
             scope="request"/>

<p:generic-owner.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Lista klientów</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
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
    </div>
    <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
    <c:if test="${empty customersData}">
      <div class="alert alert-warning lh-sm">Nie znaleziono żadnych klientów w systemie.</div>
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
                <a href="${pageContext.request.contextPath}/owner/customer-details?id=${customer.id}"
                   class="btn btn-sm btn-dark py-0">
                  Szczegóły
                </a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>
    </c:if>
  </form>
</p:generic-owner.wrapper>
