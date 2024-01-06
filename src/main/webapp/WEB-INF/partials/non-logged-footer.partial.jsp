<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="java.time.LocalDateTime" %>

<footer class="bg-white py-4">
  <div class="container">
    <div class="row">
      <div class="col-sm-6 d-flex justify-content-center justify-content-sm-start">
        <span class="me-1">&copy; <%= LocalDateTime.now().getYear() %> by</span>
        <a class="link-dark" href="${pageContext.request.contextPath}/">SkiRent Service</a>
      </div>
      <div class="col-sm-6 d-flex justify-content-center justify-content-sm-end">
        <ul class="list-unstyled mb-0 d-flex">
          <li>
            <a class="link-dark" href="${pageContext.request.contextPath}/login">
              Logowanie
            </a>
          </li>
          <li class="ms-4">
            <a class="link-dark" href="${pageContext.request.contextPath}/forgot-password-request">
              Zapomniałem/am hasła
            </a>
          </li>
        </ul>
      </div>
    </div>
  </div>
</footer>
