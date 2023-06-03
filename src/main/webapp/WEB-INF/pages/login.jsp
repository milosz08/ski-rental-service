<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="loginData" class="pl.polsl.skirentalservice.dto.login.LoginFormResDto" scope="request"/>
<jsp:useBean id="logoutModal" class="pl.polsl.skirentalservice.dto.logout.LogoutModalDto" scope="request"/>

<p:generic-page.wrapper>
    <c:if test="${logoutModal.visible}">
        <div class="modal fade" id="logoutModal" tabindex="-1" aria-hidden="false">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-body">
                        Nastąpiło poprawne wylogowanie z systemu. Aby ponownie przejść do panelu, zaloguj się ponownie
                        na konto.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-dark btn-sm" data-bs-dismiss="modal">Zamknij okno</button>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <main class="d-flex justify-content-center align-items-center flex-fill my-3">
        <div class="container-sm mx-2 px-0 media-small-size-box">
            <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
            <form action="" method="post" class="card p-4" novalidate>
                <h1 class="fs-4 mb-2 fw-normal text-secondary text-center">Logowanie do systemu</h1>
                <hr class="mb-4">
                <div class="mb-3">
                    <label for="loginOrEmail" class="form-label mb-1 text-secondary">Login/adres email:</label>
                    <input type="text" class="form-control form-control-sm ${loginData.loginOrEmail.errorStyle}"
                        id="loginOrEmail" value="${loginData.loginOrEmail.value}" name="loginOrEmail">
                    <div class="invalid-feedback">${loginData.loginOrEmail.message}</div>
                </div>
                <div class="mb-4">
                    <label for="password" class="form-label mb-1 text-secondary">Hasło:</label>
                    <div class="input-group input-group-sm has-validation">
                        <input type="password" class="form-control form-control-sm ${loginData.password.errorStyle}"
                            id="password" name="password">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${loginData.password.message}</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-dark btn-sm">
                    Zaloguj <i class="bi bi-arrow-right ms-2"></i>
                </button>
                <hr class="mb-3">
                <a class="link-dark text-center" href="${pageContext.request.contextPath}/forgot-password-request">
                    Zapomniałem/am hasła
                </a>
            </form>
        </div>
    </main>
    <jsp:include page="/WEB-INF/partials/non-logged-footer.partial.jsp"/>
</p:generic-page.wrapper>
