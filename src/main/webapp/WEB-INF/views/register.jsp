<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Именно spring-form taglib (а не jakarta.tags.form): он умеет биндить поля
     формы на RegisterDto и автоматически подставлять ошибки валидации (form:errors). --%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-page">
    <div class="login-card">
        <h1>📚 Library</h1>
        <h2>Регистрация</h2>

        <c:if test="${conflictError != null}">
            <div class="alert error"><c:out value="${conflictError}"/></div>
        </c:if>

        <%-- form:form сам подставляет CSRF-токен, поэтому ручной hidden-инпут ниже не нужен.
             Сейчас CSRF для /web/** отключён (см. SecurityConfig), но если его вернут —
             достаточно убрать только дублирующий ручной инпут. --%>
        <form:form modelAttribute="registerDto" method="post" action="${pageContext.request.contextPath}/web/register">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="field">
                <label for="username">Логин</label>
                <form:input path="username" id="username"/>
                <form:errors path="username" cssClass="alert error"/>
            </div>
            <div class="field">
                <label for="password">Пароль</label>
                <form:password path="password" id="password"/>
                <form:errors path="password" cssClass="alert error"/>
            </div>
            <div class="field">
                <label for="fullName">ФИО</label>
                <form:input path="fullName" id="fullName"/>
                <form:errors path="fullName" cssClass="alert error"/>
            </div>
            <div class="field">
                <label for="birthYear">Год рождения</label>
                <form:input path="birthYear" id="birthYear" type="number"/>
                <form:errors path="birthYear" cssClass="alert error"/>
            </div>
            <%-- Роль фиксирована: пользователь не может выдать себе EDITOR через регистрацию --%>
            <input type="hidden" name="role" value="READER"/>
            <button type="submit">Зарегистрироваться</button>
        </form:form>

        <p><a href="${pageContext.request.contextPath}/web/login">Уже есть аккаунт? Войти</a></p>
    </div>
</div>
</body>
</html>
