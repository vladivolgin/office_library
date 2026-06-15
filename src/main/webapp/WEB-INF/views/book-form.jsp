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
    <h1>${editMode ? 'Редактировать книгу' : 'Добавить книгу'}</h1>

    <c:if test="${conflictError != null}">
        <div class="alert error">${conflictError}</div>
    </c:if>

    <div class="form-card">
        <form:form modelAttribute="bookDto" method="post"
                    action="${editMode ? pageContext.request.contextPath.concat('/web/books/').concat(bookId).concat('/edit') : pageContext.request.contextPath.concat('/web/books/new')}">
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
            <div class="field">
                <label for="authorSearch">Авторы</label>
                <input type="text" id="authorSearch" placeholder="Начните вводить имя автора..." autocomplete="off"/>
                <div id="authorDropdown" class="author-dropdown"></div>
                <div id="authorChips" class="author-chips"></div>
            </div>
            <button type="submit">Сохранить</button>
        </form:form>
    </div>
</main>

<script>
    (function () {
        var authors = [
            <c:forEach var="author" items="${authors}" varStatus="status">
            {id: ${author.id}, name: "<c:out value="${author.fullName}" escapeXml="true"/>"}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        var selectedIds = [
            <c:forEach var="author" items="${bookDto.authors()}" varStatus="status">
            ${author.id()}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        var searchInput = document.getElementById('authorSearch');
        var dropdown = document.getElementById('authorDropdown');
        var chips = document.getElementById('authorChips');
        var form = searchInput.closest('form');

        function renderChips() {
            chips.innerHTML = '';
            selectedIds.forEach(function (id) {
                var author = authors.find(function (a) { return a.id === id; });
                if (!author) return;

                var chip = document.createElement('span');
                chip.className = 'author-chip';
                chip.textContent = author.name;

                var remove = document.createElement('button');
                remove.type = 'button';
                remove.className = 'author-chip-remove';
                remove.textContent = '×';
                remove.addEventListener('click', function () {
                    selectedIds = selectedIds.filter(function (sid) { return sid !== id; });
                    renderChips();
                });
                chip.appendChild(remove);

                var hidden = document.createElement('input');
                hidden.type = 'hidden';
                hidden.name = 'authorIds';
                hidden.value = id;
                chip.appendChild(hidden);

                chips.appendChild(chip);
            });
        }

        function renderDropdown() {
            var query = searchInput.value.trim().toLowerCase();
            dropdown.innerHTML = '';
            if (!query) {
                dropdown.style.display = 'none';
                return;
            }

            var matches = authors.filter(function (a) {
                return selectedIds.indexOf(a.id) === -1 && a.name.toLowerCase().indexOf(query) !== -1;
            });

            if (matches.length === 0) {
                dropdown.style.display = 'none';
                return;
            }

            matches.forEach(function (author) {
                var item = document.createElement('div');
                item.className = 'author-dropdown-item';
                item.textContent = author.name;
                item.addEventListener('click', function () {
                    selectedIds.push(author.id);
                    renderChips();
                    searchInput.value = '';
                    dropdown.style.display = 'none';
                    searchInput.focus();
                });
                dropdown.appendChild(item);
            });
            dropdown.style.display = 'block';
        }

        searchInput.addEventListener('input', renderDropdown);
        searchInput.addEventListener('focus', renderDropdown);
        document.addEventListener('click', function (e) {
            if (e.target !== searchInput) {
                dropdown.style.display = 'none';
            }
        });

        renderChips();
    })();
</script>
</body>
</html>
