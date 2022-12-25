<%@ tag import="pl.polsl.skirentalservice.util.PageTitle" %>
<%@ tag description="Overall Page template" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    final String title = (String) request.getAttribute("title");
    request.setAttribute("pageTitle", title != null ? title : PageTitle.START_PAGE.getName());
%>
<jsp:useBean id="pageTitle" type="java.lang.String" scope="request"/>

<!DOCTYPE html>
<html lang="pl">
    <head>
        <meta charset="UTF-8">
        <title>${pageTitle} | SkiRent System</title>
        <meta name="description" content="Aplikacja do zarządzania zasobami wypożyczalni sprzętu narciarskiego.">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="format-detection" content="telephone=no">
        <meta name="keywords" content="narty, wypożyczalnia, system, java, servlet, jsp">
        <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/favicon.ico">

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap-icons/1.10.2/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.2.3/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/styles.css">

        <script lang="javascript" defer src="${pageContext.request.contextPath}/webjars/jquery/3.6.2/dist/jquery.min.js"></script>
        <script lang="javascript" defer src="${pageContext.request.contextPath}/webjars/popper.js/1.16.1/dist/popper.min.js"></script>
        <script lang="javascript" defer src="${pageContext.request.contextPath}/webjars/bootstrap/5.2.3/js/bootstrap.min.js"></script>
        <script lang="javascript" defer src="${pageContext.request.contextPath}/static/scripts.js"></script>
    </head>
    <body>
        <jsp:doBody/>
    </body>
</html>
