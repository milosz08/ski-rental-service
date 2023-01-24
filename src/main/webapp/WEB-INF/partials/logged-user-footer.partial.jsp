<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page import="java.time.LocalDateTime" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<footer class="p-4 bg-white text-secondary">
    <div class="container">
        <div class="row justify-content-center align-items-center hstack">
            <div class="col-md-4 mb-4 mb-md-0">
                &copy; <%= LocalDateTime.now().getYear() %> by
                <a href="${pageContext.request.contextPath}/" class="link-dark">SkiRental Service</a>
            </div>
            <div class="col-md-7 hstack gap-3">
                <div class="vr d-none d-md-block"></div>
                <div>
                    <p class="my-0">
                        Zalogowano na konto:
                        <span class="text-dark">
                            ${loggedUserDetails.fullName} (${loggedUserDetails.gender.alias})
                            (${loggedUserDetails.login}#${loggedUserDetails.id})
                        </span>
                    </p>
                    <p class="my-0">
                        Przydział praw: <span class="text-dark">
                        ${loggedUserDetails.roleName} (${loggedUserDetails.roleAlias})
                        <a class="text-dark" href="${pageContext.request.contextPath}/logout">(Wyloguj)</a>
                    </span>
                    </p>
                </div>
            </div>
        </div>
    </div>
</footer>
