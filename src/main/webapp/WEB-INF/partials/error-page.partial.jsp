<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<main class="d-flex flex-column justify-content-center align-items-center vh-100">
  <div class="d-sm-flex d-none hstack gap-4 justify-content-center align-items-center mb-5">
    <h1 class="the-biggest-font-size lh-1 mb-0">${param.errorCode}</h1>
    <div class="vr"></div>
    <p class="text-secondary fs-3 mb-0 fw-light">${param.errorMessage}</p>
  </div>
  <div class="d-sm-none d-flex flex-column w-100 gap-4 mb-5 align-items-center justify-content-center">
    <h1 class="the-biggest-font-size lh-1 mb-0">${param.errorCode}</h1>
    <p class="text-secondary fs-3 mb-0 fw-light">${param.errorMessage}</p>
  </div>
  <a class="btn btn-dark" href="${pageContext.request.contextPath}${param.callbackPath}">${param.callbackMessage}</a>
</main>
