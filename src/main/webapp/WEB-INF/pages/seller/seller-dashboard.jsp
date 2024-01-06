<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-seller.wrapper>
  <h1 class="fs-2 fw-normal text-dark mb-2">Panel główny</h1>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item active" aria-current="page">Panel główny</li>
    </ol>
  </nav>
  <hr/>
  <jsp:include page="/WEB-INF/partials/dynamic-alert.partial.jsp"/>
  <div class="alert alert-primary">
    Witaj w systemie <strong>SkiRental</strong> służącego do zarządzania wypożyczalnią sprzętów narciarskich.
    Aktualnie jesteś zalogowany na konto <strong>pracownika wypożyczalni</strong>. Z podstawowych czynności, jakie
    możesz wykonywać z tego konta to: tworzenie i usuwanie wypożyczenia, tworzenie zwrotu, dodawanie/edytowanie/usuwanie
    klientów oraz podgląd dostępnych sprzętów. Poniżej znajdziesz dodatkowe informacje i linki.
  </div>
  <div class="container-fluid px-0">
    <div class="row">
      <div class="col-md-4 d-flex align-items-stretch">
        <a href="${pageContext.request.contextPath}/seller/equipments"
           class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
          <div class="card-header">Sprzęty narciarskie</div>
          <div class="card-body">
            <h5 class="card-title">Wyświetl wszystkie sprzęty narciarskie</h5>
            <p class="card-text lh-sm text-secondary fw-normal">
              Kliknij tutaj, aby sprawdzić dostępne sprzęty narciarskie, ich szczegóły oraz
              dostępną ilość w systemie.
            </p>
          </div>
        </a>
      </div>
      <div class="col-md-4 d-flex align-items-stretch">
        <a href="${pageContext.request.contextPath}/seller/customers"
           class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
          <div class="card-header">Klienci</div>
          <div class="card-body">
            <h5 class="card-title">Wyświelt wszystkich klientów</h5>
            <p class="card-text lh-sm text-secondary fw-normal">
              Kliknij tutaj, aby wyświetlić wszystkich klientów systemu, sprawdzić ich dane osobowe oraz
              usunąć wybranego klienta. Z tej pozycji możesz również stworzyć nowe wypożyczenie.
            </p>
          </div>
        </a>
      </div>
      <div class="col-md-4 d-flex align-items-stretch">
        <a href="${pageContext.request.contextPath}/seller/add-customer"
           class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
          <div class="card-header">Klienci</div>
          <div class="card-body">
            <h5 class="card-title">Dodaj nowego klienta</h5>
            <p class="card-text lh-sm text-secondary fw-normal">
              Kliknij tutaj, aby dodać nowego klienta do systemu. Po dodaniu nowego klienta będziesz miał
              możliwość stworzenia nowego wypożyczenia.
            </p>
          </div>
        </a>
      </div>
      <div class="col-md-4 d-flex align-items-stretch">
        <a href="${pageContext.request.contextPath}/seller/rents"
           class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
          <div class="card-header">Wypożyczenia</div>
          <div class="card-body">
            <h5 class="card-title">Wyświetl wszystkie wypożyczenia</h5>
            <p class="card-text lh-sm text-secondary fw-normal">
              Kliknij tutaj, aby wyświetlić wszystkie wypożyczenia w systemie. Z tej pozycji masz również
              możliwość podglądnięcia szczegółów oraz usunięcie wybranego wypożyczenia.
            </p>
          </div>
        </a>
      </div>
      <div class="col-md-4 d-flex align-items-stretch">
        <a href="${pageContext.request.contextPath}/seller/returns"
           class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
          <div class="card-header">Zwroty</div>
          <div class="card-body">
            <h5 class="card-title">Wyświetl wszystkie zwroty</h5>
            <p class="card-text lh-sm text-secondary fw-normal">
              Kliknij tutaj, aby wyświetlić wszystkie zwroty w systemie. Z tej pozycji masz również
              możliwość podglądnięcia szczegółów wybranego zwrotu towaru.
            </p>
          </div>
        </a>
      </div>
      <div class="col-md-4 d-flex align-items-stretch">
        <a href="${pageContext.request.contextPath}/seller/profile"
           class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
          <div class="card-header">Moje konto</div>
          <div class="card-body">
            <h5 class="card-title">Wyświetl dane konta</h5>
            <p class="card-text lh-sm text-secondary fw-normal">
              Kliknij tutaj, aby wyświetlić szczegółowe dane osobowe Twojego konta z którego aktualnie
              korzystasz w systemie.
            </p>
          </div>
        </a>
      </div>
    </div>
  </div>
  <hr/>
</p:generic-seller.wrapper>
