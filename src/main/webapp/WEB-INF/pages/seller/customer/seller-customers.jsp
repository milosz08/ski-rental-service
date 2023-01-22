<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="filterData" class="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="customersData" type="java.util.List<pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto>" scope="request"/>
<jsp:useBean id="sorterData" type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.paging.sorter.ServletSorterField>" scope="request"/>

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
        <c:if test="${alertData.active}">
            <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
                ${alertData.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${empty customersData}">
            <div class="alert alert-warning">
                Nie znaleziono żadnych klientów w systemie. Aby dodać nowego klienta, kliknij w przycisk "Dodaj
                klienta" lub w <a href="${pageContext.request.contextPath}/seller/add-customer" class="alert-link">
                ten link</a>.
            </div>
        </c:if>
        <c:if test="${not empty customersData}">
            <div class="table-responsive">
                <table class="table table-bordered table-striped table-sm table-hover">
                    <thead>
                    <tr>
                        <th scope="col">
                            <button class="border-0 bg-transparent fw-bold" name="sortBy" type="submit" value="identity">
                                ID
                                <i class="bi bi-arrow-${sorterData.get("identity").chevronBts} ms-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col">
                            <button class="border-0 bg-transparent fw-bold" name="sortBy" type="submit" value="fullName">
                                Imię i nazwisko
                                <i class="bi bi-arrow-${sorterData.get("fullName").chevronBts} ms-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col">
                            <button class="border-0 bg-transparent fw-bold" name="sortBy" type="submit" value="pesel">
                                Nr PESEL
                                <i class="bi bi-arrow-${sorterData.get("pesel").chevronBts} ms-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="d-none d-lg-table-cell">
                            <button class="border-0 bg-transparent fw-bold" name="sortBy" type="submit" value="email">
                                Adres email
                                <i class="bi bi-arrow-${sorterData.get("email").chevronBts} ms-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="d-none d-lg-table-cell">
                            <button class="border-0 bg-transparent fw-bold" name="sortBy" type="submit" value="phoneNumber">
                                Nr telefonu
                                <i class="bi bi-arrow-${sorterData.get("phoneNumber").chevronBts} ms-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="d-none d-lg-table-cell">
                            <button class="border-0 bg-transparent fw-bold" name="sortBy" type="submit" value="address">
                                Adres zamieszkania
                                <i class="bi bi-arrow-${sorterData.get("address").chevronBts} ms-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="fit">Akcja</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${customersData}" var="customer">
                        <tr>
                            <td class="align-middle">${customer.id}</td>
                            <td class="align-middle">${customer.fullName}</td>
                            <td class="align-middle">${customer.pesel}</td>
                            <td class="align-middle d-none d-lg-table-cell">
                                <a href="mailto:${customer.email}">${customer.email}</a>
                            </td>
                            <td class="align-middle d-none d-lg-table-cell">
                                <a href="tel:${customer.phoneNumber}">${customer.phoneNumber}</a>
                            </td>
                            <td class="align-middle d-none d-lg-table-cell">${customer.address}</td>
                            <td class="align-middle fit">
                                <button type="button" class="btn btn-sm btn-danger me-1" data-bs-toggle="modal"
                                    data-bs-target="#deleteCustomer${customer.id}">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                        data-bs-title="Usuń klienta">
                                        <i class="bi bi-x-lg"></i>
                                    </span>
                                </button>
                                <a href="${pageContext.request.contextPath}/seller/customer-details?id=${customer.id}"
                                    class="btn btn-sm btn-dark me-1">
                                    Szczegóły
                                </a>
                                <a href="${pageContext.request.contextPath}/seller/edit-customer?id=${customer.id}"
                                    class="btn btn-sm btn-outline-dark me-1">
                                    Edytuj
                                </a>
                                <a href="${pageContext.request.contextPath}/seller/create-reservation?id=${customer.id}"
                                    class="btn btn-sm btn-success" type="button" data-bs-toggle="tooltip"
                                    data-bs-placement="top" data-bs-title="Stwórz nowe wypożyczenie">
                                    <i class="bi bi-cart-plus"></i>
                                </a>
                            </td>
                        </tr>
                        <div class="modal fade" id="deleteCustomer${customer.id}" data-bs-backdrop="static"
                            tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-body lh-sm">
                                        Czy na pewno chcesz usunąć klienta <strong>${customer.fullName}</strong>?
                                        Operacji nie można cofnąć. Można usunąć tylko tych klientów, którzy nie mają
                                        aktywnych wypożyczeń w systemie.
                                    </div>
                                    <div class="modal-footer">
                                        <a href="${pageContext.request.contextPath}/owner/delete-employer?id=${customer.id}"
                                            type="button" class="btn btn-sm btn-outline-danger">Usuń</a>
                                        <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
                                            Zamknij
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>
        </c:if>
    </form>
</p:generic-seller.wrapper>
