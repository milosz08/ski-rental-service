<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:useBean id="pagesData" type="pl.polsl.skirentalservice.paging.pagination.ServletPagination" scope="request"/>
<jsp:useBean id="cartEq" type="pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto" scope="request"/>

<div class="modal fade eq-modals" id="editEquipment${cartEq.id}" tabindex="-1" aria-hidden="true"
     data-bs-backdrop="static"
     data-equipment-edit-cart-modal-enable-${cartEq.id}="${cartEq.resDto.immediatelyShow}" data-eqid="${cartEq.id}">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/seller/edit-equipment-from-cart" method="post" class="px-2"
            novalidate>
        <div class="modal-header">
          <h1 class="modal-title fs-5">Edytuj sprzęt obecny w zestawieniu</h1>
        </div>
        <div class="modal-body px-0 pb-0">
          <c:if test="${cartEq.resDto.alert.active}">
            <div class="alert mx-3 ${cartEq.resDto.alert.type.cssClass} alert-dismissible mb-3 fade show lh-sm"
                 role="alert">
                ${cartEq.resDto.alert.message}
              <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
          </c:if>
          <input type="hidden" name="equipmentId" value="${cartEq.id}">
          <input type="hidden" name="redirPag" value="?page=${pagesData.page}&total=${pagesData.totalPerPage}">
          <div class="container-fluid">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="count" class="form-label mb-1 text-secondary micro-font">
                  Wpisz liczbę sztuk:
                </label>
                <input type="number"
                       class="form-control form-control-sm ${cartEq.resDto.count.errorStyle}"
                       id="count" value="${cartEq.resDto.count.value}" placeholder="np. 1"
                       name="count" maxlength="${cartEq.totalPersistCount.length()}" min="1"
                       max="${cartEq.totalPersistCount}">
                <div class="invalid-feedback lh-sm">${cartEq.resDto.count.message}</div>
              </div>
              <div class="col-md-6 mb-3">
                <label for="depositPrice" class="form-label mb-1 text-secondary micro-font">
                  Kaucja netto (opcjonalnie):
                </label>
                <div class="input-group input-group-sm has-validation">
                  <input class="form-control form-control-sm ${cartEq.resDto.depositPrice.errorStyle}"
                         id="depositPrice" value="${cartEq.resDto.depositPrice.value}"
                         placeholder="np. 25,00" name="depositPrice" type="text" maxlength="7">
                  <span class="input-group-text">zł</span>
                  <div class="invalid-feedback lh-sm">${cartEq.resDto.depositPrice.message}</div>
                </div>
              </div>
              <div class="col-md-12 mb-3">
                <label for="description" class="form-label mb-1 text-secondary micro-font">
                  Uwagi (opcjonalne):
                </label>
                <textarea class="form-control form-control-sm ${cartEq.resDto.description.errorStyle}"
                          rows="4" id="description" name="description" maxlength="200"
                          placeholder="Wprowadź tutaj dodatkowe uwagi na temat np. kaucji. Maksymalnie 200 znaków."
                >${cartEq.resDto.description.value}</textarea>
                <div class="invalid-feedback lh-sm">${cartEq.resDto.description.message}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-sm btn-outline-secondary">Edytuj sprzęt</button>
          <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
            Zamknij
          </button>
        </div>
      </form>
    </div>
  </div>
</div>
