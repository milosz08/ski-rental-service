<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-page.wrapper>
  <jsp:include page="/WEB-INF/partials/error-page.partial.jsp">
    <jsp:param name="errorCode" value="500"/>
    <jsp:param name="errorMessage" value="Wewnętrzny błąd serwera"/>
    <jsp:param name="callbackPath" value="/"/>
    <jsp:param name="callbackMessage" value="Przejdź do strony głównej"/>
  </jsp:include>
</p:generic-page.wrapper>
