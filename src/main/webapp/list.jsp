<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="prefix" value="${requestScope.prefix.concat('-')}" scope="request"/>
<c:forEach items="${requestScope.companies}" var="company">
    <c:forEach begin="0" end="${company.nestingLevel}" varStatus="loop">
        -
    </c:forEach>
    ${company.name}, earnings: ${company.earnings} <br/>

    <c:set var="companies" value="${company.children}" scope="request"/>
    <jsp:include page="list.jsp"/>

</c:forEach>



