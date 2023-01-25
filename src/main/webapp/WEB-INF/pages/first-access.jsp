<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="firstAccessData" class="pl.polsl.skirentalservice.dto.first_access.FirstAccessResDto" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<p:generic-page.wrapper>
    <main class="d-flex justify-content-center align-items-center flex-fill my-3">
        <div class="container-sm mx-2 px-0 media-small-size-box">
            <c:if test="${alertData.active}">
            <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show lh-sm" role="alert">
                    ${alertData.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            </c:if>
            <form action="" method="post" class="card p-4" novalidate>
                <h1 class="fs-4 mb-2 fw-normal text-secondary text-center">
                    Witaj <span class="text-dark">${loggedUserDetails.fullName}</span>!
                </h1>
                <hr class="mb-4">
                <p class="lh-sm text-secondary mb-4">
                    Jest to twoje pierwsze logowanie do systemu. Z tego względu należy zmienić domyślne hasło do konta
                    oraz usługi poczty wygenerowane przez system na własne.
                </p>
                <div class="mb-2">
                    <label for="password" class="form-label mb-1 text-secondary">Nowe hasło do konta:</label>
                    <div class="input-group input-group-sm has-validation">
                        <input type="password" class="form-control form-control-sm ${firstAccessData.password.errorStyle}"
                            id="password" name="password" maxlength="25" value="${firstAccessData.password.value}">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${firstAccessData.password.message}</div>
                    </div>
                </div>
                <div class="mb-3">
                    <label for="passwordRep" class="form-label mb-1 text-secondary">Powtórz nowe hasło do konta:</label>
                    <div class="input-group input-group-sm has-validation">
                        <input type="password" class="form-control form-control-sm ${firstAccessData.passwordRep.errorStyle}"
                            id="passwordRep" name="passwordRep" maxlength="25" value="${firstAccessData.passwordRep.value}">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${firstAccessData.passwordRep.message}</div>
                    </div>
                </div>
                <hr class="mb-4">
                <div class="mb-2">
                    <label for="emailPassword" class="form-label mb-1 text-secondary">Nowe hasło do poczty:</label>
                    <div class="input-group input-group-sm has-validation">
                        <input type="password" class="form-control form-control-sm ${firstAccessData.emailPassword.errorStyle}"
                            id="emailPassword" name="emailPassword" maxlength="25" value="${firstAccessData.emailPassword.value}">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${firstAccessData.emailPassword.message}</div>
                    </div>
                </div>
                <div class="mb-4">
                    <label for="emailPasswordRep" class="form-label mb-1 text-secondary">Powtórz nowe hasło do poczty:</label>
                    <div class="input-group input-group-sm has-validation">
                        <input type="password" class="form-control form-control-sm ${firstAccessData.emailPasswordRep.errorStyle}"
                            id="emailPasswordRep" name="emailPasswordRep" maxlength="25"
                            value="${firstAccessData.emailPasswordRep.value}">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${firstAccessData.emailPasswordRep.message}</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-dark btn-sm">
                    Zatwierdź i przejdź do systemu <i class="bi bi-arrow-right ms-2"></i>
                </button>
            </form>
        </div>
    </main>
</p:generic-page.wrapper>
