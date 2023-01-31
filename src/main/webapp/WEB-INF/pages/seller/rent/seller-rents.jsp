<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="filterData" class="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="rentsData" type="java.util.List<pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto>" scope="request"/>
<jsp:useBean id="sorterData" type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.paging.sorter.ServletSorterField>" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Lista wypożyczeń</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Lista wypożyczeń</li>
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
                    href="${pageContext.request.contextPath}/seller/customers">
                    <i class="bi bi-plus-lg me-1"></i>
                    Stwórz nowe wypożyczenie
                </a>
            </div>
        </div>
        <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
        <c:if test="${empty rentsData}">
            <div class="alert alert-warning lh-sm">
                Nie znaleziono żadnych wypożyczeń stworzonych z Twojego konta. Aby stworzyć nowe wypożyczenie, kliknij
                w przycisk "Stwórz nowe wypożyczenie" lub w
                <a href="${pageContext.request.contextPath}/seller/customers" class="alert-link">ten link</a>.
            </div>
        </c:if>
        <c:if test="${not empty rentsData}">
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
                                Nr wypożyczenia
                                <i class="bi bi-arrow-${sorterData.get("issuedIdentifier").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                    name="sortBy" type="submit" value="issuedDateTime">
                                Data złożenia<br>wniosku
                                <i class="bi bi-arrow-${sorterData.get("issuedDateTime").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                    name="sortBy" type="submit" value="status">
                                Status
                                <i class="bi bi-arrow-${sorterData.get("status").chevronBts} mx-1 micro-font"></i>
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
                                    name="sortBy" type="submit" value="client">
                                Klient
                                <i class="bi bi-arrow-${sorterData.get("client").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb fit align-middle">Akcja</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${rentsData}" var="rent">
                        <tr>
                            <td class="nowrap-tb align-middle">${rent.id}</td>
                            <td class="nowrap-tb align-middle">${rent.issuedIdentifier}</td>
                            <td class="nowrap-tb align-middle"><p:datetime-formatter datetime="${rent.issuedDateTime}"/></td>
                            <td class="nowrap-tb align-middle">${rent.status.status}</td>
                            <td class="nowrap-tb align-middle">
                                <fmt:formatNumber value="${rent.totalPriceNetto}" minFractionDigits="2" type="currency"/>
                            </td>
                            <td class="nowrap-tb align-middle">
                                <fmt:formatNumber value="${rent.totalPriceBrutto}" minFractionDigits="2" type="currency"/>
                            </td>
                            <td class="nowrap-tb align-middle">
                                <c:if test="${not empty rent.customerId}">
                                    <a href="${pageContext.request.contextPath}/seller/customer-details?id=${rent.customerId}">
                                        ${rent.customerName}
                                    </a>
                                </c:if>
                                <c:if test="${empty rent.customerId}">${rent.customerName}</c:if>
                            </td>
                            <td class="nowrap-tb align-middle fit">
                                <button type="button" class="btn btn-sm btn-danger py-0 px-1" data-bs-toggle="modal"
                                    data-bs-target="#deleteRent${rent.id}">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                        data-bs-title="Usuń wypożyczenie">
                                        <i class="bi bi-x-lg align-middle lh-sm"></i>
                                    </span>
                                </button>
                                <a href="${pageContext.request.contextPath}/seller/rent-details?id=${rent.id}"
                                    class="btn btn-sm btn-dark py-0">
                                    Szczegóły
                                </a>
                                <button type="button" data-bs-toggle="modal" data-bs-target="#generateReturn${rent.id}"
                                    class="btn btn-sm btn-outline-success py-0">
                                    Generuj zwrot
                                </button>
                                <a href="${pageContext.request.contextPath}/resources/rent-fvs/${rent.issuedIdentifier.replace('/', '')}.pdf"
                                    target="_blank" class="btn btn-sm btn-success py-0">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                        data-bs-title="Pobierz zestawienie PDF">
                                        <i class="bi bi-filetype-pdf align-middle"></i>
                                    </span>
                                </a>
                            </td>
                        </tr>
                        <jsp:include page="/WEB-INF/partials/seller/delete-rent-modal.partial.jsp">
                            <jsp:param name="id" value="${rent.id}"/>
                            <jsp:param name="issuedIdentifier" value="${rent.issuedIdentifier}"/>
                        </jsp:include>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>
        </c:if>
    </form>
    <c:forEach items="${rentsData}" var="rent">
        <jsp:include page="/WEB-INF/partials/seller/generate-return-modal.partial.jsp">
            <jsp:param name="id" value="${rent.id}"/>
            <jsp:param name="issuedIdentifier" value="${rent.issuedIdentifier}"/>
        </jsp:include>
    </c:forEach>
</p:generic-seller.wrapper>
