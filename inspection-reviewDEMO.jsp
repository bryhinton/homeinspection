<%@ page import="sqlrow.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Home Inspection</title>
		<script type="text/javascript" src="scripts/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="scripts/inspectionDEMO.js"></script>
		<script type="text/javascript" src="scripts/database.js"></script>
		<link rel="stylesheet" href="stylesheets/mobile.css">
		<meta name='viewport' content='width=device-width, minimum-scale=1.0, maximum-scale=1.0' >
	</head>
	<body onload='setTimeout(function() { window.scrollTo(0, 1) }, 100);'>
		
		<% 	String inspectionID = request.getParameter("id"); 
			Inspection inspection = Inspections.getInspection(Integer.parseInt(inspectionID));
		%>
		<h1><%= inspection.getFirstName() %> <%= inspection.getLastName() %></h1>
		<% 	Calendar date = Calendar.getInstance();
			date.setTime(inspection.getDate());
		%>
		<h3><%= date.get(Calendar.MONTH) + 1 %>/<%= date.get(Calendar.DAY_OF_MONTH)%>/<%= date.get(Calendar.YEAR)%></h3>
		
		<div class='popup' id='popup-repairs'>
			<div class='close-X' onclick="$('#popup-repairs').css('display', 'none');">X</div>
			<table>
				<tr><td class='repair-name'>Repair Task 1</td><td class='repair-price'>$149.99</td></tr>
				<tr><td class='repair-name'>Repair Task 2<span class='recommended'>Recommended</span></td><td class='repair-price'>$189.99</td></tr>
				<tr><td class='repair-name'>Repair Task 3</td><td class='repair-price'>$209.99</td></tr>
				<tr><td class='see-more'>See More...</td></tr>
			</table>
		</div>
		
		<% 	List<InspectionArea> areas = InspectionAreas.getAllForInspection(Utils.parseInt(inspectionID, 0));
		
			for(InspectionArea area : areas)
			{ %>
				<div class='area-name'><%= area.getName() %></div>
			
				<% 	List<LineItem> topLevelLineItems = LineItems.getTopLevelLineItems(area.getArea());
				
					for(LineItem topLevel : topLevelLineItems)
					{ %>
						<div class='top-level line-item' id='toplevel_<%= topLevel.getID() %>'><%= topLevel.getName() %><span class='result'></span></div>
						<div class='indent'>
							<table class='result-table'>
						<%	List<LineItem> childItems = LineItems.getChildren(topLevel.getID());
							Map<Integer, InspectionAreaLineItem> inspectionLineItems = InspectionAreaLineItems.getMapForArea(area.getID());
						
							for(LineItem child : childItems)
							{ 
								String result = inspectionLineItems.get(child.getID()) != null ? inspectionLineItems.get(child.getID()).getResult() : null;
								String comment = inspectionLineItems.get(child.getID()) != null ? inspectionLineItems.get(child.getID()).getComment() : null;
								
								if("FAIL".equals(result))
								{
							%>
								<tr>
									<td class='child line-item'><%= child.getName() %></td>
									<td class='result-cell <%= "PASS".equals(result) ? " pass" : "FAIL".equals(result) ? " fail" : " na" %>'>
										<%= "PASS".equals(result) ? "&#x2713;" : "FAIL".equals(result) ? "X" : "n/a" %>
										<img src="images/blueArrow.gif" class="arrow" onclick="$('#popup-repairs').css('display', 'block');">
									</td>
								</tr>
								<% 	if(Utils.notEmpty(comment))
									{ %>
										<tr><td class='review-comment' colspan='2'><%= comment %></td></tr>
								<%	} %>
							<%	} %>
						<%	} %>
							</table>
						</div>
						<script type="text/javascript">getAreaResult('<%= area.getID() %>', '<%= topLevel.getID() %>');</script>
				<%	} %>
		<% 	} %>
	</body>
</html>