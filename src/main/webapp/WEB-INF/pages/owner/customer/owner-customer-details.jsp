<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="customerData" class="pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły klienta #${customerData.id()}</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/customers">Lista klientów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły klienta #${customerData.id()}</li>
        </ol>
    </nav>
    <hr/>
    <jsp:include page="/WEB-INF/partials/common/customer/common-customer-details.partial.jsp"/>
</p:generic-owner.wrapper>
