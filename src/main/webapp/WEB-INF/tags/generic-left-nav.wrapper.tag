<%@ tag description="Logged user left navigation template" pageEncoding="UTF-8" language="java" %>
<%@ attribute name="requestPath" required="true" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDetailsDto" scope="session"/>

<nav id="left-nav" class="col-3 px-0 bg-dark text-white left-nav simple-transition left-nav-active">
    <div class="sticky-top p-3 d-flex flex-column min-vh-100">
        <div class="mb-3">
            <h2 class="hstack gap-2 fs-5 mb-3 justify-content-center text-light fw-normal">
                <span>SkiRental</span>
                <span class="vr"></span>
                <span class="text-white-50 fw-light text-capitalize">${loggedUserDetails.roleName}</span>
            </h2>
            <div class="border-top border-secondary mt-3"></div>
        </div>
        <div class="flex-grow-1">
            <ul class="list-unstyled">
                <li class="mb-1">
                    <a href="${pageContext.request.contextPath}/${requestPath}/dashboard"
                       class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                                   rounded border-0 w-100 text-start lh-sm">
                        Panel główny
                        <i class="d-inline-flex align-self-center bi bi-arrow-return-right"></i>
                    </a>
                </li>
                <jsp:doBody/>
                <li class="mb-1">
                    <button data-bs-toggle="collapse" data-bs-target="#account-collapse" aria-expanded="false"
                            class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                            rounded border-0 collapsed w-100 text-start lh-sm">
                        Moje konto
                        <i class="d-inline-flex align-self-center bi bi-chevron-right chewron-icon simple-transition"></i>
                    </button>
                    <ul class="collapse ms-4 mt-1 btn-toggle-nav list-unstyled fw-normal pb-1 small"
                        id="account-collapse">
                        <li class="mb-1">
                            <a href="${pageContext.request.contextPath}/${requestPath}/profile"
                               class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                           text-decoration-none rounded btn-hover-dark w-100">
                                <i class="bi bi-person me-2"></i>Profil
                            </a>
                        </li>
                        <li class="mb-1">
                            <a href="${pageContext.request.contextPath}/${requestPath}/settings"
                               class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                            text-decoration-none rounded btn-hover-dark w-100">
                                <i class="bi bi-gear me-2"></i>Ustawienia
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
        <div class="mt-3">
            <div class="border-top border-secondary mb-3"></div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-light w-100">
                <i class="bi bi-box-arrow-left me-2"></i>Wyloguj
            </a>
        </div>
    </div>
</nav>