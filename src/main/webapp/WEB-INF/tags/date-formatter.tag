<%@ tag description="Datetime tag formatter" pageEncoding="UTF-8" language="java" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="date" required="true" %>

<fmt:parseDate value="${date}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
<fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy"/>
