<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<%--
  ~ Copyright (c) 2023 by multiple authors
  ~ Silesian University of Technology
  ~
  ~  File name: reject-changes.partial.jsp
  ~  Last modified: 21/01/2023, 16:18
  ~  Project name: ski-rental-service
  ~
  ~ This project was written for the purpose of a subject taken in the study of Computer Science.
  ~ This project is not commercial in any way and does not represent a viable business model
  ~ of the application. Project created for educational purposes only.
  --%>

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
