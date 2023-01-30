<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags" %>

<p:generic-owner.wrapper>
    <h1 class="fs-2 fw-normal text-dark mb-2">Panel główny</h1>
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item active" aria-current="page">Panel główny</li>
        </ol>
    </nav>
    <hr/>
    <div class="alert alert-primary">
        Witaj w systemie <strong>SkiRental</strong> służącego do zarządzania wypożyczalnią sprzętów narciarskich.
        Aktualnie jesteś zalogowany na konto <strong>kierownika wypożyczalni</strong>. Z podstawowych czynności, jakie
        możesz wykonywać z tego konta to: dodawanie nowego sprzętu/usuwanie/modyfikacja, dodawanie pracowników i
        podejrzenie aktywnych zamówień/zwrotów. Poniżej znajdziesz dodatkowe informacje i linki.
    </div>
    <div class="container-fluid px-0">
        <div class="row">
            <div class="col-md-4 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/rents"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Wypożyczenia</div>
                    <div class="card-body">
                        <h5 class="card-title">Wyświetl wszystkie wypożyczenia</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby wyświetlić wszystkie wypożyczenia w systemie. Z tej pozycji masz również
                            możliwość podglądnięcia szczegółów wybranego wypożyczenia.
                        </p>
                    </div>
                </a>
            </div>
            <div class="col-md-4 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/customers"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Klienci</div>
                    <div class="card-body">
                        <h5 class="card-title">Wyświelt wszystkich klientów</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby wyświetlić wszystkich klientów systemu oraz sprawdzić ich dane osobowe.
                        </p>
                    </div>
                </a>
            </div>
            <div class="col-md-4 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/employers"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Pracownicy</div>
                    <div class="card-body">
                        <h5 class="card-title">Wyświetl wszystkich pracowników</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby sprawdzić aktywnych pracowników w systemie, edytować dane osobowe wybranego
                            pracownika oraz mieć możliwość usunięcia wybranego pracownika.
                        </p>
                    </div>
                </a>
            </div>
            <div class="col-md-4 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/add-employer"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Pracownicy</div>
                    <div class="card-body">
                        <h5 class="card-title">Dodaj nowego pracownika</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby dodać nowego pracownika do systemu. Przy dodawaniu nowego pracownika
                            automatycznie zostanie stworzona nowa skrzynka pocztowa. Postępuj zgodnie z procedurami
                            na ekranie.
                        </p>
                    </div>
                </a>
            </div>
            <div class="col-md-4 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/equipments"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Sprzęty narciarskie</div>
                    <div class="card-body">
                        <h5 class="card-title">Wyświetl wszystkie sprzęty narciarskie</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby sprawdzić dostępne sprzęty narciarskie, ich szczegóły, dostępną ilość w
                            systemie oraz edytować lub usunąć wybrany sprzęt narciarski.
                        </p>
                    </div>
                </a>
            </div>
            <div class="col-md-4 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/add-equipment"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Sprzęty narciarskie</div>
                    <div class="card-body">
                        <h5 class="card-title">Dodaj nowy sprzęt narciarski</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby dodać nowy sprzęt narciarski. Atrybuty sprzętów możesz tworzyć
                            samodzielnie przy pomocy dostosowanego formularza.
                        </p>
                    </div>
                </a>
            </div>
            <div class="col-md-6 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/profile"
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
            <div class="col-md-6 d-flex align-items-stretch">
                <a href="${pageContext.request.contextPath}/owner/settings"
                    class="card border-secondary px-0 mb-3 text-decoration-none text-dark w-100">
                    <div class="card-header">Moje konto</div>
                    <div class="card-body">
                        <h5 class="card-title">Wyświetl dane konta</h5>
                        <p class="card-text lh-sm text-secondary fw-normal">
                            Kliknij tutaj, aby edytować dane osobowe zapisane do Twojego konta z którego aktualnie
                            korzystasz w systemie.
                        </p>
                    </div>
                </a>
            </div>
        </div>
    </div>
    <hr/>
</p:generic-owner.wrapper>
