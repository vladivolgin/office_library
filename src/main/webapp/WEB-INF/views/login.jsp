<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вход — Library</title>
</head>
<body>
<h1>Вход в систему</h1>

<c:if test="${param.error != null}">
    <p style="color:red;">Неверный логин или пароль</p>
</c:if>
<c:if test="${param.logout != null}">
    <p style="color:green;">Вы вышли из системы</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/web/login">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div>
        <label for="username">Логин</label>
        <input type="text" id="username" name="username" required/>
    </div>
    <div>
        <label for="password">Пароль</label>
        <input type="password" id="password" name="password" required/>
    </div>
    <button type="submit">Войти</button>
</form>
</body>
</html>
