<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Profil kierownika</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Profil konta kierownika</li>
        </ol>
    </nav>
</p:generic-owner.wrapper>