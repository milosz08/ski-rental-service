<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="filterData" class="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="equipmentsData" type="java.util.List<pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto>" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Lista sprzętów narciarskich</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Lista sprzętów narciarskich</li>
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
                    href="${pageContext.request.contextPath}/owner/add-equipment">
                    <i class="bi bi-plus-lg me-1"></i>
                    Dodaj sprzęt
                </a>
            </div>
        </div>
        <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
        <c:if test="${empty equipmentsData}">
            <div class="alert alert-warning lh-sm">
                Nie znaleziono żadnego sprzętu w systemie. Aby dodać nowy sprzęt narciarski, kliknij w przycisk "Dodaj
                sprzęt" lub w <a href="${pageContext.request.contextPath}/owner/add-equipment" class="alert-link">
                ten link</a>.
            </div>
        </c:if>
        <c:if test="${not empty equipmentsData}">
            <div class="table-responsive">
                <table class="table table-bordered table-striped table-sm table-hover bg-white">
                    <jsp:include page="/WEB-INF/partials/common/equipment/common-equipments.partial.jsp"/>
                    <tbody>
                    <c:forEach items="${equipmentsData}" var="equipment">
                        <tr>
                            <td class="nowrap-tb align-middle">${equipment.id}</td>
                            <td class="nowrap-tb align-middle">${equipment.name}</td>
                            <td class="nowrap-tb align-middle">${equipment.type}</td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">${equipment.countInStore} szt.</td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                                <fmt:formatNumber value=" ${equipment.pricePerHour}" minFractionDigits="2" type="currency"/>
                            </td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">+
                                <fmt:formatNumber value=" ${equipment.priceForNextHour}" minFractionDigits="2" type="currency"/>
                            </td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                                <fmt:formatNumber value=" ${equipment.pricePerDay}" minFractionDigits="2" type="currency"/>
                            </td>
                            <td class="nowrap-tb align-middle d-none d-lg-table-cell">
                                <fmt:formatNumber value=" ${equipment.valueCost}" minFractionDigits="2" type="currency"/>
                            </td>
                            <td class="nowrap-tb align-middle fit">
                                <button type="button" class="btn btn-sm btn-danger py-0 px-1" data-bs-toggle="modal"
                                    data-bs-target="#deleteEquipment${equipment.id}">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                                        data-bs-title="Usuń sprzęt">
                                        <i class="bi bi-x-lg align-middle lh-sm"></i>
                                    </span>
                                </button>
                                <a href="${pageContext.request.contextPath}/owner/equipment-details?id=${equipment.id}"
                                    class="btn btn-sm btn-dark py-0">
                                    Szczegóły
                                </a>
                                <a href="${pageContext.request.contextPath}/owner/edit-equipment?id=${equipment.id}"
                                    class="btn btn-sm btn-outline-dark py-0">
                                    Edytuj
                                </a>
                                <button type="button" class="btn btn-sm btn-success py-0 px-1" data-bs-toggle="modal"
                                    data-bs-target="#showBarcode${equipment.id}">
                                    <span type="button" data-bs-toggle="tooltip" data-bs-placement="left"
                                        data-bs-title="Pokaż kod kreskowy">
                                        <i class="bi bi-upc-scan align-middle lh-sm"></i>
                                    </span>
                                </button>
                            </td>
                        </tr>
                        <jsp:include page="/WEB-INF/partials/owner/delete-equipment-modal.partial.jsp">
                            <jsp:param name="id" value="${equipment.id}"/>
                            <jsp:param name="name" value="${equipment.name}"/>
                        </jsp:include>
                        <jsp:include page="/WEB-INF/partials/common/common-bar-code-modal.partial.jsp">
                            <jsp:param name="id" value="${equipment.id}"/>
                            <jsp:param name="barcode" value="${equipment.barcode}"/>
                        </jsp:include>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>
        </c:if>
    </form>
</p:generic-owner.wrapper>
