<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="equipmentData" type="pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły sprzętu narciarskiego #${equipmentData.id()}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/equipments">Lista sprzętów narciarskich</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły sprzętu narciarskiego #${equipmentData.id()}</li>
        </ol>
    </nav>
    <hr/>
    <div class="mb-3">
        <button class="btn btn-sm btn-success me-1" data-bs-toggle="modal" data-bs-target="#showBarcode${equipmentData.id()}">
            <i class="bi bi-upc-scan align-middle me-2 lh-sm"></i>
            Kod kreskowy
        </button>
    </div>
    <jsp:include page="/WEB-INF/partials/common/equipment/common-equipment-details.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/common/common-bar-code-modal.partial.jsp">
        <jsp:param name="id" value="${equipmentData.id()}"/>
        <jsp:param name="barcode" value="${equipmentData.barcode()}"/>
    </jsp:include>
</p:generic-seller.wrapper>
