<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="resetPassData" class="pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordResDto" scope="request"/>

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
                <h1 class="fs-4 mb-2 fw-normal text-secondary text-center">Zapomniałem/am hasła</h1>
                <hr class="mb-4">
                <p class="lh-sm text-secondary">
                    W celu zresetowania i przypisania nowego hasła do Twojego konta wpisz swój login lub adres email w
                    poniższym formularzu. Na skojarzony z kontem adres email przyjdzie wiadomość weryfikująca.
                </p>
                <hr class="mb-4">
                <div class="mb-3">
                    <label for="loginOrEmail" class="form-label mb-1 text-secondary">Login/adres email:</label>
                    <input type="text" class="form-control form-control-sm ${resetPassData.loginOrEmail.errorStyle}"
                        id="loginOrEmail" value="${resetPassData.loginOrEmail.value}" name="loginOrEmail">
                    <div class="invalid-feedback">${resetPassData.loginOrEmail.message}</div>
                </div>
                <button type="submit" class="btn btn-dark btn-sm">
                    Resetuj hasło <i class="bi bi-arrow-right ms-2"></i>
                </button>
            </form>
        </div>
    </main>
    <jsp:include page="/WEB-INF/partials/non-logged-footer.partial.jsp"/>
</p:generic-page.wrapper>
