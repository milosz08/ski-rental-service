<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-page>
    <jsp:include page="../partials/owner-header.jsp" />
    <div>To jest strona testowa, wyświetlanie danych z bazy:</div>
    <table class="table">
        <tr>
            <th>Imię</th>
            <th>Nazwisko</th>
        </tr>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
            </tr>
        </c:forEach>
    </table>
    <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/">Powrót do strony startowej</a>
    <jsp:include page="../partials/owner-footer.jsp" />
</p:generic-page>
