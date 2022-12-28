<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="loginData" type="pl.polsl.skirentalservice.dto.login.LoginFormResDto" scope="request"/>
<jsp:useBean id="alertData" type="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>

<p:generic-page>
    <main class="d-flex justify-content-center align-items-center bg-light vh-100">
        <div class="container-sm mx-2 media-small-size-box">
            <c:if test="${alertData.active}">
                <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
                    ${alertData.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <form action="" method="post" class="card p-4" novalidate>
                <h1 class="fs-4 mb-4 fw-normal text-secondary text-center">Logowanie do systemu</h1>
                <div class="mb-3">
                    <label for="login" class="form-label mb-1 text-secondary">Login/email</label>
                    <input type="text" class="form-control ${loginData.login.errorStyle}" name="login" id="login"
                           value="${loginData.login.value}" placeholder="np. jankowalski123">
                    <div class="invalid-feedback">${loginData.login.message}</div>
                </div>
                <div class="mb-4">
                    <label for="password" class="form-label mb-1 text-secondary">Hasło</label>
                    <div class="input-group has-validation">
                        <input type="password" class="form-control ${loginData.password.errorStyle}" name="password"
                               id="password" placeholder="np. TajneHaslo123@">
                        <button type="button" class="input-group-text password-input-toggler">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                        <div class="invalid-feedback">${loginData.password.message}</div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">
                    Zaloguj <i class="bi bi-arrow-right ms-2"></i>
                </button>
            </form>
        </div>
    </main>
</p:generic-page>
