<%@ tag description="Datetime tag formatter" pageEncoding="UTF-8" language="java" %>
<%@ tag trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="datetime" required="true" %>

<fmt:parseDate value="${datetime.replace('T', ' ')}" pattern="yyyy-MM-dd HH:mm" var="parsedDate" type="date"/>
<fmt:formatDate value="${parsedDate}" type="date" pattern="dd.MM.yyyy HH:mm"/>
