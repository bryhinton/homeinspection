var sentInspections = 0;
var totalInspectionsToSend = 99999999;
var customerDataLoaded = false;
var isOnline = false;

function useMainDatabase() {
	dbName = "inspections";
	dbDescription = "Inspections";
	dbVersion = "1.0";
	dbSize = 1024*1024*5; //5MB
}

function checkSecurity() {
	$(document).trigger("securitycomplete");
}

function pushInspections() {
	selectRecord("inspection", null, function(tx, inspections) {
		if(inspections.rows.length > 0) {
			sentInspections = 0;
			totalInspectionsToSend = inspections.rows.length;
			for(var i = 0; i < inspections.rows.length; i++) {
				if(inspections.rows.item(i)["finished"] == "true") {
					if(isOnline) {
						sendInspection(inspections.rows.item(i)["id"], false);
					}
				}
				else {
					var companyID = window.localStorage["company-id"];
					var techID = window.localStorage["tech-id"];
					if(isNaN(companyID) || isNaN(techID)) {
						if(isOnline) {
							$("#handle-unfinished-inspection").val("true");
							showLogin();
						}
						else {
							showMessage("You are not connected to the network, so you cannot log in.");
						}
					}
					else {
						$("#handle-unfinished-inspection").val("false");
						$("#unfinished-inspection-dialog").css("display", "block");
						$("#unfinished-id").val(inspections.rows.item(i)["id"]);
						break;
					}
				}
			}
		}
		else {
			$(document).trigger("inspectionspushed");
		}
	});
}

function getCustomerData() {
	var companyID = window.localStorage["company-id"];
	var techID = window.localStorage["tech-id"];
	if(isNaN(companyID) || isNaN(techID)) {
		$("#loading").css("display", "none");
		if(isOnline) {
			showLogin();
		}
		else {
			showMessage("You are not connected to the network, so you cannot log in.");
		}
	}
	else {
		$("#company-id").val(companyID);
		$("#tech-id").val(techID);

		$("#loading").css("display", "block");
		$.get("company-info?companyid=" + $("#company-id").val() + "&techid=" + techID + "&key=" + getKey(), addAreaInfo, "xml");
	}
}

function initializeInspection() {
	$("#tech-first-name").text(window.localStorage["tech-first-name"]);
	loadAreas();

	$("#loading").css("display", "none");
	$("#inspection").css("display", "none");
	$("#review").css("display", "none");
	//$("#customer-info").css("display", "block");
	//$("#start-inspection").text("Start Inspection");
	reset(false);

	if($("#loading").css("display") == "block") {
		toggleMask();
	}
}

$(document).ready(function(){
	checkIfOnline();

	window.applicationCache.addEventListener('updateready', function(e) {
		if (window.applicationCache.status == window.applicationCache.UPDATEREADY) {
			// Browser downloaded a new app cache.
			// Swap it in and reload the page to get the new hotness.
			window.applicationCache.swapCache();
			window.location.reload();
		}
	}, false);

	window.applicationCache.addEventListener("error", function(e) {
		console.log("SERVER DOWN; LOADING FROM CACHE");
	});

	// TODO: Put all of this in an event listener for a noupdate application cache status
	$(document).bind("securitycomplete", function(e) {
		if(isOnline) {
			selectRecord("inspection", null, function(tx, results) {
				console.log("Pushing inspections");
				pushInspections();
			}, function(tx) {
				console.log("Could not find inspections");
				if(isOnline) {
					pullSchema();
				}
				else {
					toggleMask();
					showMessage("Please connect to the internet and restart the application.")
				}
			});
		}
		else {
			$(document).trigger("inspectionspushed");
		}
	});

	$(document).bind("inspectionspushed", function(e) {
		console.log("INSPECTIONS PUSHED");

		if(isOnline) {
			pullSchema();
		}
		else {
			$(document).trigger("schemacomplete");
		}
	});

	$(document).bind("schemacomplete", function(e) {
		if(isOnline) {
			getCustomerData();
		}
		else {
			$(document).trigger("customerdataupdated");
		}
	});

	$(document).bind("customerdataupdated", function(e){
		console.log("CUSTOMER DATA UPDATED");
		customerDataLoaded = true;

		if(isOnline) {
			$.get("pricing?companyid=" + $("#company-id").val(), loadPricing);
		}
		else {
			showPricing();
		}
	});

	$(document).bind("pricingloaded", function(e) {
		console.log("PRICING LOADED");

		initializeInspection();
	});

	$("#signature").jSignature({"height": 270, "width": 900});

	useMainDatabase();
	checkSecurity();
	//initializeInspection();

	$("#login .line-item > .value > input").each(function() {
		$(this).bind("keypress", function(e) {
			goToNextInput(e, "login");
		});
	});

	$("#customer-info .line-item > .value > input").each(function() {
		$(this).bind("keypress", function(e) {
			goToNextInput(e, "customer-info");
		});
	});

	$("#phone-number").bind("keydown", function(e) {
		var val = $(this).val();
		val = val.split("-").join("");

		var length = val.length;

		if(e.keyCode == 8) { //Backspace
			length = length - 2;
		}

		if(length >= 3 && length < 7) {
			var newVal = val.substring(0,3);
			newVal += "-";
			newVal += val.substring(3);

			$(this).val(newVal);
		}
		else if(length >= 7) {
			var newVal = val.substring(0,3);
			newVal += "-";
			newVal += val.substring(3,6);
			newVal += "-";
			newVal += val.substring(6);

			$(this).val(newVal);
		}
		else {
			$(this).val(val);
		}
	});

	var buttons = $("button");
	var i = 0;

	while(buttons[i] != null) {
		$(buttons[i]).bind("click", function(e) {
			$(e.target).css("boxShadow", "0px 0px 100px, black");

			$(e.target).animate({"boxShadow": "0px 0px 0px black"}, 100);
		});

		i++;
	}

	if(navigator.userAgent.indexOf("iPad") > 0) {
		$("body").addClass("ipad");
	}
});

function finishSendingInspection() {
	sentInspections++;

	if(sentInspections == totalInspectionsToSend) {
		$(document).trigger("inspectionspushed");
		totalInspectionsToSend = 99999999;
	}
}

function sendUnfinishedInspection() {
	$("#unfinished-inspection-dialog").css("display", "none");

	sendInspection($("#unfinished-id").val(), false);
}

function loadInspection() {
	if($("#loading").css("display") == "block") {
		toggleMask();
	}

	$("#company-id").val(window.localStorage["company-id"]);
	$("#tech-id").val(window.localStorage["tech-id"]);
	$("#tech-first-name").text(window.localStorage["tech-first-name"]);

	$("#review").css("display", "none");
	$("#areas").empty();

	loadAreas();

	var inspectionID = $("#unfinished-id").val();

	selectRecord("inspection", {"id": inspectionID}, function(tx, inspections) {
		if(inspections.rows.length == 0) {
			getCustomerData();
		}
		else {
			var firstName = inspections.rows.item(0)["firstname"];
			var lastName = inspections.rows.item(0)["lastname"];
			var date = new Date();
			date.setTime(inspections.rows.item(0)["date"]);

			$("#first-name").val(firstName);
			$("#last-name").val(lastName);
			$("#address").val(inspections.rows.item(0)["address"]);
			$("#city").val(inspections.rows.item(0)["city"]);
			$("#state").val(inspections.rows.item(0)["state"]);
			$("#zip-code").val(inspections.rows.item(0)["zip"]);
			$("#phone-number").val(inspections.rows.item(0)["phone"]);
			$("#email").val(inspections.rows.item(0)["email"]);

			$("#inspection-id").val(inspectionID);
			$("#inspection-name").text((firstName != "" ? firstName + " ": "") + lastName);
			$("#inspection-date").text(date.toLocaleDateString());

			if(lastName == null || lastName == "") {
				$("#customer-info").css("display", "block");
				$("#inspection").css("display", "none");
			}
			else {
				$("#customer-info").css("display", "none");
				$("#inspection").css("display", "block");
			}

			selectRecord("inspectionarea", {"inspection": inspectionID}, function(tx, areas) {
				for(var i = 0; i < areas.rows.length; i++) {
					addExistingArea(areas.rows.item(i));

					if(i == areas.rows.length - 1) {
						loadLineItems(areas.rows.item(i)["id"], areas.rows.item(i)["area"], false);
					}
				}
			});
		}
	});
}

function showLogin() {
	$("#loading").css("display", "none");
	$("#customer-info").css("display", "none");
	$("#login").css("display", "block");

	$("#username").focus();
}

