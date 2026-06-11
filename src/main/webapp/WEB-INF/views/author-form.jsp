<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Новый автор — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="authors" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <h1>Добавить автора</h1>

    <div class="form-card">
        <form:form modelAttribute="authorDto" method="post" action="${pageContext.request.contextPath}/web/authors/new">
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
            <div class="field">
                <label for="biography">Биография</label>
                <form:textarea path="biography" id="biography" rows="4"/>
                <form:errors path="biography" cssClass="alert error"/>
            </div>
            <button type="submit">Сохранить</button>
        </form:form>
    </div>
</main>
</body>
</html>
