<%@ page import="sqlrow.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Home Inspection</title>
		<script type="text/javascript" src="scripts/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="scripts/database.js"></script>
		<script type="text/javascript" src="scripts/inspectionDEMO.js"></script>
		<link rel="stylesheet" href="stylesheets/mobile.css">
		<meta name='viewport' content='width=device-width, minimum-scale=1.0, maximum-scale=1.0' >
	</head>
	<body onload='setTimeout(function() { window.scrollTo(0, 1) }, 100);'>
		<script type="text/javascript">
			// Check if a new cache is available on page load.
			window.addEventListener('load', function(e) {
				window.applicationCache.addEventListener('updateready', function(e) {
			    	if (window.applicationCache.status == window.applicationCache.UPDATEREADY) {
			     		// Browser downloaded a new app cache.
			     		// Swap it in and reload the page to get the new hotness.
						window.applicationCache.swapCache();
			        	window.location.reload();
			    	}
			  	}, false);
			}, false);
		</script>
		<% 	String inspectionID = request.getParameter("id"); 
			Inspection inspection = Inspections.getInspection(Integer.parseInt(inspectionID));
		%>
		<h1><%= inspection.getFirstName() %> <%= inspection.getLastName() %></h1>
		<% 	Calendar date = Calendar.getInstance();
			date.setTime(inspection.getDate());
		%>
		<h3><%= date.get(Calendar.MONTH) + 1 %>/<%= date.get(Calendar.DAY_OF_MONTH)%>/<%= date.get(Calendar.YEAR)%></h3>
		
		<form action='new-inspection' method='get'>
		<input type="hidden" name="inspectionid" value="<%= inspectionID %>">
		<% 	List<InspectionArea> areas = InspectionAreas.getAllForInspection(Utils.parseInt(inspectionID, 0));
		
			for(InspectionArea area : areas)
			{ %>
				<div class='area-name'><%= area.getName() %></div>
			
				<% 	List<LineItem> topLevelLineItems = LineItems.getTopLevelLineItems(area.getArea());
				
					for(LineItem topLevel : topLevelLineItems)
					{ %>
						<div class='top-level line-item' id='toplevel_<%= topLevel.getID() %>'><%= topLevel.getName() %><span class='result'></span></div>
						<div class='indent'>
						<%	List<LineItem> childItems = LineItems.getChildren(topLevel.getID());
							Map<Integer, InspectionAreaLineItem> inspectionLineItems = InspectionAreaLineItems.getMapForArea(area.getID());
						
							for(LineItem child : childItems)
							{ 
								String result = inspectionLineItems.get(child.getID()) != null ? inspectionLineItems.get(child.getID()).getResult() : null;
								String comment = inspectionLineItems.get(child.getID()) != null ? inspectionLineItems.get(child.getID()).getComment().trim() : null;
							%>
								<div class='child line-item'><%= child.getName() %></div>
								<div class='line-item buttons' id="buttons_<%= child.getID() %>">
									<button type='button' class='button pass <%= "PASS".equals(result) ? " selected" : result != null ? " not-selected" : "" %>' onclick='submitResult("<%= child.getID() %>", "<%= area.getID() %>", "<%= topLevel.getID() %>", "PASS"); hideComment("<%= child.getID() %>");'>Pass</button>
									<button type='button' class='button fail <%= "FAIL".equals(result) ? " selected" : result != null ? " not-selected" : "" %>' onclick='submitResult("<%= child.getID() %>", "<%= area.getID() %>", "<%= topLevel.getID() %>", "FAIL"); failLineItem("<%= child.getID() %>");'>Fail</button>
									<button type='button' class='button na <%= "NA".equals(result) ? " selected" : result != null ? " not-selected" : "" %>' onclick='submitResult("<%= child.getID() %>", "<%= area.getID() %>", "<%= topLevel.getID() %>", "NA"); hideComment("<%= child.getID() %>");'>N/A</button>
									<textarea id='comment_<%= child.getID() %>' name='comment_<%= child.getID() %>' class='comment' placeholder="Why did it fail?" <%= Utils.notEmpty(comment) ? "style='display:block'" : "" %>><%= Utils.notEmpty(comment) ? comment : "" %></textarea>
								</div>
						<%	} %>
						</div>
						<script type="text/javascript">getAreaResult('<%= area.getID() %>', '<%= topLevel.getID() %>');</script>
				<%	} %>
		<% 	} %>
			
			<div class="divider"></div>
			<button type='submit' name='finished' class='finish button' value='true'>Finish</button>
		</form>
	</body>
</html>