function login() {
	if(isOnline) {
		$.get("login?username=" + $("#username").val() + "&password=" + $("#password").val() + "&key=" + getKey(), processLogin, "xml");
	}
	else {
		showMessage("You are not connected to the network, so you cannot log in.");
	}
}

function processLogin(xml) {
	var loginMessage = $(".login-message");
	loginMessage.text("");
	if($(xml).find("successful").text() == "true") {
		setNewKey($(xml).find("key").text());

		$("#login").css("display", "none");

		var companyID = $(xml).find("companyid").text();
		var techID = $(xml).find("techid").text();
		var techFirstName = $(xml).find("techfirstname").text();
		$("#company-id").val(companyID);
		$("#tech-id").val(techID);
		$("#tech-first-name").text(techFirstName);
		window.localStorage["company-id"] = companyID;
		window.localStorage["tech-id"] = techID;
		window.localStorage["tech-first-name"] = techFirstName;

		if($("#handle-unfinished-inpsection").val() == "true") {
			pushInspections();
		}
		else {
			getCustomerData();
		}
	}
	else {
		loginMessage.text("Login unsuccessful. Please try again.");
	}
}

function validateCustomerInfo() {
	var lastNameInput = $("#last-name");
	var lastName = lastNameInput.val();
	if(lastName == "") {
		lastNameInput.css("backgroundColor", "#ff9999");
		lastNameInput.focus();
	}
	else {
		lastNameInput.css("backgroundColor", "none");

		var firstName = $("#first-name").val();
		var address = $("#address").val();
		var city = $("#city").val();
		var state = $("#state").val();
		var zipCode = $("#zip-code").val();
		var phoneNumber = $("#phone-number").val();
		var email = $("#email").val();
		var date = new Date();

		$("#inspection-name").text((firstName != "" ? firstName + " ": "") + lastName);
		$("#inspection-date").text(date.toLocaleDateString());
		$("#customer-info").css("display", "none");
		$("#inspection").css("display", "block");

		var inspectionID = $("#inspection-id").val();

		if(inspectionID == null || inspectionID == "") {
			inspectionID = getNewID() + "";
			insertRecord("inspection", {"id": inspectionID, "firstName": firstName, "lastName": lastName, "address": address, "city": city, "state": state, "zip": zipCode, "phone": phoneNumber, "email": email, "date": date.getTime(), "tech": $("#tech-id").val(), "company": $("#company-id").val(), "finished": "false", "sendquote": "true"});
			$("#inspection-id").val(inspectionID);
		}
		else {
			updateRecord("inspection", {"id": inspectionID, "firstName": firstName, "lastName": lastName, "address": address, "city": city, "state": state, "zip": zipCode, "phone": phoneNumber, "email": email, "date": date.getTime(), "tech": $("#tech-id").val(), "company": $("#company-id").val(), "finished": "false", "sendquote": "true"}, {"id": inspectionID});
		}
	}
}

function addAreaInfo(xml) {
	$("#state").val($(xml).find("state").text());

	var newKey = $(xml).find("key").text();
	if(newKey && newKey != "") {
		setNewKey(newKey);

		var paramMap = new Object();
		paramMap["area"] = new Array();
		$(xml).find("areas").children("area").each(function() {
			paramMap["area"][paramMap["area"].length] = {"name": $(this).children("name").text(), "id": $(this).children("id").text()};

			if(paramMap["lineitem"] == null) {
				paramMap["lineitem"] = new Array();
			}

			$(this).find("lineitem").each(function() {
				paramMap["lineitem"][paramMap["lineitem"].length] = {"name": $(this).children("name").text(), "id": $(this).children("id").text(), "area": $(this).children("area").text()};

				$(this).find("subitem").each(function() {
					paramMap["lineitem"][paramMap["lineitem"].length] = {"name": $(this).children("name").text(), "id": $(this).children("id").text(), "area": $(this).children("area").text(), "parent": $(this).children("parent").text()};
				});
			});
		});

		if(paramMap["area"] != null) {
			insertRecords("area", paramMap["area"], function() {
				if(paramMap["lineitem"] != null) {
					insertRecords("lineitem", paramMap["lineitem"], function() {
						$(document).trigger("customerdataupdated");
					});
				}
				else {
					$(document).trigger("customerdataupdated");
				}
			});
		}
		else {
			$(document).trigger("customerdataupdated");
		}
	}
	else {
		$("#loading .loading-text").css("display", "none");
		showMessage("You cannot continue at this time.");
	}
}

function loadAreas() {
	selectRecord("area", null, function(tx, results) {
		$("#add-list").empty();
		if(results.rows && results.rows.length > 0) {
			for(var i = 0; i < results.rows.length; i++) {
				var areaDiv = $("<li></li>");
				areaDiv.attr("data-id", results.rows.item(i)["id"]);
				areaDiv.text(results.rows.item(i)["name"]);
				areaDiv.bind("click", openAddAreaDialog);

				$("#add-list").append(areaDiv);
			}
		}
		else {
			// TODO: Blank State
		}
	});
}

function openAddAreaDialog(e) {
	toggleMask();
	$("#add-area-dialog").css("display", "block");
	$("#new-area-name").val($(e.target).text());
	$("#new-area-id").val($(e.target).attr("data-id"));
	$("#new-area-name").focus();
	$("#new-area-name").select();
}

function addArea() {
	toggleMask();

	var name = $("#new-area-name").val();
	var newAreaDiv = $("<div></div>");
	newAreaDiv.text(name);

	var areaID = $("#new-area-id").val();
	var inspectionAreaID = getNewID() + "";
	newAreaDiv.attr("data-id", inspectionAreaID);
	newAreaDiv.attr("area-id", areaID);
	newAreaDiv.bind("click", function(e) {
		loadLineItems(inspectionAreaID, areaID, false);
	});

	$("#areas").append(newAreaDiv);

	insertRecord("inspectionarea", {"id": inspectionAreaID, "inspection": $("#inspection-id").val(), "area": areaID, "name": name});

	loadLineItems(inspectionAreaID, areaID, true);
}

function addExistingArea(item){
	var name = item["name"];
	var newAreaDiv = $("<div></div>");
	newAreaDiv.text(name);

	var areaID = item["area"];
	var inspectionAreaID = item["id"];
	newAreaDiv.attr("data-id", inspectionAreaID);
	newAreaDiv.attr("area-id", areaID);
	newAreaDiv.addClass("area");
	newAreaDiv.bind("click", function(e) {
		loadLineItems(inspectionAreaID.toString(), areaID.toString(), false);
	});

	$("#areas").append(newAreaDiv);
}

function loadLineItems(inspectionAreaID, areaID, isNew) {
	selectRecord("area", {"id": areaID}, function(tx, results) {
		if(results.rows && results.rows.length > 0) {
			$("#area-name").text(results.rows.item(0)["name"]);
			$("#line-item-list").empty();

			$(".area.active").removeClass("active");

			$(".areas div[data-id=" + inspectionAreaID + "]").addClass("area active");

			selectRecord("lineitem", {"parent": null, "area": areaID}, function(tx, results){
				if(results.rows && results.rows.length > 0) {
					for(var i = 0; i < results.rows.length; i++) {
						var id = results.rows.item(i)["id"];
						var lineItem = $("<ul></ul>");
						lineItem.addClass("line-item-parent");
						lineItem.attr("id", "line-item-" + id);
						lineItem.attr("inspection-area", inspectionAreaID);

						var lineItemParentText = $("<div></div>");
						lineItemParentText.text(results.rows.item(i)["name"]);
						lineItemParentText.addClass("line-item-parent-text");
						lineItem.append(lineItemParentText);

						$("#line-item-list").append(lineItem);

						var searchParams = new Object();
						searchParams["parent"] = id;

						if(!isNew) {
							searchParams["inspectionarea"] = inspectionAreaID;
						}
						selectRecord(isNew ? "lineitem" : "inspectionarealineitem", searchParams, function(tx, lineitems) {
							if(lineitems.rows && lineitems.rows.length > 0) {
								for(var j = 0; j < lineitems.rows.length; j++) {
									makeLineItem(lineitems.rows.item(j), isNew);
								}

								var parentID = lineitems.rows.item(0)["parent"];
								var inspectionAreaID = lineitems.rows.item(0)["inspectionarea"];
								if(isNew) {
									addCustomLineItemButton(parentID, inspectionAreaID);
								}
								else {
									loadCustomLineItems(parentID, inspectionAreaID);
								}
							}
						});

					}
				}
			});
		}
	});
}

