<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="rentDetailsData" class="pl.polsl.skirentalservice.dto.rent.RentDetailsResDto" scope="request"/>

<p:generic-seller.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Szczegóły wypożyczenia #${rentDetailsData.id}</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
      </li>
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/rents">Lista wypożyczeń</a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">Szczegóły wypożyczenia #${rentDetailsData.id}</li>
    </ol>
  </nav>
  <hr/>
  <div class="mb-3">
    <button class="btn btn-sm btn-danger me-1" data-bs-toggle="modal" data-bs-target="#deleteRent${rentDetailsData.id}">
      <i class="bi bi-x-lg align-middle me-2 lh-sm"></i>
      Usuń wypożyczenie
    </button>
    <a
      href="${pageContext.request.contextPath}/resources/rents/${rentDetailsData.issuedIdentifier.replace('/', '-')}.pdf"
      target="_blank" class="btn btn-success btn-sm me-1">
      <i class="bi bi-filetype-pdf align-middle me-2"></i>
      Pobierz PDF
    </a>
    <c:if test="${rentDetailsData.isRented}">
      <button type="button" data-bs-toggle="modal" data-bs-target="#generateReturn${rentDetailsData.id}"
              class="btn btn-sm btn-outline-success me-1">
        Generuj zwrot
      </button>
    </c:if>
  </div>
  <jsp:include page="/WEB-INF/partials/common/rent/common-rent-details.partial.jsp"/>
  <jsp:include page="/WEB-INF/partials/seller/delete-rent-modal.partial.jsp">
    <jsp:param name="id" value="${rentDetailsData.id}"/>
    <jsp:param name="issuedIdentifier" value="${rentDetailsData.issuedIdentifier}"/>
  </jsp:include>
  <jsp:include page="/WEB-INF/partials/seller/generate-return-modal.partial.jsp">
    <jsp:param name="id" value="${rentDetailsData.id}"/>
    <jsp:param name="issuedIdentifier" value="${rentDetailsData.issuedIdentifier}"/>
  </jsp:include>
</p:generic-seller.wrapper>
