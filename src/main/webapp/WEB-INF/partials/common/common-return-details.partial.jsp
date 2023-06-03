<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:useBean id="totalSum" class="java.lang.Integer" scope="request"/>
<jsp:useBean id="returnDetailsData" type="pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto" scope="request"/>
<jsp:useBean id="equipmentsReturnDetailsData" type="java.util.List<pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto>" scope="request"/>

<div class="container-fluid">
    <div class="row">
        <div class="table-responsive col-lg-12 col-xl-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Numer zwrotu:</td>
                    <td>${returnDetailsData.issuedIdentifier()}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Data utworzenia zwrotu:</td>
                    <td><p:datetime-formatter datetime="${returnDetailsData.issuedDateTime()}"/></td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="table-responsive col-lg-12 col-xl-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Podatek:</td>
                    <td>${returnDetailsData.tax()}%</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<h3 class="fs-5 mt-4 mb-0">Szczegóły klienta</h3>
<hr class="mt-2"/>
<c:if test="${empty returnDetailsData.fullName()}">
    <p class="mb-4"><i>klient usunięty</i></p>
</c:if>
<c:if test="${not empty returnDetailsData.fullName()}">
    <div class="container-fluid">
        <div class="row">
            <div class="table-responsive col-lg-12 col-xl-6">
                <table class="table table-fixed-width">
                    <tbody>
                    <tr>
                        <td class="fw-bold">Imię i nazwisko:</td>
                        <td>${returnDetailsData.fullName()}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Data urodzenia:</td>
                        <td><p:date-formatter date="${returnDetailsData.bornDate()}"/></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Wiek klienta:</td>
                        <td>${returnDetailsData.age()} lat(a)</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Adres email:</td>
                        <td><a href="mailto:${returnDetailsData.emailAddress()}">${returnDetailsData.emailAddress()}</a></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Adres zamieszkania:</td>
                        <td>${returnDetailsData.address()}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="table-responsive col-lg-12 col-xl-6">
                <table class="table table-fixed-width">
                    <tbody>
                    <tr>
                        <td class="fw-bold">Numer PESEL:</td>
                        <td>${returnDetailsData.pesel()}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Numer telefonu:</td>
                        <td><a href="tel:${returnDetailsData.phoneNumber()}">${returnDetailsData.phoneNumber()}</a></td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Płeć:</td>
                        <td>${returnDetailsData.gender().name}</td>
                    </tr>
                    <tr>
                        <td class="fw-bold">Miejscowość zamieszkania:</td>
                        <td>${returnDetailsData.cityWithPostalCode()}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>
<h3 class="fs-5 mt-3 mb-0">Zestawienie sprzętów</h3>
<hr class="mt-2"/>
<div class="table-responsive">
    <table class="table table-bordered table-striped table-sm table-hover bg-white">
        <thead>
        <tr>
            <th scope="col" class="nowrap-tb align-middle lh-sm">ID</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Nazwa</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Ilość<br>(szt.)</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Cena całkowita<br>(netto)</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Cena całkowita<br>(brutto)</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Kaucja<br>(netto)</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Kaucja<br>(brutto)</th>
            <th scope="col" class="nowrap-tb align-middle lh-sm">Akcja</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${equipmentsReturnDetailsData}" var="equipment">
            <tr>
                <td class="nowrap-tb align-middle">${equipment.id()}</td>
                <td class="nowrap-tb align-middle">${equipment.name()}</td>
                <td class="nowrap-tb align-middle">${equipment.count()} szt.</td>
                <td class="nowrap-tb align-middle">
                    <fmt:formatNumber value="${equipment.priceNetto()}" minFractionDigits="2" type="currency"/>
                </td>
                <td class="nowrap-tb align-middle">
                    <fmt:formatNumber value="${equipment.priceBrutto()}" minFractionDigits="2" type="currency"/>
                </td>
                <td class="nowrap-tb align-middle">
                    <fmt:formatNumber value="${equipment.depositPriceNetto()}" minFractionDigits="2" type="currency"/>
                </td>
                <td class="nowrap-tb align-middle">
                    <fmt:formatNumber value="${equipment.depositPriceBrutto()}" minFractionDigits="2" type="currency"/>
                </td>
                <td class="nowrap-tb align-middle fit">
                    <c:if test="${not empty equipment.name()}">
                        <button class="btn btn-outline-dark btn-sm py-0" type="button" data-bs-toggle="modal"
                                data-bs-target="#equipmentDetails${equipment.id()}">
                            Szczegóły
                        </button>
                    </c:if>
                    <c:if test="${empty equipment.name()}">
                        <button class="btn btn-outline-dark btn-sm py-0 disabled" type="button" data-bs-toggle="modal"
                                data-bs-target="#equipmentDetails${equipment.id()}" disabled>
                            Szczegóły
                        </button>
                    </c:if>
                </td>
            </tr>
            <div class="modal fade" id="equipmentDetails${equipment.id()}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5">Szczegóły</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body lh-sm">
                            <p class="lh-sm text-secondary mb-3">Kod kreskowy (${equipment.barcode()}):</p>
                            <p class="mb-3">
                                <img src="${pageContext.request.contextPath}/resources/bar-codes/${equipment.barcode()}.png"
                                    alt="" class="mx-auto d-block" height="120px"/>
                            </p>
                            <p class="lh-sm text-secondary mb-1">Dodatkowy opis:</p>
                            <p class="mb-0">
                                    ${empty equipment.description() ? '<i>brak opisu</i>' : equipment.description()}
                            </p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">Zamknij</button>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
        <tr class="table-dark">
            <td colspan="2" class="nowrap-tb align-middle">Podsumowanie:</td>
            <td class="nowrap-tb align-middle">${totalSum} szt.</td>
            <td class="nowrap-tb align-middle">
                <fmt:formatNumber value="${returnDetailsData.totalPriceNetto()}" minFractionDigits="2" type="currency"/>
            </td>
            <td class="nowrap-tb align-middle">
                <fmt:formatNumber value="${returnDetailsData.totalPriceBrutto()}" minFractionDigits="2" type="currency"/>
            </td>
            <td class="nowrap-tb align-middle">
                <fmt:formatNumber value="${returnDetailsData.totalDepositPriceNetto()}" minFractionDigits="2" type="currency"/>
            </td>
            <td colspan="2" class="nowrap-tb align-middle">
                <fmt:formatNumber value="${returnDetailsData.totalDepositPriceBrutto()}" minFractionDigits="2" type="currency"/>
            </td>
        </tr>
        </tbody>
    </table>
</div>
<h3 class="fs-5 mt-3 mb-0">Dodatkowy opis</h3>
<hr class="mt-2"/>
<p class="mb-0 lh-sm">${returnDetailsData.description()}</p>
