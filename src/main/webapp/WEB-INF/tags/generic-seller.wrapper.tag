<%@ tag description="Seller panel wrapper template" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<p:generic-page.wrapper>
    <div class="container-fluid d-flex flex-column vh-100">
        <div class="row flex-fill flex-nowrap">
            <p:generic-left-nav.wrapper requestPath="seller">
                <li class="mb-1">
                    Wewnątrz wrappera
                </li>
            </p:generic-left-nav.wrapper>
            <div class="col px-0 d-flex flex-column w-100">
                <jsp:include page="../../partials/logged-user-top-bar.partial.jsp">
                    <jsp:param name="requestPath" value="seller"/>
                </jsp:include>
                <div id="content-with-footer" class="flex-fill d-flex flex-column content-with-footer simple-transition">
                    <main class="p-3 p-sm-4 flex-grow-1">
                        <jsp:doBody/>
                    </main>
                    <jsp:include page="../../partials/logged-user-footer.partial.jsp"/>
                </div>
            </div>
        </div>
    </div>
</p:generic-page.wrapper>