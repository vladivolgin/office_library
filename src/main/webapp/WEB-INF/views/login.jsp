<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вход — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-page">
    <div class="login-card">
        <h1>📚 Library</h1>

        <c:if test="${param.error != null}">
            <div class="alert error">Неверный логин или пароль</div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div class="alert success">Вы вышли из системы</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/web/login">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="field">
                <label for="username">Логин</label>
                <input type="text" id="username" name="username" required/>
            </div>
            <div class="field">
                <label for="password">Пароль</label>
                <input type="password" id="password" name="password" required/>
            </div>
            <button type="submit">Войти</button>
        </form>

        <c:if test="${param.registered != null}">
            <div class="alert success">Регистрация прошла успешно, теперь можно войти</div>
        </c:if>

        <p><a href="${pageContext.request.contextPath}/web/register">Зарегистрироваться</a></p>
    </div>
</div>
</body>
</html>
