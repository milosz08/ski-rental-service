<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="filterData" class="pl.polsl.skirentalservice.dto.search_filter.SearchFilterDto" scope="request"/>
<jsp:useBean id="sorterData" type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.sorter.ServletSorterField>" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="employersData" type="java.util.List<pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto>" scope="request"/>

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
        <c:if test="${alertData.active}">
            <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
                    ${alertData.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${!alertData.disableContent}">
            <div class="table-responsive">
                <table class="table table-striped table-sm table-hover">
                    <thead>
                    <tr>
                        <th scope="col">Lp.</th>
                        <th scope="col">
                            <input type="hidden" name="sort-dir['full-name']" value="${sorterData.get("full-name").direction.dir}">
                            <button class="border-0 bg-transparent fw-bold" name="sort-by" type="submit" value="full-name">
                                Imię i nazwisko
                                <c:if test="${sorterData.get('full-name').active}">
                                    <i class="bi bi-arrow-${sorterData.get("full-name").chevronBts}-short fs-5"></i>
                                </c:if>
                            </button>
                        </th>
                        <th scope="col">
                            <input type="hidden" name="sort-dir['hired-date']" value="${sorterData.get("hired-date").direction.dir}">
                            <button class="border-0 bg-transparent fw-bold" name="sort-by" type="submit" value="hired-date">
                                Data zatrudnienia
                                <c:if test="${sorterData.get('hired-date').active}">
                                    <i class="bi bi-arrow-${sorterData.get("hired-date").chevronBts}-short fs-5"></i>
                                </c:if>
                            </button>
                        </th>
                        <th scope="col">PESEL</th>
                        <th scope="col">
                            <input type="hidden" name="sort-dir['email']" value="${sorterData.get("email").direction.dir}">
                            <button class="border-0 bg-transparent fw-bold" name="sort-by" type="submit" value="email">
                                Adres email
                                <c:if test="${sorterData.get('email').active}">
                                    <i class="bi bi-arrow-${sorterData.get("email").chevronBts}-short fs-5"></i>
                                </c:if>
                            </button>
                        </th>
                        <th scope="col">Nr telefonu</th>
                        <th scope="col">
                            <input type="hidden" name="sort-dir['gender']" value="${sorterData.get("gender").direction.dir}">
                            <button class="border-0 bg-transparent fw-bold" name="sort-by" type="submit" value="gender">
                                Płeć
                                <c:if test="${sorterData.get('gender').active}">
                                    <i class="bi bi-arrow-${sorterData.get("gender").chevronBts}-short fs-5"></i>
                                </c:if>
                            </button>
                        </th>
                        <th scope="col" class="fit">Akcja</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${employersData}" var="employer" varStatus="loop">
                        <tr>
                            <td class="align-middle">${loop.index + 1}</td>
                            <td class="align-middle">${employer.fullName}</td>
                            <td class="align-middle">
                                <fmt:parseDate value="${employer.hiredDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                <fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy"/>
                            </td>
                            <td class="align-middle">${employer.pesel}</td>
                            <td class="align-middle">
                                <a class="text-dark" href="mailto:${employer.email}">${employer.email}</a>
                            </td>
                            <td class="align-middle">
                                <a class="text-dark" href="tel:${employer.phoneNumber}">${employer.phoneNumber}</a>
                            </td>
                            <td class="align-middle">${employer.gender.name}</td>
                            <td class="align-middle fit">
                                <button type="button" class="btn btn-sm btn-danger me-1" data-bs-toggle="modal"
                                    data-bs-target="#deleteEmployer${employer.id}">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                        data-bs-title="Usuń pracownika">
                                        <i class="bi bi-x-lg"></i>
                                    </span>
                                </button>
                                <a href="${pageContext.request.contextPath}/owner/employer-details?id=${employer.id}"
                                    class="btn btn-sm btn-dark me-1">
                                    Szczegóły
                                </a>
                                <a href="${pageContext.request.contextPath}/owner/edit-employer?id=${employer.id}"
                                    class="btn btn-sm btn-outline-dark">
                                    Edytuj
                                </a>
                            </td>
                        </tr>
                        <div class="modal fade" id="deleteEmployer${employer.id}" data-bs-backdrop="static"
                            tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-body">
                                        Czy na pewno chcesz usunąć pracownika <strong>${employer.fullName}</strong>?
                                        Operacji nie można cofnąć.
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
