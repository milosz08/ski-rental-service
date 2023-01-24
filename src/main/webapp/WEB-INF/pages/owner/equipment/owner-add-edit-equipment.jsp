<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="addEditText" class="java.lang.String" scope="request"/>
<jsp:useBean id="alertData" class="pl.polsl.skirentalservice.dto.AlertTupleDto" scope="request"/>
<jsp:useBean id="addEditEquipmentData" class="pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto" scope="request"/>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">${addEditText} sprzęt narciarski</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/dashboard">Panel główny</a>
            </li>
            <li class="breadcrumb-item">
                <a class="link-dark" href="${pageContext.request.contextPath}/owner/equipments">Lista sprzętów narciarskich</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">${addEditText} sprzęt narciarski</li>
        </ol>
    </nav>
    <hr/>
    <c:if test="${alertData.active}">
        <div class="alert ${alertData.type.cssClass} alert-dismissible mb-3 fade show" role="alert">
                ${alertData.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <form action="" method="post">
        <div class="container-fluid px-0">
            <div class="row">
                <div class="col-md-6">
                    <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
                        <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
                            Podstawowe parametry:
                        </legend>
                        <div class="row mt-0">
                            <div class="col-xl-6 mb-2">
                                <label for="name" class="form-label mb-1 text-secondary micro-font">Nazwa sprzętu:</label>
                                <input type="text" class="form-control form-control-sm ${addEditEquipmentData.name.errorStyle}"
                                    id="name" value="${addEditEquipmentData.name.value}" placeholder="np. Narty VOLKL FLAIR 76 ELITE"
                                    name="name" maxlength="50">
                                <div class="invalid-feedback lh-sm">${addEditEquipmentData.name.message}</div>
                            </div>
                            <div class="col-xl-6 mb-2">
                                <label for="type" class="form-label mb-1 text-secondary micro-font">Typ sprzętu:</label>
                                <div class="input-group input-group-sm has-validation">
                                    <select class="form-select form-select-sm rounded-1 ${addEditEquipmentData.types.errorStyle}"
                                            name="type" id="type">
                                        <c:forEach items="${addEditEquipmentData.types.selects}" var="type">
                                            <option value="${type.value}" ${type.isSelected}>${type.text}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="button" data-bs-toggle="modal" data-bs-target="#equipmentTypeModal"
                                            class="btn btn-sm btn-dark ms-2 rounded-1">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                    <div class="invalid-feedback lh-sm">${addEditEquipmentData.types.message}</div>
                                </div>
                            </div>
                            <div class="col-xl-6 mb-2">
                                <label for="brand" class="form-label mb-1 text-secondary micro-font">Marka sprzętu:</label>
                                <div class="input-group input-group-sm has-validation">
                                    <select class="form-select form-select-sm rounded-1 ${addEditEquipmentData.brands.errorStyle}"
                                            name="brand" id="brand" >
                                        <c:forEach items="${addEditEquipmentData.brands.selects}" var="brand">
                                            <option value="${brand.value}" ${brand.isSelected}>${brand.text}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="button" class="btn btn-sm btn-dark ms-2 rounded-1">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                    <div class="invalid-feedback lh-sm">${addEditEquipmentData.brands.message}</div>
                                </div>
                            </div>
                            <div class="col-xl-6 mb-2">
                                <label for="gender" class="form-label mb-1 text-secondary micro-font">Przeznaczone dla:</label>
                                <select id="gender" class="form-select form-select-sm flex-grow-1" name="gender">
                                    <c:forEach items="${addEditEquipmentData.genders}" var="gender">
                                        <option value="${gender.value}" ${gender.isSelected}>${gender.text}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-xl-12 mb-3">
                                <label for="description" class="form-label mb-1 text-secondary micro-font">
                                    Dodatkowy opis (opcjonalny):
                                </label>
                                <textarea class="form-control form-control-sm ${addEditEquipmentData.description.errorStyle}"
                                    rows="4" id="description" name="description"
                                    placeholder="Wprowadź tutaj dodatkowy opis sprzętu. Maksymalnie 200 znaków."
                                >${addEditEquipmentData.description.value}</textarea>
                                <div class="invalid-feedback lh-sm">${addEditEquipmentData.description.message}</div>
                            </div>
                        </div>
                    </fieldset>
                </div>
                <div class="col-md-6">
                    <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
                        <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
                            Dodatkowe atrybuty:
                        </legend>
                        <div class="row mt-0">
                            <div class="col-lg-12 col-xl-3 mb-2">
                                <label for="total" class="form-label mb-1 text-secondary micro-font">Ilość na stanie:</label>
                                <input type="number" class="form-control form-control-sm ${addEditEquipmentData.total.errorStyle}"
                                    id="total" value="${addEditEquipmentData.total.value}" placeholder="np. 15" name="total"
                                    max="9999" min="1">
                                <div class="invalid-feedback lh-sm">${addEditEquipmentData.total.message}</div>
                            </div>
                            <div class="col-lg-12 col-xl mb-3">
                                <label for="color" class="form-label mb-1 text-secondary micro-font">Kolor dominujący:</label>
                                <div class="input-group input-group-sm has-validation">
                                    <select class="form-select form-select-sm rounded-1 ${addEditEquipmentData.colors.errorStyle}"
                                            id="color" name="color">
                                        <c:forEach items="${addEditEquipmentData.colors.selects}" var="color">
                                            <option value="${color.value}" ${color.isSelected}>${color.text}</option>
                                        </c:forEach>
                                    </select>
                                    <button class="btn btn-sm btn-dark ms-2 rounded-1">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                    <div class="invalid-feedback lh-sm">${addEditEquipmentData.colors.message}</div>
                                </div>
                            </div>
                            <div class="col-lg-12 col-xl mb-3">
                                <label for="size" class="form-label mb-1 text-secondary micro-font">
                                    Rozmiar (opcjonalny):
                                </label>
                                <div class="input-group input-group-sm has-validation">
                                    <input class="form-control form-control-sm ${addEditEquipmentData.size.errorStyle}" name="size"
                                        type="text" placeholder="np. 32,00" id="size" value="${addEditEquipmentData.size.value}">
                                    <span class="input-group-text">cm</span>
                                    <div class="invalid-feedback lh-sm">${addEditEquipmentData.size.message}</div>
                                </div>
                            </div>
                        </div>
                    </fieldset>

                    <%-- TODO: ta sekcjo do dorobienia --%>

                    <fieldset class="border rounded-1 py-2 pb-0 px-3 pt-1 mb-3">
                        <legend class="float-none w-auto px-2 fs-6 text-secondary bg-light fw-light mb-0">
                            Cena za sprzęt:
                        </legend>
                        <div class="row mt-0">
                            <div class="col-xl-6 mb-3">
                                <label for="price" class="form-label mb-1 text-secondary micro-font">Cena netto w jednostce:</label>
                                <div class="input-group input-group-sm has-validation">
                                    <input type="text" class="form-control form-control-sm ${addEditEmployerData.firstName.errorStyle}"
                                        id="price" value="${addEditEmployerData.firstName.value}" placeholder="np. 25,00" name="price">
                                    <div class="invalid-feedback">${addEditEmployerData.firstName.message}</div>
                                    <span class="input-group-text">zł</span>
                                </div>
                            </div>
                            <div class="col-xl-6 mb-3">
                                <label for="priceUnit" class="form-label mb-1 text-secondary micro-font">Jednostka rozliczeniowa:</label>
                                <div class="hstack gap-2">
                                    <select id="priceUnit" class="form-select form-select-sm flex-grow-1" name="color">
                                        <option value="ski">Doba</option>
                                        <option value="ski">Godzina</option>
                                    </select>
                                    <button class="btn btn-sm btn-dark">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="col-12">
                                <button class="btn btn-sm btn-dark mb-3 w-100">
                                    Dodaj nową cenę netto w jednostce rozliczeniowej
                                </button>
                            </div>
                        </div>
                    </fieldset>

                    <%-- TODO: ta sekcjo do dorobienia --%>

                </div>
            </div>
        </div>
        <hr/>
        <div class="hstack gap-3 justify-content-end">
            <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="modal" data-bs-target="#rejectChanges">
                <i class="bi bi-arrow-return-left me-1 lh-sm"></i>Odrzuć zmiany
            </button>
            <button type="submit" class="btn btn-sm btn-dark">${addEditText} sprzęt narciarski</button>
        </div>
    </form>
    <jsp:include page="/WEB-INF/partials/reject-changes.partial.jsp">
        <jsp:param name="redirectPath" value="/owner/equipments"/>
    </jsp:include>
    <jsp:include page="/WEB-INF/partials/owner-equipment/type-modal.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/owner-equipment/brand-modal.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/owner-equipment/account-unit-modal.partial.jsp"/>
    <jsp:include page="/WEB-INF/partials/owner-equipment/color-modal.partial.jsp"/>
</p:generic-owner.wrapper>
