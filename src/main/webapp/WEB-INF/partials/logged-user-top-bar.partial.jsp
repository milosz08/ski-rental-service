<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDetailsDto" scope="session"/>

<header id="top-bar" class="d-flex navbar fixed-top bg-white top-bar simple-transition">
    <button id="nav-toggler" class="btn ms-3 p-1 d-flex align-items-center">
        <span data-bs-toggle="tooltip" data-bs-placement="right" data-bs-title="Pokaż/schowaj menu" type="button">
            <i class="bi bi-list text-dark fs-4 lh-1"></i>
        </span>
    </button>
    <div class="dropdown">
        <button class="d-flex align-items-center border-0 text-dark bg-transparent dropdown-toggle me-4"
                data-bs-toggle="dropdown" aria-expanded="false">
            <img src="${pageContext.request.contextPath}/${loggedUserDetails.imageUrlPath}" alt=""
                 width="32" height="32" class="rounded-circle me-2">
            <span class="mx-1 d-none d-sm-block">${loggedUserDetails.fullName}</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end text-small">
            <li class="px-3 d-block d-sm-none">${loggedUserDetails.fullName}</li>
            <li><hr class="d-block d-sm-none dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/${param.requestPath}/profile">
                <i class="bi bi-person me-2"></i>Profil
            </a></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/${param.requestPath}/settings">
                <i class="bi bi-gear me-2"></i>Ustawienia
            </a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-left me-2"></i>Wyloguj
            </a></li>
        </ul>
    </div>
</header>