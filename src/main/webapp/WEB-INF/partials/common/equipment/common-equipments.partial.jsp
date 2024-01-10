<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>

<jsp:useBean id="sorterData"
             type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.core.servlet.pageable.ServletSorterField>"
             scope="request"/>

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
      Ilość<br>dostępnych (szt.)
      <i class="bi bi-arrow-${sorterData.get("countInStore").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
    <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
            name="sortBy" type="submit" value="pricePerHour">
      Cena za godzinę<br>(netto)
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
  <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
    <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
            name="sortBy" type="submit" value="valueCost">
      Wartość<br>(netto)
      <i class="bi bi-arrow-${sorterData.get("valueCost").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb align-middle fit">Akcja</th>
</tr>
</thead>
