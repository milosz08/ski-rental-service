<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="pagesData" class="pl.polsl.skirentalservice.paging.pagination.ServletPagination" scope="request"/>
<jsp:useBean id="cartEqAdd" class="pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto" scope="request"/>
<jsp:useBean id="addModalResDto" class="pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto" scope="request"/>

<div class="modal fade eq-modals" id="addEquipment${cartEqAdd.id}" tabindex="-1" aria-hidden="true"
    data-equipment-add-cart-modal-enable-${cartEqAdd.id}="${addModalResDto.eqId.equals(cartEqAdd.id.toString()) ? addModalResDto.immediatelyShow : ''}"
    data-eqid="${cartEqAdd.id}" data-bs-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/seller/add-equipment-to-cart" method="post" class="px-2" novalidate>
                <div class="modal-header">
                    <h1 class="modal-title fs-5">Dodaj sprzęt do wypożyczenia</h1>
                </div>
                <div class="modal-body px-0 pb-0">
                    <c:if test="${addModalResDto.alert.active}">
                        <div class="alert mx-3 ${addModalResDto.alert.type.cssClass} alert-dismissible mb-3 fade show lh-sm" role="alert">
                            ${addModalResDto.alert.message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <input type="hidden" name="equipmentId" value="${cartEqAdd.id}">
                    <input type="hidden" name="redirPag" value="?page=${pagesData.page}&total=${pagesData.totalPerPage}">
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="count" class="form-label mb-1 text-secondary micro-font">
                                    Wpisz liczbę sztuk:
                                </label>
                                <input type="number"
                                    class="form-control form-control-sm ${addModalResDto.count.errorStyle}"
                                    id="count" value="${addModalResDto.count.value}" placeholder="np. 1"
                                    name="count" maxlength="${cartEqAdd.totalCount.toString().length()}" min="1"
                                    max="${cartEqAdd.totalCount}">
                                <div class="invalid-feedback lh-sm">${addModalResDto.count.message}</div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="depositPrice" class="form-label mb-1 text-secondary micro-font">
                                    Kaucja netto (opcjonalnie):
                                </label>
                                <div class="input-group input-group-sm has-validation">
                                    <input class="form-control form-control-sm ${addModalResDto.depositPrice.errorStyle}"
                                        id="depositPrice" value="${addModalResDto.depositPrice.value}"
                                        placeholder="np. 25,00" name="depositPrice" type="text" maxlength="7">
                                    <span class="input-group-text">zł</span>
                                    <div class="invalid-feedback lh-sm">${addModalResDto.depositPrice.message}</div>
                                </div>
                            </div>
                            <div class="col-md-12 mb-3">
                                <label for="description" class="form-label mb-1 text-secondary micro-font">
                                    Uwagi (opcjonalne):
                                </label>
                                <textarea class="form-control form-control-sm ${addModalResDto.description.errorStyle}"
                                    rows="4" id="description" name="description" maxlength="200"
                                    placeholder="Wprowadź tutaj dodatkowe uwagi na temat np. kaucji. Maksymalnie 200 znaków."
                                >${addModalResDto.description.value}</textarea>
                                <div class="invalid-feedback lh-sm">${addModalResDto.description.message}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-sm btn-outline-success">Dodaj sprzęt</button>
                    <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
                        Zamknij
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
