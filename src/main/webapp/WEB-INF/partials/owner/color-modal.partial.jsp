<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="equipmentId" class="java.lang.String" scope="request"/>
<jsp:useBean id="addEditText" class="java.lang.String" scope="request"/>
<jsp:useBean id="addEditEquipmentData" class="pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto"
             scope="request"/>
<jsp:useBean id="equipmentColorsModalData" class="pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto"
             scope="request"/>

<div class="modal fade" id="equipmentColorModal" data-bs-backdrop="static" tabindex="-1" aria-hidden="true"
     data-equipment-color-modal-enable="${equipmentColorsModalData.immediatelyShow}">
  <div class="modal-dialog modal-medium-size">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5">Kolory sprzętu narciarskiego</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body px-0 pb-0">
        <c:if test="${equipmentColorsModalData.alert.active}">
          <div class="alert mx-3 ${equipmentColorsModalData.alert.type.cssClass} alert-dismissible mb-3 fade show lh-sm"
               role="alert">
              ${equipmentColorsModalData.alert.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
          </div>
        </c:if>
        <ul class="nav nav-tabs px-3" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link ${equipmentColorsModalData.activeFirstPage.button}" data-bs-toggle="tab"
                    data-bs-target="#addNewColorCard" type="button" role="tab">
              Dodaj nowy kolor
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link ${equipmentColorsModalData.activeSecondPage.button}" data-bs-toggle="tab"
                    data-bs-target="#showAllColorsCard" type="button" role="tab">
              Wszystkie kolory
            </button>
          </li>
        </ul>
        <div class="tab-content">
          <div class="tab-pane fade ${equipmentColorsModalData.activeFirstPage.canvas} p-3" id="addNewColorCard"
               role="tabpanel" tabindex="0">
            <form action="${pageContext.request.contextPath}/owner/add-equipment-color" method="post" novalidate>
              <c:if test="${addEditText.equals('Edytuj')}">
                <input type="hidden" name="redirect" value="/owner/edit-equipment?id=${equipmentId}"/>
              </c:if>
              <c:if test="${not addEditText.equals('Edytuj')}">
                <input type="hidden" name="redirect" value="/owner/add-equipment"/>
              </c:if>
              <label for="equipmentColor" class="form-label mb-1 text-secondary micro-font">
                Nazwa koloru sprzętu:
              </label>
              <div class="input-group has-validation input-group-sm">
                <input type="text" id="equipmentColor" name="name" placeholder="np. czerwony"
                       class="form-control form-control-sm ${equipmentColorsModalData.name.errorStyle}"
                       value="${equipmentColorsModalData.name.value}" minlength="5" maxlength="50">
                <button type="submit" class="btn btn-dark btn-sm">Dodaj</button>
                <div class="invalid-feedback">${equipmentColorsModalData.name.message}</div>
              </div>
            </form>
          </div>
          <div class="tab-pane fade ${equipmentColorsModalData.activeSecondPage.canvas} p-3" id="showAllColorsCard"
               role="tabpanel" tabindex="0">
            <table class="table table-sm table-hover table-bordered mb-0">
              <thead>
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Nazwa koloru sprzętu</th>
                <th scope="col" class="fit">Akcja</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${addEditEquipmentData.colors.selects}" var="color">
                <c:if test="${not color.value.equals('none')}">
                  <tr>
                    <th class="align-middle">${color.value}</th>
                    <td class="align-middle">${color.text}</td>
                    <td class="align-middle">
                      <c:if test="${addEditText.equals('Edytuj')}">
                        <a href="${pageContext.request.contextPath}
                                                    /owner/delete-equipment-color?id=${color.value}&redirect=/owner/edit-equipment?id=${equipmentId}"
                           class="btn btn-danger btn-sm py-0">
                          Usuń
                        </a>
                      </c:if>
                      <c:if test="${not addEditText.equals('Edytuj')}">
                        <a href="${pageContext.request.contextPath}
                                                    /owner/delete-equipment-color?id=${color.value}&redirect=/owner/add-equipment"
                           class="btn btn-danger btn-sm py-0">
                          Usuń
                        </a>
                      </c:if>
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
