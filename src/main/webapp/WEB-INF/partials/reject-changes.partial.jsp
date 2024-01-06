<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>

<div class="modal fade" id="rejectChanges" data-bs-backdrop="static" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-body">
        Czy na pewno chcesz odrzuć zmiany i powrócić do poprzedniej strony?
      </div>
      <div class="modal-footer">
        <a href="${pageContext.request.contextPath}${param.redirectPath}"
           type="button" class="btn btn-sm btn-outline-danger">Odrzuć zmiany</a>
        <button type="button" class="btn btn-sm btn-dark" data-bs-dismiss="modal">
          Zamknij
        </button>
      </div>
    </div>
  </div>
</div>
