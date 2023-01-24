<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<header class="d-flex navbar bg-white sticky-top bg-white">
    <button id="nav-toggle-button" class="btn ms-2 p-1 px-2 d-flex align-items-center d-lg-block d-none">
        <span data-bs-toggle="tooltip" data-bs-placement="right" data-bs-title="Pokaż/schowaj menu" type="button">
            <i class="bi bi-list text-dark fs-4 lh-sm"></i>
        </span>
    </button>
    <button class="btn ms-2 p-1 px-2 d-flex align-items-center d-lg-none d-block" type="button" data-bs-toggle="offcanvas"
            data-bs-target="#nav-mobile">
        <span data-bs-toggle="tooltip" data-bs-placement="right" data-bs-title="Pokaż/schowaj menu" type="button">
            <i class="bi bi-list text-dark fs-4 lh-sm"></i>
        </span>
    </button>
    <div class="dropdown dropdown-menu-end">
        <button class="d-flex align-items-center border-0 text-dark bg-transparent dropdown-toggle me-4"
                data-bs-toggle="dropdown" aria-expanded="false">
            <img src="${pageContext.request.contextPath}/${loggedUserDetails.imageUrl}" alt=""
                width="32" height="32" class="rounded-circle me-2">
            <span class="mx-1 d-none d-sm-block">${loggedUserDetails.fullName}</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end text-small">
            <li class="px-3 d-block d-sm-none">${loggedUserDetails.fullName}</li>
            <li><hr class="d-block d-sm-none dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/${param.requestPath}/profile">
                <span class="material-symbols-outlined fs-6 me-2">person</span>Profil
            </a></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/${param.requestPath}/settings">
                <span class="material-symbols-outlined fs-6 me-2">settings</span>Ustawienia
            </a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                <span class="material-symbols-outlined fs-6 me-2">logout</span>Wyloguj
            </a></li>
        </ul>
    </div>
</header>
