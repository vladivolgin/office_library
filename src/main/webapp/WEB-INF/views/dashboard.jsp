<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Главная — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="dashboard" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <h1>Главная</h1>

    <div class="stats">
        <div class="stat-card">
            <div class="value">${bookCount}</div>
            <div class="label">Книги</div>
        </div>
        <div class="stat-card">
            <div class="value">${authorCount}</div>
            <div class="label">Авторы</div>
        </div>
        <div class="stat-card">
            <div class="value">${userCount}</div>
            <div class="label">Пользователи</div>
        </div>
    </div>

    <h2>Топ-5 популярных книг</h2>
    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Название</th>
                <th>Авторы</th>
                <th>Выдач</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="book" items="${topBooks}">
                <tr>
                    <td><c:out value="${book.title()}"/></td>
                    <td>
                        <c:forEach var="author" items="${book.authors()}" varStatus="st">
                            <c:out value="${author.fullName()}"/><c:if test="${!st.last}">, </c:if>
                        </c:forEach>
                    </td>
                    <td>${book.loanCount()}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty topBooks}">
                <tr><td colspan="3">Книг пока нет</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="section-links">
        <a href="${pageContext.request.contextPath}/web/books">
            Список книг
            <span class="desc">Каталог книг с жанром, годом издания, авторами и статусом доступности</span>
        </a>
        <a href="${pageContext.request.contextPath}/web/authors">
            Авторы
            <span class="desc">Краткие биографии и список произведений каждого автора</span>
        </a>
    </div>
</main>
</body>
</html>
