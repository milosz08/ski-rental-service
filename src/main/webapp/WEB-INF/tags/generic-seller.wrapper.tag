<%@ tag description="Seller panel wrapper template" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<p:generic-page.wrapper>
    <div class="d-flex flex-grow-1 h-100">
        <p:generic-left-nav.wrapper requestPath="owner">
            <li>
                123
            </li>
        </p:generic-left-nav.wrapper>
        <div class="d-flex flex-column flex-fill">
            <jsp:include page="/WEB-INF/partials/logged-user-top-bar.partial.jsp">
                <jsp:param name="requestPath" value="owner"/>
            </jsp:include>
            <main class="p-3 p-sm-4 flex-grow-1">
                <jsp:doBody/>
            </main>
            <jsp:include page="/WEB-INF/partials/logged-user-footer.partial.jsp"/>
        </div>
    </div>
</p:generic-page.wrapper>
