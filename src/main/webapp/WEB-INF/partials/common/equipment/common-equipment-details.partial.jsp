<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="equipmentData" class="pl.polsl.skirentalservice.dto.equipment.EquipmentDetailsResDto" scope="request"/>

<div class="container-fluid">
    <div class="row mb-3">
        <div class="col-md-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Nazwa sprzętu:</td>
                    <td>${equipmentData.name}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Model sprzętu:</td>
                    <td>${equipmentData.model}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Nr identyfikacyjny (kod kreskowy):</td>
                    <td>${equipmentData.barcode}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Marka sprzętu:</td>
                    <td>${equipmentData.brand}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="col-md-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Typ sprzętu:</td>
                    <td>${equipmentData.type}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Przeznaczony dla:</td>
                    <td>${equipmentData.gender.name}</td>
                </tr>
                <tr>
                    <td class="fw-bold">Rozmar (w cm):</td>
                    <td><fmt:formatNumber value="${equipmentData.size}" minFractionDigits="2"/> cm</td>
                </tr>
                <tr>
                    <td class="fw-bold">Kolor sprzętu:</td>
                    <td>${equipmentData.color}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="row mb-3">
        <div class="col-md-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Ilość w magazynie (całkowita):</td>
                    <td>${equipmentData.totalCount} szt.</td>
                </tr>
                <tr>
                    <td class="fw-bold">Cena netto za godzinę wypożyczenia:</td>
                    <td>
                        <fmt:formatNumber value="${equipmentData.pricePerHour}" minFractionDigits="2" type="currency"/>
                    </td>
                </tr>
                <tr>
                    <td class="fw-bold">Cena netto za dzień wypożyczenia:</td>
                    <td>
                        <fmt:formatNumber value="${equipmentData.pricePerDay}" minFractionDigits="2" type="currency"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="col-md-6">
            <table class="table table-fixed-width">
                <tbody>
                <tr>
                    <td class="fw-bold">Ilość w magazynie (dostępna):</td>
                    <td>${equipmentData.availableCount} szt.</td>
                </tr>
                <tr>
                    <td class="fw-bold">Cena netto za kolejną godzinę wypożyczenia:</td>
                    <td>
                        <fmt:formatNumber value="${equipmentData.priceForNextHour}" minFractionDigits="2" type="currency"/>
                    </td>
                </tr>
                <tr>
                    <td class="fw-bold">Wartość sprzętu:</td>
                    <td>
                        <fmt:formatNumber value="${equipmentData.valueCost}" minFractionDigits="2" type="currency"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="row mb-3">
        <div class="col-md-12">
            <p class="fw-bold mb-0">Dodatkowy opis sprzętu:</p>
            <p class="">${equipmentData.description}</p>
        </div>
    </div>
</div>