function makeLineItem(item, isNew) {
	var lineItemID = isNew ? getNewID() + "" : item["id"];
	var lineItem = $("<li></li>");
	lineItem.attr("data-id", lineItemID);
	lineItem.addClass("line-item");

	var lineItemText = $("<span></span>");
	lineItemText.text(item["name"]);
	lineItemText.addClass("line-item-text");
	lineItem.append(lineItemText);

	addLineItemButtons(lineItem, false);

	$("#line-item-" + item["parent"]).append(lineItem);

	if(isNew) {
		insertRecord("inspectionarealineitem", {"id": lineItemID, "lineitem": item["id"], "inspectionarea": lineItem.parents(".line-item-parent").attr("inspection-area"), "name": item["name"], "parent": item["parent"]});
	}
}

function loadCustomLineItems(parentID, inspectionAreaID) {
	selectRecord("customlineitem", {"parent": parentID, "inspectionarea": inspectionAreaID}, function(tx, results) {
		if(results.rows.length > 0) {
			for(var i = 0; i < results.rows.length; i++) {
				addCustomLineItem(parentID, inspectionAreaID, results.rows.item(i)["name"], results.rows.item(i)["id"], false);
			}
		}

		addCustomLineItemButton(parentID, inspectionAreaID);
	});
}

function addCustomLineItemButton(parentID, inspectionAreaID) {
	var lineItem = $("<li></li>");
	lineItem.addClass("line-item last");
	lineItem.attr("id", "custom-" + parentID);

	var addButton = $("<button type='button'></button>");
	addButton.addClass("add-custom-button line-item-text");
	addButton.text("Add Custom");
	addButton.bind("click", function(e) {
		lineItem.remove();
		addCustomLineItem(parentID, inspectionAreaID, null, null, true);
		addCustomLineItemButton(parentID, inspectionAreaID);
	});

	lineItem.append(addButton);

	$("#line-item-" + parentID).append(lineItem);
}

function addCustomLineItem(parentID, inspectionAreaID, name, id, isNew) {
	var parentLineItem = $("#line-item-" + parentID);

	if(inspectionAreaID == null) {
		inspectionAreaID = parentLineItem.attr("inspection-area");
	}

	var customLineItemID = isNew ? "custom-" + parentID + "-" + inspectionAreaID + "-" + $("#line-item-" + parentID).children(".line-item.custom").length : id;
	var lineItem = $("<li></li>");
	lineItem.addClass("line-item custom");
	lineItem.attr("data-id", customLineItemID);

	var lineItemText = $("<span></span>");
	lineItemText.addClass("line-item-text");

	var nameInput = $("<input type='text'>");
	nameInput.addClass("custom-name");
	nameInput.attr("placeholder", "Other...");
	nameInput.bind("blur", updateCustomLineItemName);

	if(name != null) {
		nameInput.val(name);
	}

	lineItemText.append(nameInput);
	lineItem.append(lineItemText);

	addLineItemButtons(lineItem, true);

	parentLineItem.append(lineItem);

	nameInput.focus();
	nameInput.select();

	if(isNew) {
		insertRecord("customlineitem", {"id": customLineItemID, "parent": parentID + "", "inspectionarea": lineItem.parents(".line-item-parent").attr("inspection-area")});
	}
}

function addLineItemButtons(lineItem, isCustom) {
	selectRecord(isCustom ? "customlineitem" : "inspectionarealineitem", {"id": lineItem.attr("data-id")}, function(tx, results) {
		var result = null;

		if(results.rows.length > 0) {
			result = results.rows.item(0)["result"];
		}

		var buttonsDiv = $("<div></div>");
		buttonsDiv.addClass("buttons-div");

		var passButton = $("<button type='button'>Pass</button>");
		passButton.addClass("inspection-button pass");
		passButton.attr("result", "pass");
		passButton.bind("click", completeLineItem);

		buttonsDiv.append(passButton);

		var failButton = $("<button type='button'>Fail</button>");
		failButton.addClass("inspection-button fail");
		failButton.attr("result", "fail");
		failButton.bind("click", completeLineItem);

		buttonsDiv.append(failButton);

		var naButton = $("<button type='button'>N/A</button>");
		naButton.addClass("inspection-button na");
		naButton.attr("result", "na");
		naButton.bind("click", completeLineItem);

		buttonsDiv.append(naButton);

		var commentButton = $("<button type='button'>+</button>");
		commentButton.addClass("comment-button blue-gradient");
		commentButton.bind("click", function(e) {
			var failTextDiv = $(e.target).siblings(".fail-text-div");

			if(failTextDiv.css("display") != "block") {
				failTextDiv.addClass("comment");
				showFailText(failTextDiv);
			}
			else {
				failTextDiv.removeClass("comment");
				hideFailText(failTextDiv);
			}
		});

		buttonsDiv.append(commentButton);

		var failTextDiv = $("<div></div>");
		failTextDiv.addClass("fail-text-div");


		var failText = $("<textarea></textarea>");
		failText.addClass("fail-text");

		if(result == "fail") {
			failText.val(results.rows.item(0)["comment"]);
			failText.css("color", "black");
			failText.css("fontStyle", "normal");
		}
		else {
			failText.attr("placeholder", "Leave a comment...");
		}
		failTextDiv.append(failText);

		buttonsDiv.append(failTextDiv);
		lineItem.append(buttonsDiv);

		if(result != null && result != "pass") {
			passButton.addClass("gray");
		}

		if(result != null && result != "fail") {
			failButton.addClass("gray");
		}

		if(result != null && result != "na") {
			naButton.addClass("gray");
		}

		if(result == "fail") {
			showFailText(failTextDiv);
		}
	});
}

function updateCustomLineItemName(e) {
	var dataID = $(e.target).parents(".line-item.custom").attr("data-id");

	updateRecord("customlineitem", {"name": $(e.target).val()}, {"id": dataID});
}

function completeLineItem(e) {
	var target = $(e.target);
	var targetClass = target.attr("class");
	target.parents(".line-item").find(".inspection-button").each(function(i) {
		if($(this).attr("class") != targetClass) {
			$(this).addClass("gray");
		}
		else {
			$(this).removeClass("gray");
		}
	});

	var dataID = $(e.target).parents("li").attr("data-id");
	var failTextDiv = $("li[data-id='" + dataID + "']").find(".fail-text-div");
	if(target.hasClass("fail")) {
		showFailText(failTextDiv);
	}
	else if(!failTextDiv.hasClass("comment")) {
		hideFailText(failTextDiv);
	}

	var result = $(e.target).attr("result");

	if(target.parents(".line-item.custom").length > 0) {
		updateRecord("customlineitem", {"result": result}, {"id": dataID});

		var customName = target.parents(".line-item").find(".custom-name");
		if(customName.val() == "") {
			customName.val("Other");

			updateRecord("customlineitem", {"name": "Other"}, {"id": target.parents(".line-item.custom").attr("data-id")})
		}

		var parts = dataID.split("-");
	}
	else {
		updateRecord("inspectionarealineitem", {"result": result}, {"id": dataID});
	}
}

function showFailText(failTextDiv) {
	failTextDiv.css("display", "block");

	var failText = failTextDiv.find(".fail-text");
	failText.focus();
	failText.bind("blur", failTextBlur);

	failTextDiv.animate({height:'70px'}, 100);
}

function hideFailText(failTextDiv) {
	failTextDiv.animate({height:'0px'}, 100, function() {
		failTextDiv.css("display", "none");
	});
}

function failTextBlur(e) {
	var dataID = $(e.target).parents("li").attr("data-id");
	var failText = $("li[data-id='" + dataID + "']").find(".fail-text");
	failText.unbind("blur", failTextBlur);

	if(failText.val() != "") {
		if($(e.target).parents(".line-item.custom").length > 0) {
			updateRecord("customlineitem", {"comment": failText.val()}, {"id": dataID});
		}
		else {
			updateRecord("inspectionarealineitem", {"comment": failText.val()}, {"id": dataID});
		}
		failText.bind("blur", failTextBlur);
	}
}

function confirmReview() {
	toggleMask();
	$("#confirm-finish-dialog").css("display", "block");
}

