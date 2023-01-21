<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright (c) 2023 by multiple authors
  ~ Silesian University of Technology
  ~
  ~  File name: pagination.partial.jsp
  ~  Last modified: 21/01/2023, 16:18
  ~  Project name: ski-rental-service
  ~
  ~ This project was written for the purpose of a subject taken in the study of Computer Science.
  ~ This project is not commercial in any way and does not represent a viable business model
  ~ of the application. Project created for educational purposes only.
  --%>

<jsp:useBean id="pagesData" class="pl.polsl.skirentalservice.dto.pagination.PaginationDto" scope="request"/>

<div class="container-fluid mt-3">
    <div class="row">
        <div class="col-md-4 d-flex justify-content-start px-0 mb-2">
            <div class="dropdown max-width-fit">
                <button class="btn btn-dark btn-sm dropdown-toggle max-width-fit" type="button" data-bs-toggle="dropdown">
                    Ilość elementów: ${pagesData.totalPerPage}
                </button>
                <ul class="dropdown-menu dropdown-menu-dark">
                    <li><a href="${pageContext.request.contextPath}?page=1&total=10" class="dropdown-item">10</a></li>
                    <li><a href="${pageContext.request.contextPath}?page=1&total=20" class="dropdown-item">20</a></li>
                    <li><a href="${pageContext.request.contextPath}?page=1&total=25" class="dropdown-item">25</a></li>
                    <li><a href="${pageContext.request.contextPath}?page=1&total=50" class="dropdown-item">50</a></li>
                    <li><a href="${pageContext.request.contextPath}?page=1&total=100" class="dropdown-item">100</a></li>
                </ul>
            </div>
        </div>
        <div class="col-md-4 d-flex justify-content-center align-items-center px-0 mb-2">
            Wyświetlanie od ${pagesData.fromRecords} do ${pagesData.toRecords} rekordów z (${pagesData.totalRecords})
        </div>
        <nav class="col-md-4 d-flex justify-content-sm-end justify-content-center align-items-center px-0 mb-2">
            <ul class="pagination pagination-sm">
                <li class="page-item ${pagesData.prevPage.btsClass}">
                    <a href="${pageContext.request.contextPath}?page=1&total=10" class="page-link px-1">
                        <i class="bi bi-chevron-double-left"></i>
                    </a>
                </li>
                <li class="page-item ${pagesData.prevPage.btsClass}">
                    <a href="${pageContext.request.contextPath}?page=${pagesData.prevPage.pageNumber}&total=10"
                        class="page-link px-1">
                        <i class="bi bi-chevron-left"></i>
                    </a>
                </li>
                <c:forEach items="${pagesData.pages}" var="page">
                    <li class="page-item ${page.btsClass}">
                        <a href="${pageContext.request.contextPath}?page=${page.pageNumber}&total=10"
                            class="page-link px-2">
                            ${page.pageNumber}
                        </a>
                    </li>
                </c:forEach>
                <li class="page-item ${pagesData.nextPage.btsClass}">
                    <a href="${pageContext.request.contextPath}?page=${pagesData.nextPage.pageNumber}&total=10"
                        class="page-link px-1">
                        <i class="bi bi-chevron-right"></i>
                    </a>
                </li>
                <li class="page-item ${pagesData.nextPage.btsClass}">
                    <a href="${pageContext.request.contextPath}?page=${pagesData.allPages}&total=10"
                        class="page-link px-1">
                        <i class="bi bi-chevron-double-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</div>
