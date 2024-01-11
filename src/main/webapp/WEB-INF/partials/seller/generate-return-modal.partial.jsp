<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>

<div class="modal fade" id="generateReturn${param.id}" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <form action="${pageContext.request.contextPath}/seller/generate-return" method="post"
          class="modal-content" novalidate>
      <div class="modal-header">
        <h1 class="modal-title fs-5">Generowanie zwrotu</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <p class="lh-sm">
          Czy na pewno chcesz wygenerować zwrot dla wypożyczenia <strong>${param.issuedIdentifier}</strong>?
          Data zwrotu zostanie ustawiona na aktualną, a ceny zawartych sprzętów zostaną automatycznie
          ponownie przeliczone.
        </p>
        <input type="hidden" name="id" value="${param.id}"/>
        <div>
          <label for="description" class="form-label mb-1 text-secondary micro-font">
            Dodatkowy opis (uwagi do generowanego zwrotu):
          </label>
          <textarea class="form-control form-control-sm" rows="4" id="description" name="description"
                    maxlength="200" placeholder="Wprowadź tutaj dodatkowe uwagi do zwrotu. Maksymalnie 200 znaków."
          ></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button type="submit" class="btn btn-sm btn-outline-success">Generuj zwrot</button>
        <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
          Zamknij
        </button>
      </div>
    </form>
  </div>
</div>
