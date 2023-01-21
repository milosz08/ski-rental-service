<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="searchBarData" class="java.lang.String" scope="request"/>
<jsp:useBean id="sorterData" type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.sorter.ServletSorterField>" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>

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
                <label for="search-bar"></label>
                <input type="search" class="form-control form-control-sm search-max-width" name="search-bar"
                    placeholder="Szukaj po imieniu i nazwisku" id="search-bar" value="${searchBarData}">
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
    <c:if test="${!alertData.disableContent}">
        tabela z klientami
<%--        <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>--%>
    </c:if>
</p:generic-seller.wrapper>
