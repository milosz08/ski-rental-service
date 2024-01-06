<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:useBean id="filterData" type="pl.polsl.skirentalservice.paging.filter.FilterDataDto" scope="request"/>
<jsp:useBean id="inmemoryNewRentData" type="pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto" scope="session"/>
<jsp:useBean id="equipmentsData" type="java.util.List<pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto>"
             scope="request"/>
<jsp:useBean id="sorterData"
             type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.paging.sorter.ServletSorterField>"
             scope="request"/>

<p:generic-seller.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Kreator nowego wypożyczenia</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/dashboard">Panel główny</a>
      </li>
      <li class="breadcrumb-item">
        <a class="link-dark" href="${pageContext.request.contextPath}/seller/customers">Lista klientów</a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">Kreator nowego wypożyczenia</li>
    </ol>
  </nav>
  <hr/>
  <form action="" method="post" novalidate>
    <div class="row mb-3 mx-0">
      <div class="col-md-6 px-0 mb-2">
        <div class="hstack">
          <label for="searchBar"></label>
          <input type="search" class="form-control form-control-sm search-max-width" name="searchText"
                 placeholder="Szukaj po:" id="searchBar" value="${filterData.searchText}">
          <label for="searchBy">
            <select class="form-select form-select-sm fit-content ms-2" name="searchBy" id="searchBy">
              <c:forEach items="${filterData.searchBy}" var="searchItem">
                <option value="${searchItem.value}" ${searchItem.isSelected}>${searchItem.text}</option>
              </c:forEach>
            </select>
          </label>
          <button type="submit" class="btn btn-dark btn-sm ms-2">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
    </div>
    <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
    <div class="table-responsive">
      <table class="table table-bordered table-striped table-sm table-hover bg-white">
        <thead>
        <tr>
          <th scope="col" class="nowrap-tb align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="identity">
              ID
              <i class="bi bi-arrow-${sorterData.get("identity").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="name">
              Nazwa
              <i class="bi bi-arrow-${sorterData.get("name").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="type">
              Typ
              <i class="bi bi-arrow-${sorterData.get("type").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="countInStore">
              Ilość na stanie (szt.)
              <i class="bi bi-arrow-${sorterData.get("countInStore").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="pricePerHour">
              Cena za godzinę (netto)
              <i class="bi bi-arrow-${sorterData.get("pricePerHour").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="priceForNextHour">
              Kolejna godzina<br>(netto)
              <i class="bi bi-arrow-${sorterData.get("priceForNextHour").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
            <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
                    name="sortBy" type="submit" value="pricePerDay">
              Cena za dzień<br>(netto)
              <i class="bi bi-arrow-${sorterData.get("pricePerDay").chevronBts} mx-1 micro-font"></i>
            </button>
          </th>
          <th scope="col" class="nowrap-tb align-middle fit">Akcja</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${equipmentsData}" var="equipment">
          <tr>
            <td class="nowrap-tb align-middle">${equipment.id}</td>
            <td class="nowrap-tb align-middle">${equipment.name}</td>
            <td class="nowrap-tb align-middle">${equipment.type}</td>
            <td class="nowrap-tb align-middle">${equipment.totalCount} szt.</td>
            <td class="nowrap-tb align-middle">
              <fmt:formatNumber value="${equipment.pricePerHour}" minFractionDigits="2" type="currency"/>
            </td>
            <td class="nowrap-tb align-middle">+
              <fmt:formatNumber value="${equipment.priceForNextHour}" minFractionDigits="2" type="currency"/>
            </td>
            <td class="nowrap-tb align-middle">
              <fmt:formatNumber value="${equipment.pricePerDay}" minFractionDigits="2" type="currency"/>
            </td>
            <td class="nowrap-tb align-middle fit">
              <button type="button" class="btn btn-sm btn-success py-0" data-bs-toggle="modal"
                      data-bs-target="#addEquipment${equipment.id}" ${equipment.disabled}>
                Dodaj
              </button>
              <button type="button" class="btn btn-sm btn-outline-dark py-0 px-1" data-bs-toggle="modal"
                      data-bs-target="#showBarcode${equipment.id}">
                <span type="button" data-bs-toggle="tooltip" data-bs-placement="left"
                      data-bs-title="Pokaż kod kreskowy">
                  <i class="bi bi-upc-scan align-middle lh-sm"></i>
                </span>
              </button>
            </td>
          </tr>
          <jsp:include page="/WEB-INF/partials/common/common-bar-code-modal.partial.jsp">
            <jsp:param name="id" value="${equipment.id}"/>
            <jsp:param name="barcode" value="${equipment.barcode}"/>
          </jsp:include>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <jsp:include page="/WEB-INF/partials/pagination.partial.jsp"/>
  </form>
  <c:forEach items="${equipmentsData}" var="equipment">
    <c:set var="cartEqAdd" value="${equipment}" scope="request"/>
    <c:import url="/WEB-INF/partials/seller/add-equipment-to-cart-modal.partial.jsp"/>
  </c:forEach>
  <hr/>
  <h2 class="fs-4 fw-normal text-dark mb-2">Zestawienie dodanych sprzętów</h2>
  <c:if test="${empty inmemoryNewRentData.equipments}">
    <div class="alert alert-warning">Nie dodano jeszcze żadnych sprzętów do nowego wypożyczenia.</div>
  </c:if>
  <c:if test="${not empty inmemoryNewRentData.equipments}">
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
          <th scope="col" class="nowrap-tb align-middle lh-sm fit">Akcja</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${inmemoryNewRentData.equipments}" var="cartEq">
          <tr>
            <c:set var="cartEq" value="${cartEq}" scope="request"/>
            <jsp:include page="/WEB-INF/partials/seller/rent-summary-prices-table.partial.jsp"/>
            <td class="nowrap-tb align-middle fit">
              <button type="button" class="btn btn-sm btn-danger py-0 px-1" data-bs-toggle="modal"
                      data-bs-target="#deleteEquipment${cartEq.id}">
                <span type="button" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Usuń pozycję">
                  <i class="bi bi-x-lg align-middle lh-sm"></i>
                </span>
              </button>
              <button type="button" class="btn btn-sm btn-outline-dark py-0" data-bs-toggle="modal"
                      data-bs-target="#editEquipment${cartEq.id}">
                Edytuj
              </button>
              <button type="button" class="btn btn-sm btn-dark py-0" data-bs-toggle="modal"
                      data-bs-target="#cartEquipmentDetails${cartEq.id}">
                Szczegóły
              </button>
            </td>
          </tr>
        </c:forEach>
        <tr class="table-dark">
          <jsp:include page="/WEB-INF/partials/seller/rent-summary-total-prices-table.partial.jsp"/>
          <td class="nowrap-tb align-middle"></td>
        </tr>
        </tbody>
      </table>
    </div>
    <c:forEach items="${inmemoryNewRentData.equipments}" var="cartEq">
      <div class="modal fade" id="cartEquipmentDetails${cartEq.id}" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h1 class="modal-title fs-5">Szczegóły</h1>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body lh-sm">
              <p class="lh-sm text-secondary mb-1">Dodatkowy opis:</p>
              <p class="mb-0">
                  ${empty cartEq.description ? '<i>brak opisu</i>' : cartEq.description}
              </p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">Zamknij</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal fade" id="deleteEquipment${cartEq.id}" data-bs-backdrop="static" tabindex="-1"
           aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-body lh-sm">
              Czy na pewno chcesz usunąć pozyję <strong>${cartEq.name}</strong>? Operacji nie można cofnąć.
              Akcja spowoduje wszystkich sztuk wybranego sprzętu. Jeśli chcesz zmienić ilość, kliknij w
              przycisk "Edytuj". Po usunięciu pozycji, ceny zostaną automatycznie przeliczone na podstawie
              pozostałych sprzętów.
            </div>
            <div class="modal-footer">
              <a href="${pageContext.request.contextPath}/seller/delete-equipment-from-cart?id=${cartEq.id}"
                 type="button" class="btn btn-sm btn-outline-danger">Usuń</a>
              <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
                Zamknij
              </button>
            </div>
          </div>
        </div>
      </div>
      <c:set var="cartEq" value="${cartEq}" scope="request"/>
      <c:import url="/WEB-INF/partials/seller/edit-equipment-from-cart-modal.partial.jsp"/>
    </c:forEach>
  </c:if>
  <hr/>
  <div class="hstack gap-3 justify-content-between">
    <a href="${pageContext.request.contextPath}/seller/create-new-rent?id=${inmemoryNewRentData.customerId}"
       class="btn btn-sm btn-outline-dark">
      <i class="bi bi-arrow-left me-1"></i>Powrót do edycji danych wypożyczenia
    </a>
    <div class="hstack gap-3">
      <button class="btn btn-sm btn-outline-danger" type="button" data-bs-toggle="modal"
              data-bs-target="#deleteInMemoryRentData">
        <i class="bi bi-arrow-return-left align-middle me-1 lh-sm"></i>Odrzuć zmiany
      </button>
      <button class="btn btn-sm btn-dark" type="button" data-bs-toggle="modal"
              data-bs-target="#createNewRentModal">
        Stwórz nowe wypożyczenie
      </button>
    </div>
  </div>
  <jsp:include page="/WEB-INF/partials/seller/delete-in-memory-rent-data-modal.partial.jsp"/>
  <div class="modal fade" id="createNewRentModal" tabindex="-1" data-bs-backdrop="static" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5">Potwierdzenie stworzenia nowego wypożyczenia</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body lh-sm">
          <div class="alert alert-primary">
            Po kliknięciu w przycisk zostaniesz przekierowany/na do strony wszystkich wypożyczeń. Zostanie
            również wygenerowany dokument PDF z podsumowaniem stworzonego zamówienia oraz na adres email
            kierownika, pracownika oraz klienta zostanie przesłana wiadomość ze szczegółami zamawianych
            sprzętów ogólnym i podsumowaniem.
          </div>
          <div class="alert alert-warning">
            UWAGA! Wypożyczenie można stworzyć tylko wtedy, gdy dodano jakiekolwiek sprzęty do tabeli
            zestawienia wypożyczenia.
          </div>
          <h3 class="fs-5 mt-4 mb-0">Szczegóły wypożyczenia</h3>
          <hr class="mt-2"/>
          <div class="container-fluid">
            <div class="row">
              <div class="table-responsive col-lg-12 col-xl-6">
                <table class="table table-fixed-width">
                  <tbody>
                  <tr>
                    <td class="fw-bold">Numer wypożyczenia:</td>
                    <td>${inmemoryNewRentData.issuedIdentifier}</td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Data utworzenia zgłoszenia:</td>
                    <td><p:datetime-formatter datetime="${inmemoryNewRentData.issuedDateTime}"/></td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Podatek:</td>
                    <td>${inmemoryNewRentData.tax}%</td>
                  </tr>
                  </tbody>
                </table>
              </div>
              <div class="table-responsive col-lg-12 col-xl-6">
                <table class="table table-fixed-width">
                  <tbody>
                  <tr>
                    <td class="fw-bold">Data wypożyczenia:</td>
                    <td><p:datetime-formatter datetime="${inmemoryNewRentData.rentDateTime}"/></td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Planowany czas zwrotu:</td>
                    <td><p:datetime-formatter datetime="${inmemoryNewRentData.returnDateTime}"/></td>
                  </tr>
                  <tr>
                    <td class="fw-bold">Status wypożyczenia:</td>
                    <td>${inmemoryNewRentData.rentStatus.status}</td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <h3 class="fs-5 mt-4 mb-0">Szczegóły klienta</h3>
          <hr class="mt-2"/>
          <c:set var="customerData" value="${inmemoryNewRentData.customerDetails}" scope="request"/>
          <jsp:include page="/WEB-INF/partials/common/customer/common-customer-details.partial.jsp"/>
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
              </tr>
              </thead>
              <tbody>
              <c:forEach items="${inmemoryNewRentData.equipments}" var="cartEq">
                <tr>
                  <c:set var="cartEq" value="${cartEq}" scope="request"/>
                  <jsp:include page="/WEB-INF/partials/seller/rent-summary-prices-table.partial.jsp"/>
                </tr>
              </c:forEach>
              <tr class="table-dark">
                <jsp:include page="/WEB-INF/partials/seller/rent-summary-total-prices-table.partial.jsp"/>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="modal-footer">
          <a href="${pageContext.request.contextPath}/seller/persist-new-rent"
             class="btn btn-sm btn-outline-success">
            Stwórz nowe wypożyczenie
          </a>
          <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">Zamknij</button>
        </div>
      </div>
    </div>
  </div>
</p:generic-seller.wrapper>