function reviewInspection() {
	toggleMask();
	$("#inspection").css("display", "none");
	$("#review").css("display", "block");

	$("#review-name").text($("#inspection-name").text());
	$("#review-date").text($("#inspection-date").text());
	$("#review-line-item-list").empty();
	$("#review-areas").empty();
	showBlankReviewMessage();

	selectRecord("inspectionarea", {"inspection": $("#inspection-id").val()}, function(tx, areas) {
		for(var i = 0; i < areas.rows.length; i++) {
			var inspectionAreaID = areas.rows.item(i)["id"];
			var areaID = areas.rows.item(i)["area"];
			var name = areas.rows.item(i)["name"];

			var area = $("<div></div>");
			area.addClass("area");
			area.text(name);
			area.attr("data-id", inspectionAreaID);
			area.attr("area-id", areaID);
			area.bind("click", loadReviewParents);

			var resultIcon = $("<span></span>");
			resultIcon.addClass("icon pass-area");
			resultIcon.html("&#x2713;");
			area.append(resultIcon);

			$("#review-areas").append(area);

			var searchParams = {"inspectionarea": inspectionAreaID, "result": "fail"};
			var checkForQuoteItems = function(searchParams) {
				selectRecord("inspectionarealineitem", searchParams, function(tx, results) {
					if(results.rows.length == 0)
					{
						selectRecord("customlineitem", searchParams, function(tx, results) {
							if(results.rows.length > 0) {
								selectQuery("SELECT QI.*, IALI.INSPECTIONAREA FROM QUOTEITEM QI, INSPECTIONAREALINEITEM IALI WHERE QI.INSPECTIONAREALINEITEM = IALI.ID AND IALI.INSPECTIONAREA = ?", [searchParams["inspectionarea"]], function(tx, results){
									if(results.rows.length == 0) {
										var icon = $("#review-areas").find("div[data-id=" + searchParams["inspectionarea"] + "] span");
										icon.html("&#x2713;");
										icon.removeClass("pass-area");
										icon.addClass("fail-area");
									}
								});
							}
						});
					}
					else {
						selectQuery("SELECT QI.*, IALI.INSPECTIONAREA FROM QUOTEITEM QI, INSPECTIONAREALINEITEM IALI WHERE QI.INSPECTIONAREALINEITEM = IALI.ID AND IALI.INSPECTIONAREA = ?", [results.rows.item(0)["inspectionarea"]], function(tx, results){
							if(results.rows.length == 0) {
								var icon = $("#review-areas").find("div[data-id=" + searchParams["inspectionarea"] + "] span");
								icon.html("&#x2716;");
								icon.removeClass("pass-area");
								icon.addClass("fail-area");
							}
						});
					}
				});
			};
			checkForQuoteItems(searchParams);
		}

		if(areas.rows.length > 0) {
			//$("#review-areas .area[data-id=" + areas.rows.item(0)["id"] + "]").trigger("click");
		}
	});
}

function showBlankReviewMessage() {
	var reviewArea = $("#review-line-item-list");

	var mainDiv = $("<div></div>");
	mainDiv.addClass("review-message");
	mainDiv.html("Choose an area to review <span>&rarr;</span>");

	reviewArea.append(mainDiv);
}

function loadReviewParents(e) {
	var name = $(e.target).html();
	var areaID = $(e.target).attr("area-id");
	var inspectionAreaID = $(e.target).attr("data-id");

	//Reset
	$("#review-areas .area").removeClass("active");
	$(".area[data-id=" + inspectionAreaID + "]").addClass("active");
	$("#review-area-name").html(name);
	$("#review-line-item-list").empty();

	selectRecord("lineitem", {"area": areaID, "parent": null}, function(tx, parents) {
		if(parents.rows.length > 0) {
			for(var i = 0; i < parents.rows.length; i++) {
				var parentID = parents.rows.item(i)["id"];
				var parent = $("<ul></ul>");
				parent.addClass("line-item-parent");
				parent.attr("id", "review-line-item-" + parentID);
				parent.attr("inspection-area", inspectionAreaID);

				var lineItemParentText = $("<div></div>");
				lineItemParentText.text(parents.rows.item(i)["name"]);
				lineItemParentText.addClass("line-item-parent-text");
				parent.append(lineItemParentText);

				var placeholder = $("<li></li>");
				placeholder.addClass("line-item-text placeholder");
				placeholder.text("All items passed!");

				var resultIcon = $("<span></span>");
				resultIcon.addClass("pass-area");
				resultIcon.html("&#x2713;");
				placeholder.append(resultIcon);

				parent.append(placeholder);

				$("#review-line-item-list").append(parent);

				loadReviewLineItems(parentID, inspectionAreaID);
			}
		}
	});
}

function loadReviewLineItems(parentID, inspectionAreaID) {
	var searchParams = {"parent": parentID, "inspectionarea": inspectionAreaID, "result": "fail"};
	selectRecord("inspectionarealineitem", searchParams, addReviewLineItems);
	selectRecord("customlineitem", searchParams, addReviewLineItems);
}

function addReviewLineItems(tx, lineItems)  {
	if(lineItems.rows.length > 0) {
		var parent = $("#review-line-item-" + lineItems.rows.item(0)["parent"]);
		parent.find(".placeholder").remove();

		for(var i = 0; i < lineItems.rows.length; i++) {
			var lineItemID = lineItems.rows.item(i)["id"];
			var lineItem = $("<li></li>");
			lineItem.attr("data-id", lineItemID);
			lineItem.addClass("line-item");

			var lineItemText = $("<span></span>");
			lineItemText.text(lineItems.rows.item(i)["name"]);
			lineItemText.addClass("line-item-text top");
			lineItem.append(lineItemText);

			var failSpan = $("<span></span>");
			failSpan.text("FAIL");
			failSpan.addClass("fail-area top review-fail-text");
			lineItem.append(failSpan);

			var comment = lineItems.rows.item(i)["comment"];
			if(comment && comment != '') {
				var failText = $("<span></span>");
				failText.text(comment);
				failText.addClass("review-text top");
				lineItem.append(failText);
			}

			var countSpan = $("<span></span>");
			countSpan.attr("id", "count-" + lineItemID);
			countSpan.addClass("count top");
			setQuoteItemCount(lineItemID);
			lineItem.append(countSpan);

			var nspgLink = $("<button></button>");
			nspgLink.text("Pricing");
			nspgLink.addClass("nspg");
			nspgLink.bind("click", function(e) {
				toggleMask();
				var button = $(e.target);
				var lineItemID = button.parent(".line-item").attr("data-id");
				var pricingAreaName = $("#review-area-name").text();
				pricingAreaName = pricingAreaName.substring(0, pricingAreaName.length - 1);
				var pricingLineItemParent = button.parents(".line-item-parent").find(".line-item-parent-text").text();
				var pricingLineItem = button.parents(".line-item-parent").find(".line-item[data-id='" + lineItemID +"'] .line-item-text").text();

				$("#pricing-guide").css("display", "block");
				$("#pricing-guide .content .content").addClass("hidden");
				$("#pricing-guide .wrapper > .content").addClass("hidden");
				$("#pricing-id").val(lineItemID);
				$("#pricing-lineitem-parent").val(pricingLineItemParent);
				$("#pricing-lineitem").val(pricingLineItem);
				$("#pricing-guide .wrapper").scrollTop(0);

				$("#pricing-area").val(pricingAreaName);

				$("#pricing-guide > .add-area-title").text(pricingAreaName + " > " + pricingLineItemParent + " > " + pricingLineItem);
			});

			lineItem.append(nspgLink);

			parent.append(lineItem);
		}
	}
}

function setQuoteItemCount(inspectionAreaLineItemID) {
	var lineItemID = inspectionAreaLineItemID ? inspectionAreaLineItemID : $("#pricing-id").val();
	selectRecord("quoteitem", {"inspectionarealineitem": lineItemID}, function(tx, quoteItems) {
		var length = quoteItems.rows.length;
		if(length > 0) {
			$("#count-" + quoteItems.rows.item(0)["inspectionarealineitem"]).text("(" + length + ")");

			var icon = $("#review-areas .area.active span");
			icon.html("&#x2713;");
			icon.removeClass("fail-area");
			icon.addClass("pass-area");
		}
	});
}

