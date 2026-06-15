<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>История книги — Library</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:set var="activePage" value="books" scope="request"/>
<%@ include file="common/header.jsp" %>

<main>
    <div class="page-header">
        <h1>История выдачи: <c:out value="${book.title()}"/></h1>
        <a class="btn" href="${pageContext.request.contextPath}/web/books">К списку книг</a>
    </div>

    <p>Всего выдач: ${book.loanCount()}</p>

    <div class="table-wrap">
        <table>
            <thead>
            <tr>
                <th>Пользователь</th>
                <th>Взята</th>
                <th>Возвращена</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="loan" items="${loans}">
                <tr>
                    <td><c:out value="${loan.userFullName()}"/></td>
                    <td>${loan.takenAt()}</td>
                    <td>
                        <c:choose>
                            <c:when test="${loan.returnedAt() != null}">
                                ${loan.returnedAt()}
                            </c:when>
                            <c:otherwise>
                                <span class="badge taken">На руках</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty loans}">
                <tr><td colspan="3">Книгу пока никто не брал</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
