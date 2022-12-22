<%@tag description="Overall Page template" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <c:if test="${empty title}"><title>Witamy | SkiRent System</title></c:if>
        <c:if test="${not empty title}"><title>${title} | SkiRent System</title></c:if>
        <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/favicon.ico">

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.2.3/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/styles.css">

        <script src="${pageContext.request.contextPath}/webjars/bootstrap/5.2.3/js/bootstrap.min.js"></script>
        <script lang="javascript" defer src="${pageContext.request.contextPath}/static/scripts.js"></script>
    </head>
    <body>
        <jsp:doBody/>
    </body>
</html>
