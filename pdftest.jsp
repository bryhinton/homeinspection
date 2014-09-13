<%@page import="pdf.GeneratePDF"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>JSP Page</title>
</head>
<body>
<%
	if ("Generate PDF".equals(request.getParameter("OK"))) {
		String reqURL = request.getRequestURL().toString();
		String reqQuery = "btn=false";
		String folderName = "JavaHunter";
		GeneratePDF obj = new GeneratePDF();
		obj.genrateCmd(reqURL, reqQuery, folderName, "786");
	}
%>
<form id="frmPDF" action="" name="frmPDF" method="post">
	<table width="100%" border="2" style='background-color:#f2dbdb;color: #943634; line-height:20px;border:1px solid #943634;font-family: arial, verdana; font-size: 13px;'>
		<tr>
			<th style="background-color: #943634;color: #F2DBDB;">First Name</th>
			<th style="background-color: #943634;color: #F2DBDB;">Last Name</th>
			<th style="background-color: #943634;color: #F2DBDB;">Salary</th>
			<th style="background-color: #943634;color: #F2DBDB;">Place</th>
		</tr>
		<% for (int i = 1; i <= 10; i++) {%>
		<tr>
			<td align="center">Taher</td>
			<td align="center">TINWALA</td>
			<td align="center">1000<%=i%></td>
			<td align="center">Ahmedabad-<%=i%></td>
		</tr>
		<% }%>
	</table>
	<%if(!"false".equals(request.getParameter("btn"))){%>
	<div align="center">
		<br/>
		<input type="submit" id="OK" name="OK" value="Generate PDF"/>
	</div>
	<%}%>
</form>
</body>
</html>