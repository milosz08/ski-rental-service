<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="changePassData" class="pl.polsl.skirentalservice.dto.change_password.ChangeForgottenPasswordResDto" scope="request"/>
<jsp:useBean id="employerData" class="pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto" scope="request"/>

<p:generic-page.wrapper>
    <main class="d-flex justify-content-center align-items-center flex-fill my-3">
        <div class="container-sm mx-2 px-0 media-small-size-box">
            <c:if test="${alertData.active}">
                <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
                        ${alertData.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${!alertData.disableContent}">
                <form action="" method="post" class="card p-4" novalidate>
                    <img src="${employerData.imageUrl}" class="rounded-circle mx-auto d-block" alt="" width="80" height="80">
                    <h1 class="mt-4 fs-4 mb-2 fw-normal text-secondary text-center">
                        Zmiana hasła dla
                        <span class="text-dark">${employerData.fullName}</span>
                    </h1>
                    <hr class="mb-4">
                    <div class="mb-4">
                        <label for="password" class="form-label mb-1 text-secondary">Nowe hasło:</label>
                        <div class="input-group input-group-sm has-validation">
                            <input type="password" class="form-control form-control-sm ${changePassData.password.errorStyle}"
                                name="password" id="password" value="${changePassData.password.value}">
                            <button type="button" class="input-group-text password-input-toggler">
                                <i class="bi bi-eye-fill"></i>
                            </button>
                            <div class="invalid-feedback">${changePassData.password.message}</div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="passwordRepeat" class="form-label mb-1 text-secondary">Powtórz nowe hasło:</label>
                        <div class="input-group input-group-sm has-validation">
                            <input type="password" class="form-control form-control-sm ${changePassData.passwordRepeat.errorStyle}"
                                name="passwordRepeat" id="passwordRepeat" value="${changePassData.passwordRepeat.value}">
                            <button type="button" class="input-group-text password-input-toggler">
                                <i class="bi bi-eye-fill"></i>
                            </button>
                            <div class="invalid-feedback">${changePassData.passwordRepeat.message}</div>
                        </div>
                    </div>
                    <hr class="mb-4">
                    <div class="col-md-12 mb-3 text-secondary fw-normal lh-sm">
                        Hasło musi spełniać następujące kryteria:
                        <ul>
                            <li>minimum 8 znaków</li>
                            <li>przynajmniej jedna wielka i mała litera</li>
                            <li>przynajmniej jedna cyfra</li>
                            <li>przynajmniej jeden znak specjalny: #?!@$%^&*</li>
                        </ul>
                    </div>
                    <button type="submit" class="btn btn-dark btn-sm">
                        Zmień hasło <i class="bi bi-arrow-right ms-2"></i>
                    </button>
                </form>
            </c:if>
        </div>
    </main>
    <jsp:include page="/WEB-INF/partials/non-logged-footer.partial.jsp"/>
</p:generic-page.wrapper>
