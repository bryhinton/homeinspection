// Comment

function submitResult(lineItemID, areaID, topLevelID, result) {
	var ajaxResult = $.get("inspection-result?lineitemid=" + lineItemID + "&inspectionareaid=" + areaID + "&result=" + result,
							function(data) {
								$("#buttons_" + lineItemID + " button").removeClass("selected");
								$("#buttons_" + lineItemID + " button").addClass("not-selected");
								
								$("#buttons_" + lineItemID + " ." + data).removeClass("not-selected");
								$("#buttons_" + lineItemID + " ." + data).addClass("selected");
								
								getAreaResult(areaID, topLevelID) },
							"text");
}

function getAreaResult(areaID, topLevelID) {
	$.get("inspection-result?inspectionareaid=" + areaID + "&toplevelid=" + topLevelID,
			function(data) {
		var resultElement = $("#toplevel_" + topLevelID + " span.result");
		if("pass" == data)
		{
			resultElement.html("&#x2713;");
			resultElement.removeClass("fail");
			resultElement.addClass("pass");
		}
		else if("fail" == data)
		{
			resultElement.html("X");
			resultElement.addClass("fail");
			resultElement.removeClass("pass");
		}
		else
		{
			resultElement.html("");
			resultElement.removeClass("fail");
			resultElement.removeClass("pass");
		}
	}, 
	"text");
}

function failLineItem(lineItemID) {
	$("#comment_" + lineItemID).css("display", "block");
}

function hideComment(lineItemID) {
	$("#comment_" + lineItemID).val("");
	$("#comment_" + lineItemID).css("display", "none");
}

// **************** ON PAGE LOAD ********************
// 1. Check for unfinished inspection, prompt whether to keep it (delete if no)
// 2. Check network (if no, skip everything)
// 3. Push all finished inspections
// 4. If keeping unfinished inspection, store in object
// 5. Pull schema for main database(which wipes database and creates new tables if the local schema is out of date)
// 6. Re-add the unfinished inspection, if necessary, then pull schema for stash database

$(document).ready(function(){
	useMainDatabase();
	selectRecord("inspection", {finished: "false"}, checkForUnfinishedInspections);
	console.log("Querying for unfinished inspection");
});

function checkForUnfinishedInspections(tx, results) {
	if(results.rows.length > 0) {
		if(confirm("You have an unfinished inspection. Click OK to delete it.")) {
			deleteRecord("inspection", {"id": results.rows.item(0).id}, pushInspections());
		}
		else {
			stashUnfinishedInspection(results);
		}
	}
}

function stashUnfinishedInspection(results) {
	console.log("Stashing unfinished inspection");
	inspectionResults = {ready:false};
	
	selectRecord("inspection", {finished: "false"}, function(tx, results) {
		if(results.rows.length > 0) {
			inspectionResults["inspection"] = results.rows.item(0);
		}
	});
}

// 1. Push inspection row
// 2. Get the ID for that inspection back
// 3. Update inspectionAreas and inspection row
// 4. Push inspectionArea rows
// 		a. For each
//			i. Get ID back
//			ii. Update inspectionAreaLineItems and inspectionArea rows
// 5. Push inspectionAreaLineItem rows
// 6. Delete inspection (just the inspection row, because the database will cascade the delete)
function pushInspections() {
	if(navigator.onLine) {
		console.log("Pushing inspections");
		
	}
	else {
		console.log("No internet connection. Unable to push finished inspections.")
	}
}

function useMainDatabase() {
	dbName = "inspections";
	dbDescription = "Inspections";
	dbVersion = "1.0";
	dbSize = 1024*1024*5; //5MB
}

function useStashDatabase() {
	dbName = "stash";
	dbDescription = "Stash";
	dbVersion = "1.0";
	dbSize = 1024; //1K - This should never store more than one inspection
}