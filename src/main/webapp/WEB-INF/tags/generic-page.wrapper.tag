<%@ tag description="Overall Page template" pageEncoding="UTF-8" language="java" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="title" class="java.lang.String" scope="request"/>
<fmt:setLocale value="pl_PL" scope="session"/>

<!DOCTYPE html>
<html lang="pl">
<head>
  <meta charset="UTF-8">
  <title>${title}</title>
  <meta name="description" content="Aplikacja do zarządzania zasobami wypożyczalni sprzętu narciarskiego.">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="format-detection" content="telephone=no">
  <meta name="keywords" content="narty, wypożyczalnia, system, java, servlet, jsp">
  <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/favicon.ico">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap">
  <link rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0">
  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/webjars/bootstrap-icons/1.10.2/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/5.2.3/css/bootstrap.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/boostrap-normalizer.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/styles.css">
</head>
<body class="d-flex flex-column bg-light min-vh-100">
<jsp:doBody/>
<script lang="javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.6.2/dist/jquery.min.js"></script>
<script lang="javascript"
        src="${pageContext.request.contextPath}/webjars/popper.js/1.16.1/dist/umd/popper.min.js"></script>
<script lang="javascript"
        src="${pageContext.request.contextPath}/webjars/bootstrap/5.2.3/js/bootstrap.bundle.min.js"></script>
<script lang="javascript" src="${pageContext.request.contextPath}/static/js/scripts.js"></script>
</body>
</html>
