<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Пользователи — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="users" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <h1>Пользователи</h1>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Логин</th>
                <th>ФИО</th>
                <th>Год рождения</th>
                <th>Роль</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.username}</td>
                    <td>${user.fullName}</td>
                    <td>${user.birthYear}</td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/web/users/${user.id}/role" class="inline-form">
                            <select name="role">
                                <c:forEach var="role" items="${roles}">
                                    <option value="${role}" ${role == user.role ? 'selected' : ''}>${role}</option>
                                </c:forEach>
                            </select>
                            <button type="submit">Сохранить</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