function loadPricing(xml) {
	selectQuery("DELETE FROM category", []);
	selectQuery("DELETE FROM task", []);

	var categories = [];
	var tasks = [];

	$(xml).find("category").each(function() {
		var category = {};
		category.id = getNewID();
		category.name = $(this).children("name").text();

		categories[categories.length] = category;

		$(this).find("subcategory").each(function(e) {
			var subCategory = {};
			subCategory.id = getNewID();
			subCategory.name = $(this).children("name").text();
			subCategory.parent = category.id;

			categories[categories.length] = subCategory;

			$(this).find("task").each(function() {
				var task = {};
				task.name = $(this).find("name").text();
				task.number = $(this).find("number").text();
				task.description = $(this).find("description").text();
				task.time = $(this).find("time").text();
				task.parts = $(this).find("parts").text();
				task.standard = $(this).find("col3").find("primary").text();
				task.standardaddon = $(this).find("col3").find("addon").text();
				task.member = $(this).find("col2").find("primary").text();
				task.memberaddon = $(this).find("col2").find("addon").text();
				task.category = subCategory.id;

				tasks[tasks.length] = task;
			});
		});
	});

	insertRecords("category", categories, function(tx, results) {
		insertRecords("task", tasks);
	});

	showPricing();
}

function showPricing() {
	$("#pricing-guide .loading").css("display", "none");

	var table = $("#pricing-table");

	var tableWrapper = $("<div></div>");
	tableWrapper.addClass("wrapper");
	table.append(tableWrapper);

	var records = [];

	selectRecord("category", {"parent": null}, function(tx, categories) {
		if(categories.rows.length > 0) {
			for(var i = 0; i < categories.rows.length; i++) {
				var categoryItem = categories.rows.item(i);
				var category = $("<div></div>");
				category.addClass("category");
				category.text(categoryItem.name);

				category.bind("click", function(e) {
					var content = $(this).next();
					if(content.hasClass("hidden")) {
						content.removeClass("hidden");

						var wrapper = $("#pricing-table .wrapper");
						wrapper.scrollTop(0);
						wrapper.scrollTop(wrapper.scrollTop() + $(this).position().top);
					}
					else {
						content.addClass("hidden");
					}

				});

				var categoryContent = $("<div></div>");
				categoryContent.attr("id", "category-" + categoryItem.id);
				categoryContent.addClass("content hidden");
				tableWrapper.append(category);
				tableWrapper.append(categoryContent);

				selectRecord("category", {"parent": categoryItem.id}, function(tx, subCategories) {
					if(subCategories.rows.length > 0) {
						for(var j = 0; j < subCategories.rows.length; j++) {
							var subCategoryItem = subCategories.rows.item(j);
							var subcategory = $("<div></div>");
							subcategory.addClass("subcategory");
							subcategory.text(subCategoryItem.name);

							subcategory.bind("click", function(e) {
								var content = $(this).next();
								if(content.hasClass("hidden")) {
									content.removeClass("hidden");

									var wrapper = $("#pricing-table .wrapper");
									wrapper.scrollTop(0);
									wrapper.scrollTop(wrapper.scrollTop() + $(this).position().top);
								}
								else {
									content.addClass("hidden");
								}
							});

							var subCategoryContent = $("<div></div>");
							subCategoryContent.attr("id", "category-" + subCategoryItem.id);
							subCategoryContent.addClass("content hidden");

							var header = $("<table></table>");
							header.addClass("pricing-header");
							var headerRow = $("<tr></tr>");

							var timePartsHeader = $("<th></th>");
							timePartsHeader.addClass("time-parts");
							headerRow.append(timePartsHeader);

							var headerName = $("<th></th>");
							headerName.addClass("task-name");
							headerName.text("Name");
							headerRow.append(headerName);

							var headerStandard = $("<th></th>");
							headerStandard.addClass("task-cell");
							headerRow.append(headerStandard);

							header.append(headerRow);

							subCategoryContent.append(header);

							var categoryContent = $("#category-" + subCategoryItem.parent);
							categoryContent.append(subcategory);
							categoryContent.append(subCategoryContent);

							selectRecord("task", {"category": subCategoryItem.id}, function(tx, tasks) {
								if(tasks.rows.length > 0) {
									for(var k = 0; k < tasks.rows.length; k++) {
										var taskItem = tasks.rows.item(k);

										var nameText = taskItem.name;
										var descriptionText = taskItem.description;
										var taskNumber = taskItem.number;

										var task = $("<table></table>");
										task.addClass("task");
										task.attr("data-id", taskNumber);

										var row = $("<tr></tr>");

										var time = $("<td></td>");
										time.html(parseInt(taskItem.time));
										time.addClass("task-cell time-parts");
										row.append(time);

										var name = $("<td></td>");
										name.addClass("task-name");
										name.text(nameText);
										row.append(name);

										var standard = $("<td rowspan='2'></td>");
										standard.addClass("task-cell standard price");
										var addButton = $("<button type='button'></button>");
										addButton.addClass("pass");
										addButton.text("Add");
										addButton.attr("data-id", taskNumber);
										addButton.bind("click", function(e) {
											addQuoteItem($(e.target).attr("data-id"));
										});
										standard.append(addButton);

										var removeButton = $("<button type='button'></button>");
										removeButton.addClass("fail");
										removeButton.text("Remove");
										removeButton.bind("click", function(e) {
											deleteRecord("quoteitem", {"inspectionarealineitem": $("#pricing-id").val(), "task": $(e.target).parents(".task").attr("data-id")});
											showSelectedTasks();
										});
										standard.append(removeButton);

										row.append(standard);

										task.append(row);

										var row2 = $("<tr></tr>");
										row2.addClass("row2");

										var parts = $("<td></td>");
										parts.addClass("task-cell time-parts");
										parts.text(taskItem.parts.split(".").join(""));
										row2.append(parts);

										var description = $("<td></td>");
										description.addClass("task-description");
										description.text(descriptionText != "Null" ? descriptionText : "");
										row2.append(description);

										task.append(row2);

										$("#category-" + taskItem.category).append(task);
									}
								}
							});
						}
					}
				});
			}

			var spacerDiv = $("<div></div>");
			spacerDiv.css("height", "50%");
			tableWrapper.append(spacerDiv);
		}
	});

	var bottomBar = $("<div></div>");
	bottomBar.addClass("pricing-bottom-bar");

	var reviewItemsButton = $("<button></button>");
	reviewItemsButton.addClass("pricing-button review blue-gradient");
	reviewItemsButton.bind("click", showCompleteLineItemQuoteDialog);
	reviewItemsButton.text("Review Items");
	bottomBar.append(reviewItemsButton);

	var closeButton = $("<button></button>");
	closeButton.addClass("pricing-button close blue-gradient");
	closeButton.bind("click", function(e) {
		setQuoteItemCount(); toggleMask();
	});
	closeButton.text("Close");
	bottomBar.append(closeButton);

	table.append(bottomBar);

	$(document).trigger("pricingloaded");

	showSelectedTasks();
}

function showSelectedTasks() {
	selectRecord("quoteitem", {"inspectionarealineitem": $("#pricing-id").val()}, function(tx, quoteItems) {
		$(".task.selected").removeClass("selected");
		if(quoteItems.rows.length > 0) {
			for(var i = 0; i < quoteItems.rows.length; i++) {
				$(".task[data-id='" + quoteItems.rows.item(i)["task"] + "']").addClass("selected");
			}
		}
	});
}

function addQuoteItem(taskNumber) {
	insertRecord("quoteitem", {"id": getNewID() + "", "inspectionarealineitem": $("#pricing-id").val(), "inspection": $("#inspection-id").val(), "task": taskNumber, "active": "false", "addon": "false"}, function(tx, results) {
		showSelectedTasks();
	});
}

function showCompleteLineItemQuoteDialog() {
	$("#pricing-sub-mask").css("display", "block");
	$("#review-lineitem-quote-dialog").css("display", "block");
	$("#review-lineitem-quote").empty();

	selectQuery("SELECT task.name, task.description, task.standard, task.standardaddon, task.member, task.memberaddon, quoteitem.id, quoteitem.active FROM quoteitem, task  WHERE quoteitem.inspectionarealineitem = ? AND quoteitem.task = task.number", [$("#pricing-id").val()], function(tx, tasks) {
		if(tasks.rows.length > 0) {
			for(var i = 0; i < tasks.rows.length; i++) {
				$("#review-lineitem-quote").append(getQuoteItem(tasks.rows.item(i), true));
			}
		}
	});
}

