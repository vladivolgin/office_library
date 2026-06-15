<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Авторы — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="authors" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <div class="page-header">
        <h1>Авторы</h1>
        <sec:authorize access="hasRole('EDITOR')">
            <a class="btn" href="${pageContext.request.contextPath}/web/authors/new">+ Добавить автора</a>
        </sec:authorize>
    </div>

    <div class="authors-grid">
        <c:forEach var="author" items="${authors}">
            <div class="author-card">
                <h2><c:out value="${author.fullName}"/></h2>
                <div class="meta">Год рождения: ${author.birthYear}</div>
                <p><c:out value="${author.biography}"/></p>
                <div class="works-title">Произведения</div>
                <ul>
                    <c:forEach var="book" items="${author.books}">
                        <li><c:out value="${book.title}"/> (${book.publishYear})</li>
                    </c:forEach>
                </ul>
                <sec:authorize access="hasRole('EDITOR')">
                    <form method="post" action="${pageContext.request.contextPath}/web/authors/${author.id}/delete" class="inline-form" onsubmit="return confirm('Удалить этого автора?');">
                        <button type="submit" class="btn-danger">Удалить</button>
                    </form>
                </sec:authorize>
            </div>
        </c:forEach>
    </div>
</main>
</body>
</html>
