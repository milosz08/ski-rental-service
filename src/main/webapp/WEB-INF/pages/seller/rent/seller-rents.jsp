<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-seller.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Lista wypożyczeń</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Lista wypożyczeń</li>
        </ol>
    </nav>
    <hr/>
    <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/common/rent/common-rents.partial.jsp"/>
</p:generic-seller.wrapper>
