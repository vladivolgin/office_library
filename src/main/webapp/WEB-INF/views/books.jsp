<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Книги — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="books" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <div class="page-header">
        <h1>Список книг</h1>
        <sec:authorize access="hasRole('EDITOR')">
            <a class="btn" href="${pageContext.request.contextPath}/web/books/new">+ Добавить книгу</a>
        </sec:authorize>
    </div>

    <div class="table-wrap">
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
                    <td><span class="genre-tag">${book.genre()}</span></td>
                    <td>
                        <c:forEach var="author" items="${book.authors()}" varStatus="st">
                            ${author.fullName()}<c:if test="${!st.last}">, </c:if>
                        </c:forEach>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${book.takenByUserId() != null}">
                                <span class="badge taken">Занята (пользователь #${book.takenByUserId()})</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge free">Свободна</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
