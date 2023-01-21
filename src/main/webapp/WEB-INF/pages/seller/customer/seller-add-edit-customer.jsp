<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="addEditText" class="java.lang.String" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">${addEditText} klienta</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/customers">Lista klientów</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">${addEditText} klienta</li>
        </ol>
    </nav>
    <hr/>
    <c:if test="${alertData.active}">
        <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
            ${alertData.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <form action="" class="container-fluid" method="post" novalidate>
        to jest miejsce na form dodawania klienta
        <hr/>
        <div class="hstack gap-3 justify-content-end">
            <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="modal" data-bs-target="#rejectChanges">
                <i class="bi bi-arrow-return-left me-1 lh-sm"></i>Odrzuć zmiany
            </button>
            <button type="submit" class="btn btn-sm btn-dark">${addEditText} klienta</button>
        </div>
    </form>
    <jsp:include page="/WEB-INF/partials/reject-changes.partial.jsp">
        <jsp:param name="redirectPath" value="/seller/customers"/>
    </jsp:include>
</p:generic-seller.wrapper>
