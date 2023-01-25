<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>

<jsp:useBean id="addEditEquipmentData" class="pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto" scope="request"/>
<jsp:useBean id="equipmentBrandsModalData" class="pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto" scope="request"/>

<div class="modal fade" id="equipmentBrandModal" data-bs-backdrop="static" tabindex="-1" aria-hidden="true"
    data-equipment-brand-modal-enable="${equipmentBrandsModalData.immediatelyShow}">
    <div class="modal-dialog modal-medium-size">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5">Marki sprzętu narciarskiego</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body px-0 pb-0">
                <c:if test="${equipmentBrandsModalData.alert.active}">
                    <div class="alert mx-3 ${equipmentBrandsModalData.alert.type.cssClass} alert-dismissible mb-3 fade show lh-sm" role="alert">
                            ${equipmentBrandsModalData.alert.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <ul class="nav nav-tabs px-3" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link ${equipmentBrandsModalData.activeFirstPage.button}" data-bs-toggle="tab"
                                data-bs-target="#addNewBrandCard" type="button" role="tab">
                            Dodaj nową markę
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link ${equipmentBrandsModalData.activeSecondPage.button}" data-bs-toggle="tab"
                                data-bs-target="#showAllBrandsCard" type="button" role="tab">
                            Wszystkie marki
                        </button>
                    </li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane fade ${equipmentBrandsModalData.activeFirstPage.canvas} p-3" id="addNewBrandCard"
                        role="tabpanel" tabindex="0">
                        <form action="${pageContext.request.contextPath}/owner/add-equipment-brand" method="post" novalidate>
                            <label for="equipmentBrand" class="form-label mb-1 text-secondary micro-font">
                                Nazwa marki sprzętu:
                            </label>
                            <div class="input-group has-validation input-group-sm">
                                <input type="text" id="equipmentBrand" name="name" placeholder="np. Volkl"
                                    class="form-control form-control-sm ${equipmentBrandsModalData.name.errorStyle}"
                                    value="${equipmentBrandsModalData.name.value}" minlength="5" maxlength="50">
                                <button type="submit" class="btn btn-dark btn-sm">Dodaj</button>
                                <div class="invalid-feedback">${equipmentBrandsModalData.name.message}</div>
                            </div>
                        </form>
                    </div>
                    <div class="tab-pane fade ${equipmentBrandsModalData.activeSecondPage.canvas} p-3" id="showAllBrandsCard"
                        role="tabpanel" tabindex="0">
                        <table class="table table-sm table-hover table-bordered mb-0">
                            <thead>
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col">Nazwa marki sprzętu</th>
                                <th scope="col" class="fit">Akcja</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${addEditEquipmentData.brands.selects}" var="brand">
                                <c:if test="${not brand.value.equals('none')}">
                                    <tr>
                                        <th class="align-middle">${brand.value}</th>
                                        <td class="align-middle">${brand.text}</td>
                                        <td class="align-middle">
                                            <a href="${pageContext.request.contextPath}/owner/delete-equipment-brand?id=${brand.value}"
                                                class="btn btn-danger btn-sm py-0">
                                                Usuń
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
