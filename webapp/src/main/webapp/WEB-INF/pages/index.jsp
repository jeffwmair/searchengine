<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<h2>Search Results Page</h2>
<c:forEach var="item" items="${searchResults}">
	<p>${item}</p>
</c:forEach>
</body>
</html>