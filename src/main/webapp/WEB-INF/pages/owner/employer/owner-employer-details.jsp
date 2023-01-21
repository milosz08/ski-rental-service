<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="employerData" class="pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły pracownika #1</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/employers">Lista pracowników</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">Szczegóły pracownika #1</li>
        </ol>
    </nav>
    <hr/>
    <div class="container-fluid">
        Imię i nazwisko: <br>${employerData.fullName}
    </div>
</p:generic-owner.wrapper>
