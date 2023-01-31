<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="rentDetailsData" class="pl.polsl.skirentalservice.dto.rent.RentDetailsResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły wypożyczenia #${rentDetailsData.id}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/rents">Lista wypożyczeń</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły wypożyczenia #${rentDetailsData.id}</li>
        </ol>
    </nav>
    <hr/>
    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/resources/rent-fvs/${rentDetailsData.issuedIdentifier.replace('/', '')}.pdf"
            target="_blank" class="btn btn-success btn-sm mb-3">
            <i class="bi bi-filetype-pdf align-middle me-2"></i>
            Pobierz PDF
        </a>
    </div>
    <jsp:include page="/WEB-INF/partials/common/rent/common-rent-details.partial.jsp"/>
</p:generic-owner.wrapper>
