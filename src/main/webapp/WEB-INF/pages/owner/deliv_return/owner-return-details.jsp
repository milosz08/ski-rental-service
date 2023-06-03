<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="returnDetailsData" type="pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły zwrotu #${returnDetailsData.id()}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/returns">Lista zwrotów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły zwrotu #${returnDetailsData.id()}</li>
        </ol>
    </nav>
    <hr/>
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/resources/return-fvs/${returnDetailsData.issuedIdentifier().replace('/', '')}.pdf"
            target="_blank" class="btn btn-success btn-sm mb-3">
            <i class="bi bi-filetype-pdf align-middle me-2"></i>
            Pobierz PDF
        </a>
    </div>
    <jsp:include page="/WEB-INF/partials/common/common-return-details.partial.jsp"/>
</p:generic-owner.wrapper>
