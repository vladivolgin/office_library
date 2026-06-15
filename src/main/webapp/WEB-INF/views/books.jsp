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

    <c:if test="${conflictError != null}">
        <div class="alert error"><c:out value="${conflictError}"/></div>
    </c:if>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Название</th>
                <th>Год</th>
                <th>Жанр</th>
                <th>Авторы</th>
                <th>Статус</th>
                <th>Выдач</th>
                <th></th>
                <sec:authorize access="hasRole('EDITOR')">
                    <th></th>
                </sec:authorize>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="book" items="${books}">
                <tr>
                    <td><c:out value="${book.title()}"/></td>
                    <td>${book.publishYear()}</td>
                    <td><span class="genre-tag"><c:out value="${book.genre()}"/></span></td>
                    <td>
                        <c:forEach var="author" items="${book.authors()}" varStatus="st">
                            <c:out value="${author.fullName()}"/><c:if test="${!st.last}">, </c:if>
                        </c:forEach>
                    </td>
                    <td>
                        <sec:authorize access="hasRole('EDITOR')">
                            <form method="post" action="${pageContext.request.contextPath}/web/books/${book.id()}/availability" class="availability-form">
                                <select name="userId" class="${book.takenByUserId() != null ? 'is-taken' : 'is-free'}">
                                    <option value="">Свободна</option>
                                    <c:forEach var="u" items="${users}">
                                        <option value="${u.id}" ${u.id == book.takenByUserId() ? 'selected' : ''}>
                                            <c:out value="${u.fullName}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <button type="submit" title="Сохранить">✓</button>
                            </form>
                        </sec:authorize>
                        <sec:authorize access="!hasRole('EDITOR')">
                            <c:choose>
                                <c:when test="${book.takenByUserId() != null}">
                                    <span class="badge taken">Занята</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge free">Свободна</span>
                                </c:otherwise>
                            </c:choose>
                        </sec:authorize>
                    </td>
                    <td>${book.loanCount()}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/web/books/${book.id()}/history">История</a>
                    </td>
                    <sec:authorize access="hasRole('EDITOR')">
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/web/books/${book.id()}/delete" class="inline-form" onsubmit="return confirm('Удалить эту книгу?');">
                                <button type="submit" class="btn-danger">Удалить</button>
                            </form>
                        </td>
                    </sec:authorize>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
