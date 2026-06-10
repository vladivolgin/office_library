<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Главная — Library</title>
    <style>
        body { font-family: sans-serif; margin: 2em; }
        table { border-collapse: collapse; width: 100%; margin-top: 1em; }
        th, td { border: 1px solid #ccc; padding: 6px 10px; text-align: left; }
        th { background: #f0f0f0; }
        .free { color: green; }
        .taken { color: #b00; }
    </style>
</head>
<body>
<h1>Library — панель управления</h1>
<p>Вы вошли как: <strong>${username}</strong></p>

<ul>
    <li>Книги: ${bookCount}</li>
    <li>Авторы: ${authorCount}</li>
    <li>Пользователи: ${userCount}</li>
</ul>

<h2>Список книг</h2>
<table>
    <thead>
    <tr>
        <th>Название</th>
        <th>Год</th>
        <th>Жанр</th>
        <th>Авторы</th>
        <th>Статус</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="book" items="${books}">
        <tr>
            <td>${book.title()}</td>
            <td>${book.publishYear()}</td>
            <td>${book.genre()}</td>
            <td>
                <c:forEach var="author" items="${book.authors()}" varStatus="st">
                    ${author.fullName()}<c:if test="${!st.last}">, </c:if>
                </c:forEach>
            </td>
            <td>
                <c:choose>
                    <c:when test="${book.takenByUserId() != null}">
                        <span class="taken">Взята (пользователь #${book.takenByUserId()})</span>
                    </c:when>
                    <c:otherwise>
                        <span class="free">Свободна</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<form method="post" action="${pageContext.request.contextPath}/web/logout">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit">Выйти</button>
</form>
</body>
</html>
