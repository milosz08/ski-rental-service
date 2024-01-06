<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>

<jsp:useBean id="sorterData"
             type="java.util.Map<java.lang.String, pl.polsl.skirentalservice.paging.sorter.ServletSorterField>"
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
            name="sortBy" type="submit" value="fullName">
      Imię i nazwisko
      <i class="bi bi-arrow-${sorterData.get("fullName").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb align-middle">
    <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
            name="sortBy" type="submit" value="pesel">
      Nr PESEL
      <i class="bi bi-arrow-${sorterData.get("pesel").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
    <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
            name="sortBy" type="submit" value="email">
      Adres email
      <i class="bi bi-arrow-${sorterData.get("email").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
    <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
            name="sortBy" type="submit" value="phoneNumber">
      Nr telefonu
      <i class="bi bi-arrow-${sorterData.get("phoneNumber").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb d-none d-lg-table-cell align-middle">
    <button class="border-0 bg-transparent fw-bold text-start hstack justify-content-between w-100 lh-sm"
            name="sortBy" type="submit" value="address">
      Adres zamieszkania
      <i class="bi bi-arrow-${sorterData.get("address").chevronBts} mx-1 micro-font"></i>
    </button>
  </th>
  <th scope="col" class="nowrap-tb fit align-middle">Akcja</th>
</tr>
</thead>
