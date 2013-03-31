
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Accounts</title>
</head>
<body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:out value="Accounts"/>
    <jsp:useBean id="displayAccounts" scope="application" class="processingEmulator.DisplayAccounts"/>
    <c:set var="accounts" value="${displayAccounts.accounts}" />
    <c:forEach items="${accounts}" var="account">
        <p>
        <c:out value="${account.id} : ${account.money}"/>
        </p>
    </c:forEach>
</body>
</html>