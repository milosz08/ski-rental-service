<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="customerData" class="pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły klienta #${customerData.id}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/customers">Lista klientów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły klienta #${customerData.id}</li>
        </ol>
    </nav>
    <hr/>
    <div class="mb-3">
        <button class="btn btn-sm btn-danger me-1" data-bs-toggle="modal" data-bs-target="#deleteCustomer${customerData.id}">
            <i class="bi bi-x-lg align-middle me-2 lh-sm"></i>
            Usuń klienta
        </button>
        <a href="${pageContext.request.contextPath}/seller/edit-customer?id=${customerData.id}"
            class="btn btn-sm btn-outline-dark">
            Edytuj dane klienta
        </a>
    </div>
    <jsp:include page="/WEB-INF/partials/common/customer/common-customer-details.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/seller/delete-customer-modal.partial.jsp">
        <jsp:param name="id" value="${customerData.id}"/>
        <jsp:param name="fullName" value="${customerData.fullName}"/>
    </jsp:include>
</p:generic-seller.wrapper>
