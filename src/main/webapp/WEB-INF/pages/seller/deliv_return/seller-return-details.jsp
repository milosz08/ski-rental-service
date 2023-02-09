<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="returnDetailsData" class="pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły zwrotu #${returnDetailsData.id}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/returns">Lista zwrotów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły zwrotu #${returnDetailsData.id}</li>
        </ol>
    </nav>
    <hr/>
    <div class="mb-3">
        <button class="btn btn-sm btn-danger me-1" data-bs-toggle="modal" data-bs-target="#deleteReturn${returnDetailsData.id}">
            <i class="bi bi-x-lg align-middle me-2 lh-sm"></i>
            Usuń zwrot
        </button>
        <a href="${pageContext.request.contextPath}/resources/return-fvs/${returnDetailsData.issuedIdentifier.replace('/', '')}.pdf"
            target="_blank" class="btn btn-success btn-sm me-1">
            <i class="bi bi-filetype-pdf align-middle me-2"></i>
            Pobierz PDF
        </a>
    </div>
    <jsp:include page="/WEB-INF/partials/common/common-return-details.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/seller/delete-return-modal.partial.jsp">
        <jsp:param name="id" value="${returnDetailsData.id}"/>
        <jsp:param name="issuedIdentifier" value="${returnDetailsData.issuedIdentifier}"/>
        <jsp:param name="rentIssuedIdentifier" value="${returnDetailsData.rentIssuedIdentifier}"/>
    </jsp:include>
</p:generic-seller.wrapper>