function getQuoteItem(task, shouldRemove) {
	var quoteItemDiv = $("<div></div>");
	quoteItemDiv.addClass("quote-item");

	if(shouldRemove) {
		var deleteX = $("<div></div>");
		deleteX.attr("data-id", task["id"]);
		deleteX.addClass("delete-x fail");
		deleteX.text("X");
		deleteX.bind("click", function(e) {
			deleteRecord("quoteitem", {"id": $(e.target).attr("data-id")});
			$(e.target).parent(".quote-item").remove();
		});
		quoteItemDiv.append(deleteX);
	}
	else {
		var activeCheck = $("<div></div>");
		activeCheck.attr("data-id", task["id"]);
		activeCheck.addClass("delete-x active-check");

		if(task["active"] == "true") {
			activeCheck.addClass("pass");
		}
		else {
			activeCheck.addClass("gray");
		}

		activeCheck.html("&#x2713;");
		activeCheck.bind("click", function(e) {
			var target = $(e.target);

			if(target.hasClass("pass")) {
				target.removeClass("pass");
				target.addClass("gray");

				target.siblings(".amount").addClass("gray-text");
				target.siblings(".name").addClass("gray-text");
				target.siblings(".price").addClass("gray-text");

				updateRecord("quoteitem", {"active": "false"}, {"id": target.attr("data-id")});
				calculateQuoteTotal();
			}
			else {
				target.removeClass("gray");
				target.addClass("pass");

				target.siblings(".amount").removeClass("gray-text");
				target.siblings(".name").removeClass("gray-text");
				target.siblings(".price").removeClass("gray-text");

				updateRecord("quoteitem", {"active": "true"}, {"id": target.attr("data-id")});
				calculateQuoteTotal();
			}
		});

		quoteItemDiv.append(activeCheck);

		var addOnCheck = $("<div></div>");
		addOnCheck.attr("data-id", task["id"]);
		addOnCheck.addClass("addon");

		if(task["addon"] == "true") {
			addOnCheck.addClass("pass");
		}
		else {
			addOnCheck.addClass("gray");
		}

		addOnCheck.text("A");
		addOnCheck.bind("click", function(e) {
			var target = $(e.target);

			if(target.hasClass("pass")) {
				target.removeClass("pass");
				target.addClass("gray");

				target.siblings(".show").removeClass("show");
				target.siblings(".regular").addClass("show");

				updateRecord("quoteitem", {"addon": "false"}, {"id": target.attr("data-id")});
				calculateQuoteTotal();
			}
			else {
				target.removeClass("gray");
				target.addClass("pass");

				target.siblings(".show").removeClass("show");
				target.siblings(".addon-price").addClass("show");

				updateRecord("quoteitem", {"addon": "true"}, {"id": target.attr("data-id")});
				calculateQuoteTotal();
			}
		});

		quoteItemDiv.append(addOnCheck);

		if(task.standardaddon == "") {
			addOnCheck.css("visibility", "hidden");
		}
	}

	var name = $("<div></div>");
	name.addClass("name");

	if(task["active"] != "true") {
		name.addClass("gray-text");
	}

	name.text(task["name"]);
	quoteItemDiv.append(name);

	var description = $("<div></div>");
	description.addClass("description");

	if(task["active"] != "true") {
		description.addClass("gray-text");
	}

	description.text(task["description"]);
	quoteItemDiv.append(description);

	if(!shouldRemove) {
		var standard = $("<div></div>");
		standard.addClass("standard regular price");
		standard.attr("total", task["standard"]);
		standard.text("$" + parseFloat(task["standard"]).toFixed(2));
		quoteItemDiv.append(standard);

		var standardAddOn = $("<div></div>");
		standardAddOn.addClass("standard addon-price price");
		standardAddOn.attr("total", task["standardaddon"]);
		standardAddOn.text("$" + parseFloat(task["standardaddon"]).toFixed(2));
		quoteItemDiv.append(standardAddOn);

		var member = $("<div></div>");
		member.addClass("member regular price");
		member.attr("total", task["member"]);
		member.text("$" + parseFloat(task["member"]).toFixed(2));
		quoteItemDiv.append(member);

		var memberAddOn = $("<div></div>");
		memberAddOn.addClass("member addon-price price");
		memberAddOn.attr("total", task["memberaddon"]);
		memberAddOn.text("$" + parseFloat(task["memberaddon"]).toFixed(2));
		quoteItemDiv.append(memberAddOn);

		var yourSavedAmount = (parseFloat(task["standard"]) - parseFloat(task["member"])).toFixed(2);
		var yourSaved = $("<div></div>");
		yourSaved.addClass("yoursaved regular price");
		yourSaved.attr("total", yourSavedAmount);
		yourSaved.text("$" + yourSavedAmount);
		quoteItemDiv.append(yourSaved);

		var yourSavedAddOnAmount = (parseFloat(task["standardaddon"]) - parseFloat(task["memberaddon"])).toFixed(2);
		var yourSavedAddOn = $("<div></div>");
		yourSavedAddOn.addClass("yoursaved addon-price price");
		yourSavedAddOn.attr("total", yourSavedAddOnAmount);
		yourSavedAddOn.text("$" + yourSavedAddOnAmount);
		quoteItemDiv.append(yourSavedAddOn);

		if(task["addon"] == "true") {
			standard.removeClass("show");
			member.removeClass("show");
			yourSaved.removeClass("show");

			standardAddOn.addClass("show");
			memberAddOn.addClass("show");
			yourSavedAddOn.addClass("show");
		}
		else {
			standard.addClass("show");
			member.addClass("show");
			yourSaved.addClass("show");

			standardAddOn.removeClass("show");
			memberAddOn.removeClass("show");
			yourSavedAddOn.removeClass("show");
		}

		if(task["active"] != "true") {
			standard.addClass("gray-text");
			member.addClass("gray-text");
			yourSaved.addClass("gray-text");
			standardAddOn.addClass("gray-text");
			memberAddOn.addClass("gray-text");
			yourSavedAddOn.addClass("gray-text");
		}
	}

//	var amount = $("<div></div>");
//	amount.attr("total", task["amount"]);
//	amount.addClass("amount");
//	amount.text("$" + parseFloat(task["amount"]).toFixed(2));
//	quoteItemDiv.append(amount);

	return quoteItemDiv;
}

function showQuote() {
	toggleMask();

	$("#quote").css("display", "block");
	$("#quote .quote-items").empty();

	selectQuery("SELECT task.name, task.description, task.standard, task.standardaddon, task.member, task.memberaddon, quoteitem.id, quoteitem.active, quoteitem.addon, quoteitem.inspectionarealineitem FROM quoteitem, task  WHERE quoteitem.inspection = ? AND quoteitem.task = task.number ORDER BY inspectionarealineitem", [$("#inspection-id").val()], function(tx, tasks) {
		if(tasks.rows.length > 0) {
			for(var i = 0; i < tasks.rows.length; i++) {
				var lineItemID = tasks.rows.item(i)["inspectionarealineitem"];
				var parentLine = $("#quote .quote-items .parent[data-id=" + lineItemID + "]");

				if(parentLine.length == 0) {
					parentLine = $("<div></div>");
					parentLine.attr("data-id", lineItemID);
					parentLine.addClass("parent");
					$("#quote .quote-items").append(parentLine);

					var parentText = $("<div></div>");
					parentText.addClass("parent-text");
					parentLine.append(parentText);

					var labelDiv = $("<div></div>");
					labelDiv.addClass("header-row blue-gradient");

					var standardLabel = $("<div></div>");
					standardLabel.addClass("standard header");
					standardLabel.text("Standard");
					labelDiv.append(standardLabel);

					var memberLabel = $("<div></div>");
					memberLabel.addClass("member header");
					memberLabel.text("Member");
					labelDiv.append(memberLabel);

					var yourSavedLabel = $("<div></div>");
					yourSavedLabel.addClass("yoursaved header");
					yourSavedLabel.text("You Saved");
					labelDiv.append(yourSavedLabel);

					parentLine.append(labelDiv);

					selectQuery("SELECT area.name as area, lineitem.name as lineitem, iali.id as id FROM area, lineitem, inspectionarealineitem iali WHERE lineitem.area = area.id AND lineitem.id = iali.lineitem AND iali.id = ?", [lineItemID], function(tx, results) {
						if(results.rows.length > 0) {
							var item = results.rows.item(0);
							$("#quote .parent[data-id=" + item["id"] + "] .parent-text").text(item["area"] + " > " + item["lineitem"]);
						}
						else {
							selectQuery("SELECT area.name as area, cli.name as lineitem, cli.id as id FROM area, inspectionarea, customlineitem cli WHERE cli.inspectionarea = inspectionarea.id AND inspectionarea.area = area.id AND cli.id = ?", [lineItemID], function(tx, results) {
								var item = results.rows.item(0);
								$("#quote .parent[data-id=" + item["id"] + "] .parent-text").text(item["area"] + " > " + item["lineitem"]);
							});
						}
					});
				}

				parentLine.append(getQuoteItem(tasks.rows.item(i), false));
			}
		}

		calculateQuoteTotal();
	});
}

