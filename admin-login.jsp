<!DOCTYPE html>
<html>
<head>
	<title>Home Inspection</title>
	<link rel="stylesheet" href="stylesheets/controlpanel.css">
</head>
<body class='admin'>
<%@include file="adminHeader.jsp"%>

<form action="company-login" method="post" class='company-login'>
	<input type='hidden' name='adminlogin' value='true'>
	<div>
		<label for="username">Username:</label>
		<input type="text" name="username" id="username">
	</div>

	<div>
		<label for="password">Password:</label>
		<input type="password" name="password" id="password">
	</div>

	<button type="submit">Login</button>

	<% if("true".equals(request.getParameter("error"))) { %>
	<div class='error'>Login failed. Please try again.</div>
	<% } %>
</form>
</body>
</html>