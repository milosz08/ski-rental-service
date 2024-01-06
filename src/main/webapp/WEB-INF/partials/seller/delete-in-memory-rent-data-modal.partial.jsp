<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>

<div class="modal fade" id="deleteInMemoryRentData" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body lh-sm">
        Czy na pewno chcesz porzucić informacje o nowo wprowadzonym wypożyczeniu z tymczasowej pamięci systemu?
        Po usunięciu informacji zostaniesz przeniesiony na stronę z listą klientów w systemie.
      </div>
      <div class="modal-footer">
        <a href="${pageContext.request.contextPath}/seller/reject-new-rent"
           type="button" class="btn btn-sm btn-outline-danger">Usuń</a>
        <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
          Zamknij
        </button>
      </div>
    </div>
  </div>
</div>
