<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Новая книга — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="books" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <h1>Добавить книгу</h1>

    <div class="form-card">
        <form:form modelAttribute="bookDto" method="post" action="${pageContext.request.contextPath}/web/books/new">
            <div class="field">
                <label for="title">Название</label>
                <form:input path="title" id="title"/>
                <form:errors path="title" cssClass="alert error"/>
            </div>
            <div class="field">
                <label for="publishYear">Год публикации</label>
                <form:input path="publishYear" id="publishYear" type="number"/>
                <form:errors path="publishYear" cssClass="alert error"/>
            </div>
            <div class="field">
                <label for="genre">Жанр</label>
                <form:input path="genre" id="genre"/>
                <form:errors path="genre" cssClass="alert error"/>
            </div>
            <button type="submit">Сохранить</button>
        </form:form>
    </div>
</main>
</body>
</html>
