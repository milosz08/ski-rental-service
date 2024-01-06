<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:useBean id="filterData" type="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="returnsData"
             type="java.util.List<pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto>"
             scope="request"/>
<jsp:useBean id="sorterData"
             type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.paging.sorter.ServletSorterField>"
             scope="request"/>

<p:generic-owner.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Lista zwrotów</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">Lista zwrotów</li>
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
    <c:if test="${empty returnsData}">
      <div class="alert alert-warning lh-sm">
        Nie znaleziono żadnych zwrotów stworzonych przez jakiegokolwiek pracownika.
      </div>
    </c:if>
    <c:if test="${not empty returnsData}">
      <div class="table-responsive">
        <table class="table table-bordered table-striped table-sm table-hover bg-white">
          <thead>
          <tr>
            <th scope="col" class="nowrap-tb align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="identity">
                ID
                <i class="bi bi-arrow-${sorterData.get("identity").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="issuedIdentifier">
                Nr zwrotu
                <i class="bi bi-arrow-${sorterData.get("issuedIdentifier").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="issuedDateTime">
                Data złożenia<br>zwrotu
                <i class="bi bi-arrow-${sorterData.get("issuedDateTime").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="totalPriceNetto">
                Całkowita cena<br>netto
                <i class="bi bi-arrow-${sorterData.get("totalPriceNetto").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="totalPriceBrutto">
                Całkowita cena<br>brutto
                <i class="bi bi-arrow-${sorterData.get("totalPriceBrutto").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="rentIssuedIdentifier">
                Identyfikator<br>wypożyczenia
                <i class="bi bi-arrow-${sorterData.get("rentIssuedIdentifier").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
              <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                      name="sortBy" type="submit" value="employer">
                Imię i nazwisko<br>pracownika
                <i class="bi bi-arrow-${sorterData.get("employer").chevronBts} mx-1 micro-font"></i>
              </button>
            </th>
            <th scope="col" class="nowrap-tb fit align-middle">Akcja</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${returnsData}" var="sReturn">
            <tr>
              <td class="nowrap-tb align-middle">${sReturn.id}</td>
              <td class="nowrap-tb align-middle">${sReturn.issuedIdentifier}</td>
              <td class="nowrap-tb align-middle"><p:datetime-formatter datetime="${sReturn.issuedDateTime}"/></td>
              <td class="nowrap-tb align-middle">
                <fmt:formatNumber value="${sReturn.totalPriceNetto}" minFractionDigits="2" type="currency"/>
              </td>
              <td class="nowrap-tb align-middle">
                <fmt:formatNumber value="${sReturn.totalPriceBrutto}" minFractionDigits="2" type="currency"/>
              </td>
              <td class="nowrap-tb align-middle">
                <a href="${pageContext.request.contextPath}/owner/rent-details?id=${sReturn.rentId}">
                    ${sReturn.rentIssuedIdentifier}
                </a>
              </td>
              <td class="nowrap-tb align-middle">
                <a href="${pageContext.request.contextPath}/owner/employer-details?id=${sReturn.employerId}">
                    ${sReturn.employerFullName}
                </a>
              </td>
              <td class="nowrap-tb align-middle fit">
                <a href="${pageContext.request.contextPath}/owner/return-details?id=${sReturn.id}"
                   class="btn btn-sm btn-dark py-0">
                  Szczegóły
                </a>
                <a
                  href="${pageContext.request.contextPath}/resources/return-fvs/${sReturn.issuedIdentifier.replace('/', '')}.pdf"
                  target="_blank" class="btn btn-sm btn-success py-0">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                          data-bs-title="Pobierz zestawienie PDF">
                                        <i class="bi bi-filetype-pdf align-middle"></i>
                                    </span>
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
