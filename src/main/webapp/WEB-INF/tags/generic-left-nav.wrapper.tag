<%@ tag description="Logged user left navigation template" pageEncoding="UTF-8" language="java" %>
<%@ tag trimDirectiveWhitespaces="true"%>
<%@ attribute name="requestPath" required="true" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<div class="offcanvas offcanvas-start text-bg-dark d-lg-none d-block mobile-left-nav-wrapper" id="nav-mobile">
    <div class="offcanvas-body p-3 d-flex flex-column min-vh-100">
        <div class="mb-3">
            <div class="hstack justify-content-between align-items-center mb-3">
                <h2 class="hstack gap-2 mb-0 fs-5 justify-content-center text-light fw-normal">
                    <span>SkiRental</span>
                    <span class="vr"></span>
                    <span class="text-white-50 fw-light text-capitalize">${loggedUserDetails.roleName}</span>
                </h2>
                <button type="button" class="bg-transparent border-0" data-bs-dismiss="offcanvas">
                    <i class="bi bi-x-lg text-white-50"></i>
                </button>
            </div>
            <div class="border-top border-secondary mt-3"></div>
        </div>
        <ul class="flex-grow-1 list-unstyled">
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
                <ul class="collapse ms-3 mt-1 btn-toggle-nav list-unstyled fw-normal pb-1 small"
                    id="account-collapse-mobile">
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/${requestPath}/profile"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                            text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">person</span>Profil
                        </a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/${requestPath}/settings"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">settings</span>Ustawienia
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
        <div class="mt-3">
            <div class="border-top border-secondary mb-3"></div>
            <a href="${pageContext.request.contextPath}/logout"
                class="btn btn-sm btn-light w-100 d-flex justify-content-center align-items-center">
                <span class="material-symbols-outlined fs-6 me-2">logout</span>Wyloguj
            </a>
        </div>
    </div>
</div>
<nav class="bg-dark text-light left-nav-wrapper d-lg-block d-none left-nav-wrapper-active simple-transition flex-shrink-0"
    id="left-nav-wrapper">
    <div class="sticky-top p-3 d-flex flex-column min-vh-100">
        <div class="mb-3">
            <h2 class="hstack gap-2 fs-5 mb-3 justify-content-center text-light fw-normal">
                <span>SkiRental</span>
                <span class="vr"></span>
                <span class="text-white-50 fw-light text-capitalize">${loggedUserDetails.roleName}</span>
            </h2>
            <div class="border-top border-secondary mt-3"></div>
        </div>
        <ul class="flex-grow-1 list-unstyled">
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
                <ul class="collapse ms-3 mt-1 btn-toggle-nav list-unstyled fw-normal pb-1 small"
                    id="account-collapse">
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/${requestPath}/profile"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                            text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">person</span>Profil
                        </a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/${requestPath}/settings"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">settings</span>Ustawienia
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
        <div class="mt-3">
            <div class="border-top border-secondary mb-3"></div>
            <a href="${pageContext.request.contextPath}/logout"
                class="btn btn-sm btn-light w-100 d-flex justify-content-center align-items-center">
                <span class="material-symbols-outlined fs-6 me-2">logout</span>Wyloguj
            </a>
        </div>
    </div>
</nav>
