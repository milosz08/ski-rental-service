<%@ tag description="Owner panel wrapper template" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<p:generic-page.wrapper>
    <div class="d-flex flex-grow-1 h-100">
        <p:generic-left-nav.wrapper requestPath="owner">
            <li class="mb-1">
                <button data-bs-toggle="collapse" data-bs-target="#employers-collapse" aria-expanded="false"
                    class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                        rounded border-0 collapsed w-100 text-start lh-sm">
                    Pracownicy
                    <i class="d-inline-flex align-self-center bi bi-chevron-right chewron-icon simple-transition"></i>
                </button>
                <ul class="collapse ms-3 mt-1 btn-toggle-nav list-unstyled fw-normal pb-1 small" id="employers-collapse">
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/owner/employers"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">badge</span>Lista pracowników
                        </a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/owner/add-employer"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">add</span>Dodaj pracownika
                        </a>
                    </li>
                </ul>
            </li>
        </p:generic-left-nav.wrapper>
        <div id="main-wrapper" class="d-flex flex-column flex-fill main-wrapper main-wrapper-active simple-transition">
            <jsp:include page="/WEB-INF/partials/logged-user-top-bar.partial.jsp">
                <jsp:param name="requestPath" value="owner"/>
            </jsp:include>
            <main class="p-3 p-sm-4 flex-grow-1">
                <jsp:doBody/>
            </main>
            <jsp:include page="/WEB-INF/partials/logged-user-footer.partial.jsp"/>
        </div>
    </div>
</p:generic-page.wrapper>