function calculateQuoteTotal() {
	var activeChecks = $("#quote .active-check.pass");
	var standardTotal = 0.0;
	var memberTotal = 0.0;
	var yourSavedTotal = 0.0;

	var i = 0;
	while(activeChecks[i]) {
		standardTotal += parseFloat($(activeChecks[i]).parent(".quote-item").find(".standard.show").attr("total"));
		memberTotal += parseFloat($(activeChecks[i]).parent(".quote-item").find(".member.show").attr("total"));
		yourSavedTotal += parseFloat($(activeChecks[i]).parent(".quote-item").find(".yoursaved.show").attr("total"));
		i++;
	}

	var totalDiv = $("#quote .totals");
	totalDiv.empty();

	var parentText = $("<div></div>");
	parentText.addClass("parent-text");
	parentText.text("TOTAL");
	totalDiv.append(parentText);

	var headerRow = $("<div></div>");
	headerRow.addClass("header-row blue-gradient");

	var standardLabel = $("<div></div>");
	standardLabel.addClass("standard header");
	standardLabel.text("Standard");
	headerRow.append(standardLabel);

	var memberLabel = $("<div></div>");
	memberLabel.addClass("member header");
	memberLabel.text("Member");
	headerRow.append(memberLabel);

	var yourSavedLabel = $("<div></div>");
	yourSavedLabel.addClass("yoursaved header");
	yourSavedLabel.text("You Saved");
	headerRow.append(yourSavedLabel);

	totalDiv.append(headerRow);

	var sendQuoteDiv = $("<div></div>");
	sendQuoteDiv.addClass("send-quote");

	var sendQuote = $("<div></div>");
	sendQuote.addClass("send-quote-box blue-gradient");
	sendQuote.attr("id", "send-quote-box");
	sendQuote.html("&#x2713;");
	sendQuote.bind("click", function(e) {
		var checkedAttr = $("#send-quote").attr("checked");

		if(checkedAttr) {
			$("#send-quote-box").html("&nbsp;");
			$("#send-quote").removeAttr("checked");
		}
		else {
			$("#send-quote-box").html("&#x2713;");
			$("#send-quote").attr("checked", "checked");
		}

		updateRecord("inspection", {"sendquote": checkedAttr == null }, {"id": $("#inspection-id").val()});
	});

	sendQuoteDiv.append(sendQuote);

	var sendQuoteCheckBox = $("<input type='checkbox'>");
	sendQuoteCheckBox.attr("id", "send-quote");
	sendQuoteCheckBox.attr("name", "send-quote");
	sendQuoteCheckBox.attr("checked", "checked");
	sendQuoteCheckBox.css("display", "none");

	selectRecord("inspection", {"id": $("#inspection-id").val()}, function(tx, results) {
		if(results.rows.length > 0) {
			if("true" != results.rows.item(0)["sendquote"]) {
				$("#send-quote").removeAttr("checked");
				$("#send-quote-box").html("&nbsp;");
			}
		}
	});

	sendQuoteDiv.append(sendQuoteCheckBox);

	var sendQuoteLabel = $("<label for='send-quote'></label>");
	sendQuoteLabel.text("Send Quote");
	sendQuoteDiv.append(sendQuoteLabel);
	totalDiv.append(sendQuoteDiv);

	var standardTotalDiv = $("<div></div>");
	standardTotalDiv.addClass("standard total");
	standardTotalDiv.text("$" + standardTotal.toFixed(2));
	totalDiv.append(standardTotalDiv);

	var memberTotalDiv = $("<div></div>");
	memberTotalDiv.addClass("member total");
	memberTotalDiv.text("$" + memberTotal.toFixed(2));
	totalDiv.append(memberTotalDiv);

	var yourSavedTotalDiv = $("<div></div>");
	yourSavedTotalDiv.addClass("yoursaved total");
	yourSavedTotalDiv.text("$" + yourSavedTotal.toFixed(2));
	totalDiv.append(yourSavedTotalDiv);
}

function showSignature() {
	$("#signature-div").css("display", "block");
}

function saveSignature() {
	updateRecord("inspection", {"signature": $("#signature").jSignature("getData", "svg")[1]}, {"id": $("#inspection-id").val()});
}

function showEmailCustomerDialog() {
	$("#quote-sub-mask").css("display", "block");

	var emailDialog = $("#email-dialog");
	emailDialog.css("display", "block");
	var emailAddress = $("#add-email-address");
	emailAddress.focus();

	selectRecord("inspection", {"id": $("#inspection-id").val()}, function(tx, inspections) {
		if(inspections.rows.length > 0) {
			var emailAddresses = inspections.rows.item(0)["email"];
			$("#email-address").val(emailAddresses);
			showAllEmailAddresses(emailAddresses.split(","));
		}
	});

	if(isOnline) {
		emailDialog.find(".info-text").text("Customer(s) will receive email within 30 minutes.");
	}
	else {
		emailDialog.find(".info-text").text("Customer(s) will receive email within 30 minutes of connection being restored.");
	}
}

function showAllEmailAddresses(emailAddresses) {
	var exitingEmailsDiv = $("#existing-emails");
	exitingEmailsDiv.empty();
	checkIfOnline();

	for(var i = 0; i < emailAddresses.length; i++) {
		if(emailAddresses[i] != "") {
			var emailDiv = $("<div></div>");
			emailDiv.addClass("existing-email");

			var emailSpan = $("<span></span>");
			emailSpan.text(emailAddresses[i]);
			emailDiv.append(emailSpan);

			var deleteX = $("<div></div>");
			deleteX.addClass("delete-x fail");
			deleteX.text("X");
			deleteX.bind("click", function(e) {
				$(e.target).parent(".existing-email").remove();
				var newVal = "";

				$(".existing-email span").each(function() {
					if(newVal != "") {
						newVal += ",";
					}

					newVal += $(this).text();
				});

				$("#email-address").val(newVal);
			});
			emailDiv.append(deleteX);
			exitingEmailsDiv.append(emailDiv);
		}
	}
}

function enterNewEmail(e) {
	if(e.keyCode == 13) {
		addEmail();
	}
}

function addEmail() {
	var addNewEmailInput = $("#add-email-address");

	if(addNewEmailInput.val() != "") {
		var oldVal = $("#email-address").val();
		var newVal = (oldVal != '' ? oldVal + "," : "") + addNewEmailInput.val();
		$("#email-address").val(newVal);
		showAllEmailAddresses(newVal.split(","));

		updateRecord("inspection", {"email": newVal}, {"id": $("#inspection-id").val()});
	}

	addNewEmailInput.val("");
	addNewEmailInput.focus();
}

function reviewCustomerInfo() {
	$("#inspection").css("display", "none");
	$("#review").css("display", "none");
	$("#customer-info").css("display", "block");

	$("#start-inspection").text("Continue Inspection");
}

function showDeleteInspectionDialog() {
	toggleMask();

	var dialog = $("#delete-inspection-dialog");
	dialog.css("display", "block");
}

function showLogoutDialog() {
	toggleMask();

	var dialog = $("#logout-dialog");
	dialog.css("display", "block");
}

function showResetDialog() {
	toggleMask();

	var dialog = $("#reset-dialog");
	dialog.css("display", "block");
}

function deleteInspectionAndReload(id, shouldLogout) {
	if(shouldLogout) {
		logout();
	}

	deleteInspection(id, function(tx) {
		setTimeout(function() {
			window.location.reload();
		}, 1000);
	});
}

function reset(shouldLogout) {
	$("#area-name").text("Select an area by tapping the Add button");
	$("#line-item-list").empty();
	$("#areas").empty();

	$("#inspection").css("display", "none");
	$("#review").css("display", "none");

	$("#first-name").val("");
	$("#last-name").val("");
	$("#address").val("");
	$("#city").val("");
	$("#zip-code").val("");
	$("#phone-number").val("");
	$("#email").val("");

	$("#email-address").val("");
	$("#start-inspection").text("Start Inspection");

	if(!shouldLogout) {
		if(customerDataLoaded) {
			toggleMask();
		}

		$("#customer-info").css("display", "block");
		$("#first-name").focus();
	}
	else {
		toggleMask();

		logout();
	}
}

