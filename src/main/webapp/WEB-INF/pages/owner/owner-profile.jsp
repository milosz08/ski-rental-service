<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
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
  <hr/>
  <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
  <jsp:include page="/WEB-INF/partials/common/employer/common-employer-details.partial.jsp"/>
</p:generic-owner.wrapper>
