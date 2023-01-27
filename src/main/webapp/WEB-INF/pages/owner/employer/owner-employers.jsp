<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="filterData" class="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="employersData" type="java.util.List<pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto>" scope="request"/>
<jsp:useBean id="sorterData" type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.paging.sorter.ServletSorterField>" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Lista pracowników</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Lista pracowników</li>
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
                    href="${pageContext.request.contextPath}/owner/add-employer">
                    <i class="bi bi-plus-lg me-1"></i>
                    Dodaj pracownika
                </a>
            </div>
        </div>
        <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
        <c:if test="${empty employersData}">
            <div class="alert alert-warning lh-sm">
                Nie znaleziono żadnych pracowników w systemie. Aby dodać nowego pracownika, kliknij w przycisk "Dodaj
                pracownika" lub w <a href="${pageContext.request.contextPath}/owner/add-employer" class="alert-link">
                ten link</a>.
            </div>
        </c:if>
        <c:if test="${not empty employersData}">
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
                                name="sortBy" type="submit" value="fullName">
                                Imię i nazwisko
                                <i class="bi bi-arrow-${sorterData.get("fullName").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                name="sortBy" type="submit" value="pesel">
                                Nr PESEL
                                <i class="bi bi-arrow-${sorterData.get("pesel").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                name="sortBy" type="submit" value="hiredDate">
                                Data zatrudnienia
                                <i class="bi bi-arrow-${sorterData.get("hiredDate").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                name="sortBy" type="submit" value="email">
                                Adres email
                                <i class="bi bi-arrow-${sorterData.get("email").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                name="sortBy" type="submit" value="phoneNumber">
                                Nr telefonu
                                <i class="bi bi-arrow-${sorterData.get("phoneNumber").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
                            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                                name="sortBy" type="submit" value="gender">
                                Płeć
                                <i class="bi bi-arrow-${sorterData.get("gender").chevronBts} mx-1 micro-font"></i>
                            </button>
                        </th>
                        <th scope="col" class="nowrap-tb fit align-middle">Akcja</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${employersData}" var="employer">
                        <tr>
                            <td class="nowrap-tb align-middle">${employer.id}</td>
                            <td class="nowrap-tb align-middle">${employer.fullName}</td>
                            <td class="nowrap-tb align-middle">${employer.pesel}</td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                                <fmt:parseDate value="${employer.hiredDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                <fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy"/>
                            </td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                                <a href="mailto:${employer.email}">${employer.email}</a>
                            </td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                                <a href="tel:${employer.phoneNumber}">${employer.phoneNumber}</a>
                            </td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">${employer.gender.name}</td>
                            <td class="nowrap-tb align-middle fit">
                                <button type="button" class="btn btn-sm btn-danger py-0 px-1" data-bs-toggle="modal"
                                    data-bs-target="#deleteEmployer${employer.id}">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                        data-bs-title="Usuń pracownika">
                                        <i class="bi bi-x-lg align-middle lh-sm"></i>
                                    </span>
                                </button>
                                <a href="${pageContext.request.contextPath}/owner/employer-details?id=${employer.id}"
                                    class="btn btn-sm btn-dark py-0">
                                    Szczegóły
                                </a>
                                <a href="${pageContext.request.contextPath}/owner/edit-employer?id=${employer.id}"
                                    class="btn btn-sm btn-outline-dark py-0">
                                    Edytuj
                                </a>
                            </td>
                        </tr>
                        <div class="modal fade" id="deleteEmployer${employer.id}" data-bs-backdrop="static"
                            tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-body lh-sm">
                                        Czy na pewno chcesz usunąć pracownika <strong>${employer.fullName}</strong>?
                                        Operacji nie można cofnąć. Można usunąć tylko tych pracowników, który w danej
                                        chwili nie obsługują żadnego wypożyczenia w systemie.
                                    </div>
                                    <div class="modal-footer">
                                        <a href="${pageContext.request.contextPath}/owner/delete-employer?id=${employer.id}"
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
</p:generic-owner.wrapper>
