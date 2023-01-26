<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>

<jsp:useBean id="addEditEquipmentData" class="pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto" scope="request"/>
<jsp:useBean id="equipmentTypeModalData" class="pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto" scope="request"/>

<div class="modal fade" id="equipmentTypeModal" data-bs-backdrop="static" tabindex="-1" aria-hidden="true"
    data-equipment-type-modal-enable="${equipmentTypeModalData.immediatelyShow}">
    <div class="modal-dialog modal-medium-size">
        <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5">Typ sprzętu narciarskiego</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body px-0 pb-0">
                <c:if test="${equipmentTypeModalData.alert.active}">
                    <div class="alert mx-3 ${equipmentTypeModalData.alert.type.cssClass} alert-dismissible mb-3 fade show lh-sm" role="alert">
                        ${equipmentTypeModalData.alert.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <ul class="nav nav-tabs px-3" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link ${equipmentTypeModalData.activeFirstPage.button}" data-bs-toggle="tab"
                            data-bs-target="#addNewTypeCard" type="button" role="tab">
                            Dodaj nowy typ
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link ${equipmentTypeModalData.activeSecondPage.button}" data-bs-toggle="tab"
                            data-bs-target="#showAllTypesCard" type="button" role="tab">
                            Wszystkie typy
                        </button>
                    </li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane fade ${equipmentTypeModalData.activeFirstPage.canvas} p-3" id="addNewTypeCard"
                        role="tabpanel" tabindex="0">
                        <form action="${pageContext.request.contextPath}/owner/add-equipment-type" method="post" novalidate>
                            <label for="equipmentType" class="form-label mb-1 text-secondary micro-font">
                                Nazwa typu sprzętu:
                            </label>
                            <div class="input-group has-validation input-group-sm">
                                <input type="text" id="equipmentType" name="name" placeholder="np. Narty"
                                    class="form-control form-control-sm ${equipmentTypeModalData.name.errorStyle}"
                                    value="${equipmentTypeModalData.name.value}" minlength="5" maxlength="50">
                                <button type="submit" class="btn btn-dark btn-sm">Dodaj</button>
                                <div class="invalid-feedback">${equipmentTypeModalData.name.message}</div>
                            </div>
                        </form>
                    </div>
                    <div class="tab-pane fade ${equipmentTypeModalData.activeSecondPage.canvas} p-3" id="showAllTypesCard"
                        role="tabpanel" tabindex="0">
                        <table class="table table-sm table-hover table-bordered mb-0">
                            <thead>
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col">Nazwa typu sprzętu</th>
                                <th scope="col" class="fit">Akcja</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${addEditEquipmentData.types.selects}" var="type">
                                <c:if test="${not type.value.equals('none')}">
                                    <tr>
                                        <th class="align-middle">${type.value}</th>
                                        <td class="align-middle">${type.text}</td>
                                        <td class="align-middle">
                                            <a href="${pageContext.request.contextPath}/owner/delete-equipment-type?id=${type.value}"
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