function logout() {
	$("#company-id").val("");
	$("#tech-id").val("");
	$("#tech-first-name").text("");
	window.localStorage["company-id"] = null;
	window.localStorage["tech-id"] = null;
	window.localStorage["tech-first-name"] = null;

	$("#username").val("");
	$("#password").val("");
	showLogin();
}

function deleteInspection(id, success) {
	var inspectionID = id != null ? id : $("#inspection-id").val();
	selectRecord("inspectionarea", {"inspection": inspectionID}, function(tx, inspectionAreas) {
		for(var i = 0; i < inspectionAreas.rows.length; i++) {
			var inspectionAreaID = inspectionAreas.rows.item(i)["id"];
			deleteRecord("inspectionarealineitem", {"inspectionarea": inspectionAreaID});
			deleteRecord("customlineitem", {"inspectionarea": inspectionAreaID});
		}
	});

	deleteRecord("inspectionarea", {"inspection": inspectionID});
	deleteRecord("inspection", {"id": inspectionID}, success);
}

function prepareSendInspection() {
	$(".dialog").css("display", "none");

	sendInspection(null, true);
}

function sendInspection(id, reload) {
	var inspectionID = id != null ? id : $("#inspection-id").val();

	if(isOnline) {
		selectRecord("inspection", {"id": inspectionID}, function(tx, inspections) {
			if(inspections.rows.length > 0) {
				var inspectionData = {};

				for(key in inspections.rows.item(0)) {
					inspectionData[key] = inspections.rows.item(0)[key];
				}

				inspectionData["email"] = $("#email-address").val();

				selectRecord("inspectionarea", {"inspection": inspectionID}, function(tx, areaResults) {
					if(areaResults.rows.length > 0) {
						var areas = {};
						var areaList = [];

						var areaIDs = [];
						for(var i = 0; i < areaResults.rows.length; i++) {
							var areaItem = {};

							for(key in areaResults.rows.item(i)) {
								areaItem[key] = areaResults.rows.item(i)[key];
							}

							areaIDs[areaIDs.length] = areaResults.rows.item(i)["id"];
							areaList[areaList.length] = areaItem;
						}

						areas["count"] = areaList.length;
						areas["list"] = areaList;
						inspectionData["inspectionareas"] = areas;

						selectRecord("inspectionarealineitem", {"inspectionarea": areaIDs}, function(tx, lineItemResults) {
							var lineItems = {};
							var lineItemList = [];

							if(lineItemResults.rows.length > 0) {
								for(var j = 0; j < lineItemResults.rows.length; j++) {
									var lineItem = {};
									for(key in lineItemResults.rows.item(j)) {
										lineItem[key] = lineItemResults.rows.item(j)[key];
									}

									lineItemList[lineItemList.length] = lineItem;
								}

								lineItems["count"] = lineItemList.length;
								lineItems["list"] = lineItemList;
								inspectionData["inspectionarealineitems"] = lineItems;

								selectRecord("customlineitem", {"inspectionarea": areaIDs}, function(tx, customItemResults) {
									if(customItemResults.rows.length > 0) {
										var customItems = {};
										var customItemList = [];

										for(var k = 0; k < customItemResults.rows.length; k++) {
											var customItem = {};

											for(key in customItemResults.rows.item(k)) {
												customItem[key] = customItemResults.rows.item(k)[key];
											}

											customItemList[customItemList.length] = customItem;
										}

										customItems["count"] = customItemList.length;
										customItems["list"] = customItemList;
										inspectionData["customlineitems"] = customItems;

									}

									selectRecord("quoteitem", {"inspection": inspectionID}, function(tx, quoteItemResults) {
										var length = quoteItemResults.rows.length;

										if(length > 0) {
											var quoteItems = {};
											var quoteItemList = [];

											for(var l = 0; l < length; l++) {
												var quoteItem = {};
												var item = quoteItemResults.rows.item(l);

												for(key in item) {
													quoteItem[key] = item[key];
												}

												quoteItemList[quoteItemList.length] = quoteItem;
											}

											quoteItems["count"] = quoteItemList.length;
											quoteItems["list"] = quoteItemList;
											inspectionData["quoteitems"] = quoteItems;
										}

										$.post("inspection", {"inspection": inspectionData, "key": getKey()}, function(result) {
											if($(result).find("success").text() == "true") {
												setNewKey($(result).find("key").text());
												finishSendingInspection();
												if(reload) {
													deleteInspectionAndReload(null, false);
												}
												else {
													deleteInspection(inspectionID);
												}
											}
											else {
												$("#loading").unbind("click", toggleMask);
												showMessage("You cannot continue at this time.");
											}
										}, "xml");
									});
								});
							}
							else {
								finishSendingInspection();
								deleteInspectionAndReload(inspectionID, false);
							}
						});
					}
					else {
						finishSendingInspection();
						deleteInspectionAndReload(inspectionID, false);
					}
				});
			}
			else {
				finishSendingInspection();
				deleteInspectionAndReload(inspectionID, false);
			}
		});
	}
	else {
		var emailAddress = $("#email-address");
		updateRecord("inspection", {"finished": "true", "email": emailAddress.val()}, {"id": inspectionID}, function() {
			toggleMask();
			window.location.reload();
		});
	}
}

function showTitleMenu(e, isMain) {
	var titleMenu = isMain ? $("#side-panel .title-menu") : $("#review-side-panel .title-menu");

	if(titleMenu.css("display") != "block") {
		var online = titleMenu.find(".online");

		if(isOnline) {
			online.text("You are online");
			online.removeClass("offline");
		}
		else {
			online.text("You are offline");
			online.addClass("offline");
		}

		titleMenu.css("display", "block");
		titleMenu.animate({ height: "202px"}, 100);

		$(document).bind("click", hideTitleMenu);
	}
	else {
		hideTitleMenu(e);
	}

	e.stopPropagation();
}

function hideTitleMenu(e) {
	var titleMenu = $(".title-menu");
	titleMenu.animate({ height: "0px"}, 100, function() {
		titleMenu.css("display", "none");
	});

	$(document).unbind("click", hideTitleMenu);
	e.stopPropagation();
}

function toggleHiddenListElement(e, id) {
	var element = $("#" + id);

	if(element) {
		if(element.css("display") != "block")
		{
			element.css("display", "block");
			$(document).bind("click", hideHiddenList);
			e.stopPropagation();
		}
		else {
			element.css("display", "none");
			$(document).unbind("click", hideHiddenList);
			e.stopPropagation();
		}
	}
}

function hideHiddenList(e) {
	if(!$(e.target).hasClass("hidden-list") || (e.target).parent(".hidden-list").length == 0) {
		$(".hidden-list").each(function() {
			$(this).css("display", "none");
		})
	}
}

function toggleMask() {
	var mask = $("#loading");

	if(mask.css("display") != "block") {
		mask.css("display", "block");
		$("#loading .loading-text").css("display", "none");

		mask.bind("click", toggleMask);
	}
	else {
		mask.css("display", "none");

		$(".dialog").each(function() {
			$(this).css("display", "none");
		});

		mask.unbind("click");
	}
}

function hideSubMask() {
	var subMask = $(".sub-mask");
	subMask.css("display", "none");
	$(".sub-dialog").css("display", "none");
}

function showMessage(text) {
	$("#message").css("display", "block");
	$("#message .loading-text").text(text);
}

function getNewID() {
	return Math.ceil(Math.random() * 1000000);
}

function getKey() {
	return window.localStorage["homesafetyinspectionkey"];
}

function setNewKey(key) {
	window.localStorage["homesafetyinspectionkey"] = key;
}

function validateKey(key) {
	if(key && key != "") {
		return true;
	}
	else {
		showMessage("You cannot continue at this time.");
		return false;
	}
}

function goToNextInput(e, areaID) {
	if(e.keyCode == 13) {
		var inputs = $("#" + areaID + " .line-item > .value > input");

		for(var i = 0; i < inputs.length; i++) {
			if(e.target == inputs[i]) {
				if(i < inputs.length - 1) {
					inputs[i+1].focus();
					break;
				}
				else {
					$("#" + areaID + " .line-item > .value > button").click();
				}
			}
		}
	}
}

function checkIfOnline() {
	if(!navigator.onLine) {
		isOnline = false;
	}

	$.ajax({
		url: "company-login.jsp",
		async: false,
		type: "HEAD",
		success: function(result) {
				isOnline = true;
			},
		error: function(result) {
				isOnline = false;
			}
		}
	);
}