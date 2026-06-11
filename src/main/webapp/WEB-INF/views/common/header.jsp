<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<header class="app-header">
    <div class="brand">📚 Library</div>
    <nav>
        <a href="${pageContext.request.contextPath}/web/dashboard"
           class="${activePage == 'dashboard' ? 'active' : ''}">Главная</a>
        <a href="${pageContext.request.contextPath}/web/books"
           class="${activePage == 'books' ? 'active' : ''}">Книги</a>
        <a href="${pageContext.request.contextPath}/web/authors"
           class="${activePage == 'authors' ? 'active' : ''}">Авторы</a>
        <sec:authorize access="hasRole('EDITOR')">
            <a href="${pageContext.request.contextPath}/web/users"
               class="${activePage == 'users' ? 'active' : ''}">Пользователи</a>
        </sec:authorize>
    </nav>
    <div class="user-box">
        <span>${username}</span>
        <form method="post" action="${pageContext.request.contextPath}/web/logout">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit">Выйти</button>
        </form>
    </div>
</header>
