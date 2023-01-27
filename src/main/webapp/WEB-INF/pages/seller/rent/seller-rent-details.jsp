<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły wypożyczenia #1</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/rents">Lista wypożyczeń</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły wypożyczenia #1</li>
        </ol>
    </nav>
    <hr/>
    <jsp:include page="/WEB-INF/partials/common/rent/common-rent-details.jsp"/>
</p:generic-seller.wrapper>
