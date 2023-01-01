<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="loginData" class="pl.polsl.skirentalservice.dto.login.LoginFormResDto" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="logoutModal" class="pl.polsl.skirentalservice.dto.logout.LogoutModalDto" scope="request"/>

<p:generic-page.wrapper>
    <c:if test="${logoutModal.visible}">
        <div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="logoutModalLabel" aria-hidden="false">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="logoutModalLabel">Wylogowano z systemu</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Nastąpiło poprawne wylogowanie z systemu. Aby ponownie przejść do panelu, zaloguj się ponownie
                        na konto.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-dark" data-bs-dismiss="modal">Zamknij okno</button>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <main class="d-flex justify-content-center align-items-center flex-fill">
        <div class="container-sm mx-2 px-0 media-small-size-box">
            <c:if test="${alertData.active}">
                <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
                    ${alertData.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <form action="" method="post" class="card p-4" novalidate>
                <h1 class="fs-4 mb-2 fw-normal text-secondary text-center">Logowanie do systemu</h1>
                <hr class="mb-4">
                <div class="mb-3">
                    <label for="loginOrEmail" class="form-label mb-1 text-secondary">Login/adres email:</label>
                    <input type="text" class="form-control ${loginData.loginOrEmail.errorStyle}" name="loginOrEmail"
                           id="loginOrEmail" value="${loginData.loginOrEmail.value}">
                    <div class="invalid-feedback">${loginData.loginOrEmail.message}</div>
                </div>
                <div class="mb-4">
                    <label for="password" class="form-label mb-1 text-secondary">Hasło:</label>
                    <div class="input-group has-validation">
                        <input type="password" class="form-control ${loginData.password.errorStyle}" name="password"
                               id="password">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${loginData.password.message}</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-dark">
                    Zaloguj <i class="bi bi-arrow-right ms-2"></i>
                </button>
            </form>
        </div>
    </main>
</p:generic-page.wrapper>
