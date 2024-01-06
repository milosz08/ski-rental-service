<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>

<div class="modal fade" id="deleteRent${param.id}" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body lh-sm">
        Czy na pewno chcesz usunąć wypożyczenie <strong>${param.issuedIdentifier}</strong>?
        Operacji nie można cofnąć.
      </div>
      <div class="modal-footer">
        <a href="${pageContext.request.contextPath}/seller/delete-rent?id=${param.id}"
           type="button" class="btn btn-sm btn-outline-danger">Usuń</a>
        <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
          Zamknij
        </button>
      </div>
    </div>
  </div>
</div>
