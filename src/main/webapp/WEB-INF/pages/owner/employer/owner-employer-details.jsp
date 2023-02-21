<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="employerData" class="pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły pracownika #${employerData.id()}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/employers">Lista pracowników</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły pracownika #${employerData.id()}</li>
        </ol>
    </nav>
    <hr/>
    <div class="mb-3">
        <button class="btn btn-sm btn-danger me-1" data-bs-toggle="modal" data-bs-target="#deleteEmployer${employerData.id()}">
            <i class="bi bi-x-lg align-middle me-2 lh-sm"></i>
            Usuń pracownika
        </button>
        <a href="${pageContext.request.contextPath}/owner/edit-employer?id=${employerData.id()}"
            class="btn btn-sm btn-outline-dark me-1">
            Edytuj dane pracownika
        </a>
    </div>
    <jsp:include page="/WEB-INF/partials/common/employer/common-employer-details.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/owner/delete-employer-modal.partial.jsp">
        <jsp:param name="id" value="${employerData.id()}"/>
        <jsp:param name="fullName" value="${employerData.fullName()}"/>
    </jsp:include>
</p:generic-owner.wrapper>
