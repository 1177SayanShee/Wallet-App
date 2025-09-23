<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Message</title></head>
<body>
  <c:if test="${not empty msg}">
    <div style="color:green">${msg}</div>
  </c:if>
  <c:if test="${not empty error}">
    <div style="color:red">${error}</div>
  </c:if>
  <a href="index.jsp">Back to login</a>
</body>
</html>
