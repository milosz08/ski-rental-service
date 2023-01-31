<%@ tag description="Seller panel wrapper template" pageEncoding="UTF-8" language="java" %>
<%@ tag trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="loggedUserDetails" type="pl.polsl.skirentalservice.dto.login.LoggedUserDataDto" scope="session"/>

<p:generic-page.wrapper>
    <div class="d-flex flex-grow-1 h-100">
        <p:generic-left-nav.wrapper requestPath="seller">
            <c:if test="${sessionScope.get('inmemoryNewRentData') != null}">
                <jsp:useBean id="inmemoryNewRentData" type="pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto" scope="session"/>
                <li class="mb-1">
                    <a href="${pageContext.request.contextPath}/seller/create-new-rent?id=${inmemoryNewRentData.customerId}"
                        class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                        rounded border-0 w-100 text-start lh-sm rent-saved-data-opacity">
                        Wypożyczenie robocze
                        <i class="d-inline-flex align-self-center bi bi-arrow-bar-right"></i>
                    </a>
                </li>
                <div class="border-top border-secondary my-3"></div>
            </c:if>
            <li class="mb-1">
                <a href="${pageContext.request.contextPath}/seller/equipments"
                    class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                        rounded border-0 w-100 text-start lh-sm">
                    Sprzęty narciarskie
                    <i class="d-inline-flex align-self-center bi bi-arrow-return-right"></i>
                </a>
            </li>
            <li class="mb-1">
                <button data-bs-toggle="collapse" data-bs-target="#customers-collapse" aria-expanded="false"
                        class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                        rounded border-0 collapsed w-100 text-start lh-sm">
                    Klienci
                    <i class="d-inline-flex align-self-center bi bi-chevron-right chewron-icon simple-transition"></i>
                </button>
                <ul class="collapse ms-3 mt-1 btn-toggle-nav list-unstyled fw-normal pb-1 small" id="customers-collapse">
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/seller/customers"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">group</span>Lista klientów
                        </a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/seller/add-customer"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">add</span>Dodaj klienta
                        </a>
                    </li>
                </ul>
            </li>
            <li class="mb-1">
                <button data-bs-toggle="collapse" data-bs-target="#rents-collapse" aria-expanded="false"
                        class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                        rounded border-0 collapsed w-100 text-start lh-sm">
                    Wypożyczenia
                    <i class="d-inline-flex align-self-center bi bi-chevron-right chewron-icon simple-transition"></i>
                </button>
                <ul class="collapse ms-3 mt-1 btn-toggle-nav list-unstyled fw-normal pb-1 small" id="rents-collapse">
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/seller/rents"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">shopping_cart</span>
                            Lista wypożyczeń
                        </a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/seller/returns"
                            class="text-white-50 d-inline-flex align-items-center py-1 ps-2
                                text-decoration-none rounded btn-hover-dark w-100 fw-light">
                            <span class="material-symbols-outlined fs-6 me-2">shopping_cart_checkout</span>
                            Lista zwrotów
                        </a>
                    </li>
                </ul>
            </li>
            <li class="mb-1">
                <a href="${pageContext.request.contextPath}/seller/profile"
                    class="btn d-flex justify-content-between text-white-50 fw-light btn-hover-dark
                        rounded border-0 w-100 text-start lh-sm">
                    Moje konto
                    <i class="d-inline-flex align-self-center bi bi-arrow-return-right"></i>
                </a>
            </li>
        </p:generic-left-nav.wrapper>
        <div id="main-wrapper" class="d-flex flex-column flex-fill main-wrapper main-wrapper-active simple-transition">
            <jsp:include page="/WEB-INF/partials/logged-user-top-bar.partial.jsp">
                <jsp:param name="requestPath" value="seller"/>
            </jsp:include>
            <main class="p-3 p-sm-4 flex-grow-1">
                <jsp:doBody/>
            </main>
            <jsp:include page="/WEB-INF/partials/logged-user-footer.partial.jsp"/>
        </div>
    </div>
</p:generic-page.wrapper>
