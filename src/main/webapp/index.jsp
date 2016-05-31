<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Company Manager</title>
</head>
<body>
<H3>Company Manager</H3>
<form action="${pageContext.request.contextPath}/companies" method="post">
    <table>
        <tr><td>Company name:</td><td><input type="text" name="companyName"/></td></tr>
        <tr><td>Earnings:</td><td><input type="text" name="earnings"/></td></tr>
        <tr><td>Parent name:</td><td><input type="text" name="parent"/></td></tr>
    </table>
    <table>
        <tr><td><input type="submit" name="action" value="create"/> </td><td>Creates new company, leave "parent" field empty for top-level company</td></tr>
        <tr><td><input type="submit" name="action" value="retrieve"/> </td><td>Search by name only</td></tr>
        <tr><td><input type="submit" name="action" value="update"/></td><td>Updates earnings</td></tr>
        <tr><td><input type="submit" name="action" value="delete"/></td><td>Deletes company and all child companies</td></tr>
    </table>
    <br/><br/>

    <c:if test="${not empty param.resultCompanyName}">
        <br/>
        <font size="4" color="green"> Search result: </font>
        <br/>
        Company name: ${param.resultCompanyName}<br/>
        Company earnings: ${param.resultEarnings}
        <br/>
        <c:if test="${not empty param.resultParentCompany}">
            Company parent: ${param.resultParentCompany}
        </c:if>
        <c:if test="${empty param.resultParentCompany}">
            Company parent: none (top-level company)
        </c:if>
        <br/>
        <br/>
    </c:if>

    <c:if test="${not empty param.errorMessage}">
        <font size="5" color="red"> ${param.errorMessage}<br/> </font>
    </c:if>
    <font size="4" color="green"> Company tree: </font>
    <br/>
    <c:forEach items="${requestScope.companies}" var="company">
        <c:forEach begin="0" end="${company.nestingLevel}" varStatus="loop">
            -
        </c:forEach>
        ${company.name}, earnings: ${company.earnings} <br/>
        <c:set var="companies" value="${company.children}" scope="request"/>
        <jsp:include page="list.jsp"/>
    </c:forEach>
</form>
</body>
</html>