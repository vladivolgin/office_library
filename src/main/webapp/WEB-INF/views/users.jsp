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

    <c:if test="${conflictError != null}">
        <div class="alert error"><c:out value="${conflictError}"/></div>
    </c:if>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Логин</th>
                <th>ФИО</th>
                <th>Год рождения</th>
                <th>Роль</th>
                <th>Статус</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td><c:out value="${user.username}"/></td>
                    <td><c:out value="${user.fullName}"/></td>
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
                    <td>
                        <c:choose>
                            <c:when test="${user.enabled}">
                                <span class="badge free">Активен</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge taken">Заблокирован</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/web/users/${user.id}/enabled" class="inline-form">
                            <c:choose>
                                <c:when test="${user.enabled}">
                                    <input type="hidden" name="enabled" value="false"/>
                                    <button type="submit" class="btn-danger" onclick="return confirm('Заблокировать пользователя?');">Блокировать</button>
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="enabled" value="true"/>
                                    <button type="submit">Разблокировать</button>
                                </c:otherwise>
                            </c:choose>
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
