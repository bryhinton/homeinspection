var alteredAreas = {};
var alteredLineItems = {};
var companyInfo = {};
var timer;

$(document).ready(function(){
	$("#by-date-range").bind("mouseover", function(e) {
		toggleHiddenList("date-range");
	});

	$("#by-date-range").bind("mouseout", function(e) {
		toggleHiddenList("date-range");
	});

	$("#by-technician").bind("mouseover", function(e) {
		toggleHiddenList("technician-list");
	});

	$("#by-technician").bind("mouseout", function(e) {
		toggleHiddenList("technician-list");
	});

	$("#by-options").bind("mouseover", function(e) {
		toggleHiddenList("options");
	});

	$("#by-options").bind("mouseout", function(e) {
		toggleHiddenList("options");
	});

	$("#search-nav").bind("click", function(e) {
		toggleHiddenList("hidden-search");
	});

	$(".area-info > div > input[type='text']").bind("keyup", prepareAreaEdit);
	$(".area-info > div > input[type='checkbox']").bind("change", prepareAreaEdit);

	$(".parent > div > input[type='text']").bind("keyup", prepareSectionEdit);
	$(".parent > div > input[type='checkbox']").bind("change", prepareSectionEdit);

	$(".lineitem > input[type='text']").bind("keyup", prepareLineItemEdit);
	$(".lineitem > input[type='checkbox']").bind("change", prepareLineItemEdit);

	$("#company-name").bind("keyup", prepareCompanyInfoEdit);
	$("#company-email").bind("keyup", prepareCompanyInfoEdit);
	$("#company-phone").bind("keyup", prepareCompanyInfoEdit);
	$("#send-quote").bind("click", prepareCompanyInfoEdit);
});

function loadInspection(e, id) {
	var target = $(e.target);
	if(target.hasClass("delete-x") || target.hasClass("contacted") || target.parents(".contacted").length > 0) {
		return;
	}

	var thisInspection = $(".inspection-info[inspection-id=" + id + "]");

	if(thisInspection.length) {
		if(thisInspection.css("display") == "block") {
			thisInspection.css("display", "none");
		}
		else {
			$(".inspection-info").css("display", "none");
			thisInspection.css("display", "block");
		}
	}
	else {
		$(".inspection-info").css("display", "none");
		$.get("../inspection-info?inspectionid=" + id, function(xml) {
			var infoDiv = $("<div></div>");
			infoDiv.addClass("inspection-info");
			infoDiv.attr("inspection-id", id);

			var infoTable = $("<table></table>");
			var infoTableRow = $("<tr></tr>");

			var customerInfoCell = $("<td></td>");
			customerInfoCell.addClass("customer-info");

			var phone = $("<div></div>");
			phone.addClass("row phone");
			phone.text($(xml).find("Phone").text());
			customerInfoCell.append(phone);

			var email = $("<div></div>");
			var emailText = $(xml).find("Email").text();
			email.addClass("row email");
			email.html("<a href='mailto:" + emailText + "'>" + emailText + "</a>");
			customerInfoCell.append(email);

			var address1 = $("<div></div>");
			address1.addClass("row address");
			address1.text($(xml).find("Address").text());
			customerInfoCell.append(address1);

			var address2 = $("<div></div>");
			var city = $(xml).find("City").text();
			var state = $(xml).find("State").text();
			var zip = $(xml).find("ZIP").text();

			if(city != "" || state != "" || zip != "") {
				address2.addClass("row address");
				address2.text(city + (city != "" && state != "" ? ", " : "") + state + " " + zip);
				customerInfoCell.append(address2);
			}

			infoTableRow.append(customerInfoCell);

			var failingItemsCell = $("<td></td>");
			failingItemsCell.addClass("failing-items");

			var quoteTotal = $("<div></div>");
			quoteTotal.addClass("quote-total");

			var standardTotal = $("<div></div>");
			standardTotal.addClass("standard");
			standardTotal.text("STANDARD: " + $(xml).find("standardtotal").text());
			quoteTotal.append(standardTotal);

			var memberTotal = $("<div></div>");
			memberTotal.addClass("member");
			memberTotal.text("MEMBER: " + $(xml).find("membertotal").text());
			quoteTotal.append(memberTotal);

			var yourSaved = $("<div></div>");
			yourSaved.addClass("yoursaved");
			yourSaved.text("YOUR SAVED: " + $(xml).find("yoursavedtotal").text());
			quoteTotal.append(yourSaved);

			failingItemsCell.append(quoteTotal);

			$(xml).find("lineitem").each(function() {
				var lineItem = $("<div></div>");
				lineItem.addClass("lineitem");

				var redX = $("<span></span>");
				redX.addClass("red-x");
				redX.text("X");
				lineItem.append(redX);

				var parents = $("<span></span>");
				parents.text($(this).parents("area").children("name").text() + " > " + $(this).parents("parent").children("name").text() + " > " + $(this).children("name").text());
				lineItem.append(parents);

				var comment = $("<div></div>");
				comment.addClass("comment");
				comment.text($(this).children("comment").text());
				lineItem.append(comment);

				var quoteItems = $(this).find("quoteitem");

				if(quoteItems.length > 0) {
					var quoteItemList = $("<ul></ul>");
					quoteItemList.addClass("quote-item-list");

					quoteItems.each(function() {
						var quoteItem = $("<li></li>");
						quoteItem.addClass("quote-item");

						if($(this).find("active").text() != "true") {
							quoteItem.addClass("inactive");
						}

						var quoteName = $("<div></div>");
						quoteName.addClass("name");
						quoteName.text($(this).find("name").text());
						quoteItem.append(quoteName);

						var quoteDescription = $("<div></div>");
						quoteDescription.addClass("description");
						quoteDescription.text($(this).find("description").text());
						quoteItem.append(quoteDescription);

						var isAddon = "true" == $(this).find("addon").text();
						var addon = $("<div></div>");
						addon.addClass("addon");
						addon.text(isAddon ? "Add On" : "");
						quoteItem.append(addon);

						var prices = $("<div></div>");
						prices.addClass("prices");

						var standardPrice = parseFloat(isAddon ? $(this).find("StandardAddOn").text() : $(this).find("Standard").text());
						var standard = $("<div></div>");
						standard.addClass("standard");
						standard.text("STANDARD: $" + standardPrice.toFixed(2));
						prices.append(standard);

						var memberPrice = parseFloat(isAddon ? $(this).find("MemberAddOn").text() : $(this).find("Member").text());
						var member = $("<div></div>");
						member.addClass("member");
						member.text("MEMBER: $" + memberPrice.toFixed(2));
						prices.append(member);

						var yourSaved = $("<div></div>");
						yourSaved.addClass("yoursaved");
						yourSaved.text("YOUR SAVED: $" + (standardPrice - memberPrice).toFixed(2));
						prices.append(yourSaved);

						quoteItem.append(prices);

						quoteItemList.append(quoteItem);
					});

					lineItem.append(quoteItemList);
				}

				failingItemsCell.append(lineItem);
			});

			infoTableRow.append(failingItemsCell);
			infoTable.append(infoTableRow);
			infoDiv.append(infoTable);

			var signatureText = $(xml).find("Signature").text();

			if(signatureText != "" && signatureText != "null") {
				var signature = $("<div></div>");
				signature.addClass("signature");
				signature.append(signatureText);
				infoDiv.append(signature);
			}




			$(".inspection-row[inspection-id=" + id + "]").after(infoDiv);
		}, "xml");
	}
}

function toggleContact(id) {
	var checkBox = $("#contacted-" + id);
	var params = {};
	params["request"] = "contact";
	params["inspectionid"] = id;

	if(checkBox.attr("checked") == "checked") {
		params["contacted"] = "true";
		checkBox.parents(".inspection-row").addClass("row-contacted");
	}
	else {
		params["contacted"] = "false";
		checkBox.parents(".inspection-row").removeClass("row-contacted");
	}

	$.post("../inspection", params);
}

function deleteInspection(inspectionID) {
	if(confirm("Are you sure you want to delete this inspection?")) {
		$(".inspection-row[inspection-id=" + inspectionID + "]").remove();

		$.post("../inspection", {"request": "delete", "inspectionid": inspectionID});
	}
}

function deleteArea(id) {
	if(id instanceof String && id.indexOf("new") > -1) {
		removeArea(id);

		return;
	}

	var params = {};
	params["request"] = "delete";
	params["areaid"] = id;

	$.post("../edit-area", params, function(xml) {
		var success = $(xml).find("success").text();
		if("pending" == success) {
			if(confirm("This area has been used in some inspections. Are you sure you want to delete it?")) {
				params["force"] = "true";
				$.post("../edit-area", params, function(followUpXML) {
					if("true" == $(followUpXML).find("success").text()) {
						removeArea(id);
					}
				});
			}
		}
		else if("true" == success) {
			removeArea(id);
		}
	});
}

function removeArea(id) {
	$("#area-" + id).remove();
	$("#area-info-" + id).remove();
}

function deleteLineItem(id, isSection) {
	if(id instanceof String && id.indexOf("new") > -1) {
		if(isSection) {
			removeSection(id);
		}
		else {
			removeLineItem(id);
		}

		return;
	}

	var params = {};
	params["request"] = "delete";
	params["lineitemid"] = id;

	$.post("../edit-lineitem", params, function(xml) {
		var success = $(xml).find("success").text();
		if("pending" == success) {
			if(confirm("This " + (isSection ? "section" : "line item") + " has been used in some inspections. Are you sure you want to delete it?")) {
				params["force"] = "true";
				$.post("../edit-lineitem", params, function(followUpXML) {
					if("true" == $(followUpXML).find("success").text()) {
						if(isSection) {
							removeSection(id);
						}
						else {
							removeLineItem(id);
						}
					}
				});
			}
		}
		else if("true" == success) {
			if(isSection) {
				removeSection(id);
			}
			else {
				removeLineItem(id);
			}
		}
	});
}

function removeSection(id) {
	$("#parent-" + id).remove();
}

function removeLineItem(id) {
	$("#lineitem-" + id).remove();
}

function toggleHiddenList(listID) {
	var list = $("#" + listID);

	if(list.css("display") != "block") {
		list.css("display", "block");
	}
	else {
		list.css("display", "none");
	}
}

function search() {
	window.location = "inspections?search=" + $("#search").val();
}

function clearSearch() {
	$("#search").val("");
	window.location = "inspections";
}

function toggleTechnician(id) {
	var technicianInfo = $("#technician-info-" + id);

	if(technicianInfo.css("display") != "block") {
		$(".technician-info").css("display", "none");
		technicianInfo.css("display", "block");
		technicianInfo.animate({height:"75px"}, 100);
	}
	else {
		technicianInfo.animate({height: "0px"}, 100, function() {
			technicianInfo.css("display", "none");
		})
	}
}

function editTechnician(id) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["editid"] = id;
	params["firstname"] = $("#firstname-" + id).val();
	params["lastname"] = $("#lastname-" + id).val();
	params["username"] = $("#username-" + id).val();
	params["password"] = $("#password-" + id).val();
	params["active"] = $("#active-" + id).attr("checked");
	params["admin"] = $("#admin-" + id).attr("checked");

	if(!params["active"]) {
		params["active"] = "false";
	}

	if(!params["admin"]) {
		params["admin"] = "false";
	}

	$.post("../edit-technician", params, function(xml){
		var technicianInfo = $("#technician-info-" + id);
		var previousMessage = $("#technician-info-" + id + " .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			if("new" == id) {
				window.location = "settings";
			}
			else {
				$(".save-message").css("display", "block");
				setTimeout(hideSaveMessage, 1000);

				$("#technician-" + id + " .firstname").text($("#firstname-" + id).val());
				$("#technician-" + id + " .lastname").text($("#lastname-" + id).val());

				var isActive = $("#active-" + id).attr("checked") == "checked";
				var activeText = $("#technician-" + id + " .isactive");
				activeText.text(isActive ? "Active" : "Inactive");

				if(isActive) {
					activeText.removeClass("inactive");
					activeText.addClass("active");
				}
				else {
					activeText.removeClass("active");
					activeText.addClass("inactive");
				}
			}
		}
		else {
//			messageDiv.text("Technician cannot be updated at this time.");
//			messageDiv.addClass("fail");
		}
	}, "xml");
}

function editCompany() {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["companyid"] = $("#company-id").val();
	params["name"] = $("#company-name").val();
	params["email"] = $("#company-email").val();
	params["phone"] = $("#company-phone").val();
	params["sendquote"] = $("#send-quote").val();

	$.post("../edit-company", params, function(xml) {
		var companyInfo = $("#company-info");
		var previousMessage = $("#company-info .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);

			$("#page-subheader").text("Control Panel - " + $("#company-name").val());
		}
		else {
			//messageDiv.text("Company cannot be updated at this time.");
			//messageDiv.addClass("fail");
		}

	}, "xml");
}

function prepareAreaEdit(e) {
	var id = $(e.target).parents(".area-info").attr("data-id");
	alteredAreas[id] = id;
	clearTimeout(timer);
	timer = setTimeout(editAll, 1500);
}

function prepareSectionEdit(e) {
	var parentID = $(e.target).parents(".parent").attr("data-id");
	var params = {};
	params["id"] = parentID;
	params["parent"] = null;
	params["area"] = $(e.target).parents(".area-info").attr("data-id");
	alteredLineItems[parentID] = params;

	clearTimeout(timer);
	timer = setTimeout(editAll, 1500);
}

function prepareLineItemEdit(e) {
	var lineItemID = $(e.target).parents(".lineitem").attr("data-id");
	var params = {};
	params["id"] = lineItemID;
	params["parent"] = $(e.target).parents(".parent").attr("data-id");
	params["area"] = $(e.target).parents(".area-info").attr("data-id");
	alteredLineItems[lineItemID] = params;

	clearTimeout(timer);
	timer = setTimeout(editAll, 1500);
}

function prepareCompanyInfoEdit(e) {
	companyInfo["update"] = true;

	clearTimeout(timer);
	timer = setTimeout(editAll, 1500);
}

function editAll() {
	for(var area in alteredAreas) {
		editArea(alteredAreas[area]);
	}

	for(var lineItem in alteredLineItems) {
		editLineItem(alteredLineItems[lineItem]["id"], alteredLineItems[lineItem]["parent"], alteredLineItems[lineItem]["area"]);
	}

	if(companyInfo["update"] == true) {
		editCompany();
	}

	alteredAreas = {};
	alteredLineItems = {};
	companyInfo = {};
}

function hideSaveMessage() {
	$(".save-message").animate({opacity: "0.0"}, 1000, function() {
		var message = $(".save-message");
		message.css("display", "none");
		message.css("opacity", "1.0");
	});
}

function editArea(id) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["areaid"] = id;
	params["name"] = $("#area-name-" + id).val();

	if("new" == id) {
		params["active"] = "checked";
	}
	else {
		params["active"] = $("#area-active-" + id).attr("checked");

		if(!params["active"]) {
			params["active"] = false;
		}
	}

	$.post("../edit-area", params, function(xml) {
		if(id.indexOf("delete") > -1) {
			window.location = "settings";
			return;
		}

		if($(xml).find("success").text() == "true") {
			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);

			$("#area-name-text-" + id).text($("#area-name-" + id).val());

			if($(xml).find("newid").length > 0) {
				var newID = $(xml).find("newid").text();

				$("#area-" + id).attr("id", "area-" + newID);
				$("#area-name-" + id).attr("id", "area-name-" + newID);
				$("#area-name-text-" + id).attr("id", "area-name-text" + newID);
				$("#area-active-" + id).attr("id", "area-active-" + newID);
				$("#area-info-" + id).attr("data-id", newID);
				$("#area-info-" + id).attr("id", "area-info-" + newID);
				$("#area-" + newID).find(".delete-x").unbind();
				$("#area-" + newID).find(".delete-x").bind("click", function(e) {
					deleteArea(newID);
				});

				$("#area-info-" + newID).prev().unbind();
				$("#area-info-" + newID).prev().bind("click", function(e) {
					toggleHiddenList("area-info-" + newID);
				});

				addParentButton(newID);
			}
		}
		else {
			//messageDiv.text("Area cannot be updated at this time.");
			//messageDiv.addClass("fail");
		}
	}, "xml");
}

function editLineItem(id, parent, area) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["lineitemid"] = id;
	params["name"] = $("#lineitem-name-" + (id == "new" ? parent + "-" : "") + id).val();
	params["parentid"] = parent;
	params["areaid"] = area;

	if(id.indexOf("new-") > -1) {
		params["active"] = "checked";
	}
	else {
		params["active"] = $("#lineitem-active-" + id).attr("checked");

		if(!params["active"]) {
			params["active"] = false;
		}
	}

	$.post("../edit-lineitem", params, function(xml) {
		if($(xml).find("success").text() == "true") {
			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);

			if($(xml).find("newid").length > 0) {
				var newID = $(xml).find("newid").text();

				$("#lineitem-" + id).attr("id", "lineitem-" + newID);
				$("#lineitem-name-" + id).attr("id", "lineitem-name-" + newID);
				$("#lineitem-active-" + id).attr("id", "lineitem-active-" + newID);
				if(parent == null) {
					$("#lineitem-" + newID).parent(".parent").attr("data-id", newID);
					$("#lineitem-" + newID).find(".delete-x").unbind();
					$("#lineitem-" + newID).find(".delete-x").bind("click", function(e) {
						deleteLineItem(newID, true);
					});
				}
				else {
					$("#lineitem-" + newID).attr("data-id", newID);
					$("#lineitem-" + newID).find(".delete-x").unbind();
					$("#lineitem-" + newID).find(".delete-x").bind("click", function(e) {
						deleteLineItem(newID, false);
					});
				}

				if(parent == null) {
					addLineItemsSection(newID, area);
				}
			}
		}
		else {
			//messageDiv.text((parent == null ? "Parent" : "Line item") + " cannot be updated at this time.");
			//messageDiv.addClass("fail");
		}
	}, "xml");
}

function addLineItem(parentID, areaID) {
	var addButton = $("#add-lineitem-" + parentID).parent(".lineitem");

	var fakeID = Math.ceil(Math.random() * 1000000);

	var newLineItemDiv = $("<div></div>");
	newLineItemDiv.attr("id", "lineitem-new-" + fakeID);
	newLineItemDiv.attr("data-id", "new-" + fakeID);
	newLineItemDiv.addClass("lineitem row no-hover");

	var nameLabel = $("<label></label>");
	nameLabel.text("Name:");
	newLineItemDiv.append(nameLabel);

	var nameInput = $("<input type='text'>");
	nameInput.attr("id", "lineitem-name-new-" + fakeID);
	nameInput.bind("keyup", prepareLineItemEdit);
	newLineItemDiv.append(nameInput);

	var checkboxLabel = $("<label></label>");
	checkboxLabel.text("Active:");
	newLineItemDiv.append(checkboxLabel);

	var activeCheckbox = $("<input type='checkbox'>");
	activeCheckbox.attr("id", "lineitem-active-new-" + fakeID);
	activeCheckbox.attr("checked", "checked");
	activeCheckbox.bind("change", prepareLineItemEdit);
	newLineItemDiv.append(activeCheckbox);

	var deleteX = $("<div></div>");
	deleteX.addClass("delete-x");
	deleteX.html("&#x2716;");
	deleteX.bind("click", function(e) {
		deleteLineItem("new-" + fakeID, false);
	});
	newLineItemDiv.append(deleteX);

	addButton.before(newLineItemDiv);
}

function addParent(areaID) {
	var addParentSection = $("#add-parent-" + areaID).parent().prev();
	var fakeID = Math.ceil(Math.random() * 1000000);

	var parentDiv = $("<div></div>");
	parentDiv.attr("id", "parent-new-" + fakeID);

	var subheaderDiv = $("<div></div>");
	subheaderDiv.addClass("subheader");
	subheaderDiv.text("Section");

	parentDiv.append(subheaderDiv);

	var parentOuterDiv = $("<div></div>");
	parentOuterDiv.addClass("parent");
	parentOuterDiv.attr("data-id", "new-" + fakeID);

	var newParentDiv = $("<div></div>");
	newParentDiv.attr("id", "lineitem-new-" + fakeID);

	var nameLabel = $("<label></label>");
	nameLabel.text("Name:");
	newParentDiv.append(nameLabel);

	var nameInput = $("<input type='text'>");
	nameInput.attr("id", "lineitem-name-new-" + fakeID);
	nameInput.bind("keyup", prepareSectionEdit);
	newParentDiv.append(nameInput);

	var checkboxLabel = $("<label></label>");
	checkboxLabel.text("Active:");
	newParentDiv.append(checkboxLabel);

	var activeCheckbox = $("<input type='checkbox'>");
	activeCheckbox.attr("id", "lineitem-active-new-" + fakeID);
	activeCheckbox.attr("checked", "checked");
	activeCheckbox.bind("change", prepareSectionEdit);
	newParentDiv.append(activeCheckbox);

	var deleteX = $("<div></div>");
	deleteX.addClass("delete-x");
	deleteX.html("&#x2716;");
	deleteX.bind("click", function(e) {
		deleteLineItem("new-" + fakeID, true);
	});
	newParentDiv.append(deleteX);

	parentOuterDiv.append(newParentDiv);
	parentDiv.append(parentOuterDiv);

	addParentSection.before(parentDiv);
}

function addParentButton(area) {
	var areaInfoDiv = $("#area-info-" + area);

	var subheaderDiv = $("<div></div>");
	subheaderDiv.addClass("subheader");
	areaInfoDiv.append(subheaderDiv);

	var parentDiv = $("<div></div>");
	parentDiv.addClass("parent");

	var addButton = $("<button type='button'></button>");
	addButton.text("Add Section...");
	addButton.attr("id", "add-parent-" + area);
	addButton.bind("click", function(e) {
		addParent(area);
	});

	parentDiv.append(addButton);
	areaInfoDiv.append(parentDiv);
}

function addLineItemsSection(id, area) {
	var parentDiv = $("#lineitem-" + id).parent();

	var lineItemsSubheaderDiv = $("<div></div>");
	lineItemsSubheaderDiv.addClass("subheader");
	lineItemsSubheaderDiv.text("Line Items");

	parentDiv.append(lineItemsSubheaderDiv);

	var addButtonDiv = $("<div></div>");
	addButtonDiv.addClass("lineitem row no-hover");
	addButtonDiv.attr("id", "lineitem-" + id + "-new");

	var addLineItemButton = $("<button type='button'></button>");
	addLineItemButton.attr("id", "add-lineitem-" + id);
	addLineItemButton.text("Add Line Item...");
	addLineItemButton.bind("click", function(e){
		addLineItem(id, area);
	});

	addButtonDiv.append(addLineItemButton);

	parentDiv.append(addButtonDiv);
}

function addArea() {
	$(".area-info").css("display", "none");

	var areasButtonDiv = $("#area-new");
	var fakeID =  Math.ceil(Math.random() * 1000000);

	var newAreaDiv = $("<div></div>");
	newAreaDiv.addClass("area row");
	newAreaDiv.attr("id", "area-new-" + fakeID);
	newAreaDiv.bind("click", function(e){
		toggleHiddenList("area-info-new-" + fakeID);
	});

	var areaNameText = $("<div></div>");
	areaNameText.addClass("cell");
	areaNameText.attr("id", "area-name-text-new-" + fakeID);
	areaNameText.text("New Area");

	newAreaDiv.append(areaNameText);

	var deleteX = $("<div></div>");
	deleteX.addClass("delete-x");
	deleteX.html("&#x2716;");
	deleteX.bind("click", function(e) {
		deleteArea("new-" + fakeID);
	});
	newAreaDiv.append(deleteX);

	areasButtonDiv.before(newAreaDiv);

	var areaInfoDiv = $("<div></div>");
	areaInfoDiv.addClass("area-info");
	areaInfoDiv.attr("id", "area-info-new-" + fakeID);
	areaInfoDiv.attr("data-id", "new-" + fakeID);
	areaInfoDiv.css("display", "block");

	var areaNameLabel = $("<label></label>");
	areaNameLabel.text("Name:");
	areaInfoDiv.append(areaNameLabel);

	var areaName = $("<input type='text'>");
	areaName.attr("id", "area-name-new-" + fakeID);
	areaName.bind("keyup", prepareAreaEdit);
	areaInfoDiv.append(areaName);

	var checkboxLabel = $("<label></label>");
	checkboxLabel.text("Active:");
	areaInfoDiv.append(checkboxLabel);

	var activeCheckbox = $("<input type='checkbox'>");
	activeCheckbox.attr("id", "area-active-new-" + fakeID);
	activeCheckbox.attr("checked", "checked");
	activeCheckbox.bind("change", prepareAreaEdit);
	areaInfoDiv.append(activeCheckbox);

	areasButtonDiv.before(areaInfoDiv);
}

function toggleField(id) {
	var fieldInfo = $("#field-info-" + id);

	if(fieldInfo.css("display") != "block") {
		$(".field-info").css("display", "none");
		fieldInfo.css("display", "block");
		fieldInfo.animate({height:"145px"}, 100);
	}
	else {
		fieldInfo.animate({height: "0px"}, 100, function() {
			fieldInfo.css("display", "none");
		})
	}
}

function editField(id) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["editid"] = id;
	params["name"] = $("#field-name-" + id).val();
	params["type"] = $("#field-type-" + id).val();
	params["required"] = $("#field-required-" + id).attr("checked");
	params["enabled"] = $("#field-active-" + id).attr("checked");
	params["sum"] = $("#field-sum-" + id).attr("checked");

	if(!params["required"]) {
		params["required"] = "false";
	}

	if(!params["enabled"]) {
		params["enabled"] = "false";
	}

	if(!params["sum"]) {
		params["sum"] = "false";
	}

	$.post("../edit-field", params, function(xml){
		var fieldInfo = $("#field-info-" + id);
		var previousMessage = $("#field-info-" + id + " .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			if("new" == id) {
				window.location = "settings";
			}
			else {
				$(".save-message").css("display", "block");
				setTimeout(hideSaveMessage, 1000);

				$("#field-" + id + " .field-name").text($("#field-name-" + id).val());

				var isActive = $("#field-active-" + id).attr("checked") == "checked";
				var activeText = $("#field-" + id + " .isactive");
				activeText.text(isActive ? "Active" : "Inactive");

				if(isActive) {
					activeText.removeClass("inactive");
					activeText.addClass("active");
				}
				else {
					activeText.removeClass("active");
					activeText.addClass("inactive");
				}
			}
		}
		else {
//			messageDiv.text("Technician cannot be updated at this time.");
//			messageDiv.addClass("fail");
		}
	}, "xml");
}

function resetLicense(id) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["licenseid"] = id;
	params["action"] = "reset";

	$.post("edit-company-license", params, function(xml) {
		var licenseRow = $("#license-" + id);
		var previousMessage = $("#license-" + id + " .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);

			var techName = licenseRow.find(".techname");
			techName.removeClass("techname");
			techName.addClass("unused");
			techName.text("Unused");
		}
		else {
			//messageDiv.text("License cannot be reset at this time.");
			//messageDiv.addClass("fail");
		}

		//licenseRow.append(messageDiv);
	}, "xml");
}

function deleteLicense(id) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["licenseid"] = id;
	params["action"] = "delete";

	$.post("edit-company-license", params, function(xml) {
		var licenseRow = $("#license-" + id);
		var previousMessage = $("#license-" + id + " .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);
			licenseRow.css("display", "none");
		}
		else {
			//messageDiv.text("License cannot be reset at this time.");
			//messageDiv.addClass("fail");
			//licenseRow.append(messageDiv);
		}

	}, "xml");
}

function addLicense(company) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["licenseid"] = "new";
	params["companyid"] = company;

	$.post("edit-company-license", params, function(xml) {
		var licenseRow = $("#license-new-" + company);
		var previousMessage = $("#license-new-" + company + " .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			var newID = $(xml).find("newid").text();

			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);

			var companyInfo = $("#company-info-" + company);

			licenseRow = $("<div></div>");
			licenseRow.attr("id", "license-" + newID);
			licenseRow.addClass("license row no-hover");

			var number = $("<div></div>");
			number.addClass("cell number");
			number.text(companyInfo.find(".license").length + ".");
			licenseRow.append(number);

			var unused = $("<div></div>");
			unused.addClass("cell unused");
			unused.text("Unused");
			licenseRow.append(unused);

			var resetButton = $("<button type='button'></button>");
			resetButton.text("Reset");
			resetButton.bind("click", function(e) {
				resetLicense(newID);
			});

			licenseRow.append(resetButton);

			var deleteButton = $("<button type='button'></button>");
			deleteButton.text("Delete");
			deleteButton.bind("click", function(e) {
				deleteLicense(newID);
			});

			licenseRow.append(deleteButton);

			$("#license-new-" + company).before(licenseRow);
		}
		else {
			//messageDiv.text("License cannot be reset at this time.");
			//messageDiv.addClass("fail");
		}

	}, "xml");
}

function addCompany() {
	var companiesButtonDiv = $("#company-new");
	var fakeID =  Math.ceil(Math.random() * 1000000);

	var newCompanyDiv = $("<div></div>");
	newCompanyDiv.addClass("company row");
	newCompanyDiv.bind("click", function(e){
		toggleHiddenList("company-info-new-" + fakeID);
	});

	var companyNameText = $("<div></div>");
	companyNameText.addClass("cell");
	companyNameText.attr("id", "company-name-text-new-" + fakeID);
	companyNameText.text("New Company");

	newCompanyDiv.append(companyNameText);

	var activeDiv = $("<div></div>");
	activeDiv.addClass("cell isactive active");
	activeDiv.attr("id", "company-active-text-new-" + fakeID);
	activeDiv.text("Active");
	newCompanyDiv.append(activeDiv);

	companiesButtonDiv.before(newCompanyDiv);

	var companyInfoDiv = $("<div></div>");
	companyInfoDiv.addClass("company-info section");
	companyInfoDiv.attr("id", "company-info-new-" + fakeID);
	companyInfoDiv.css("display", "block");

	var companyNameLabel = $("<label></label>");
	companyNameLabel.text("Name:");
	companyInfoDiv.append(companyNameLabel);

	var companyName = $("<input type='text'>");
	companyName.attr("id", "company-name-new-" + fakeID);
	companyInfoDiv.append(companyName);

	var checkboxLabel = $("<label></label>");
	checkboxLabel.text("Active:");
	companyInfoDiv.append(checkboxLabel);

	var activeCheckbox = $("<input type='checkbox'>");
	activeCheckbox.attr("id", "company-active-new-" + fakeID);
	activeCheckbox.attr("checked", "checked");
	companyInfoDiv.append(activeCheckbox);

	var saveButton = $("<button type='button'></button>");
	saveButton.addClass("company-save-button");
	saveButton.text("Save");
	saveButton.bind("click", function(e) {
		editCompanyFull("new-" + fakeID);
	});

	companyInfoDiv.append(saveButton);

	companiesButtonDiv.before(companyInfoDiv);
}

function editCompanyFull(id) {
	var params = {};
	params["techid"] = $("#tech-id").val();
	params["companyid"] = id;
	params["name"] = $("#company-name-" + id).val();
	params["trial"] = $("#company-trial-" + id).val();

	if("new" == id) {
		params["name"] = $("#new-company-name").val();
		params["active"] = "checked";
		params["seed"] = $("#company-seed").val();
		params["admin-firstname"] = $("#admin-firstname").val();
		params["admin-lastname"] = $("#admin-lastname").val();
		params["admin-username"] = $("#admin-username").val();
		params["admin-password"] = $("#admin-password").val();
		params["new-devices"] = $("#new-devices").val();
	}
	else {
		params["active"] = $("#company-active-" + id).attr("checked");

		if(!params["active"]) {
			params["active"] = false;
		}
	}

	$.post("edit-company", params, function(xml) {
		var companyInfo = $("#company-" + id);
		var previousMessage = $("#company-info-" + id + " .message");
		previousMessage.css("display", "none");

		if($(xml).find("success").text() == "true") {
			//hideDialog();
			$(".save-message").css("display", "block");
			setTimeout(hideSaveMessage, 1000);

			$("#company-name-text-" + id).text($("#company-name-" + id).val());

			if($(xml).find("newid").length > 0) {
				var newID = $(xml).find("newid").text();

				if(newID && newID != '') {
					window.location = "admin?save=true";
				}

//				$("#company-" + id).attr("id", "company-" + newID);
//				$("#company-name-" + id).attr("id", "company-name-" + newID);
//				$("#company-name-text-" + id).attr("id", "company-name-text-" + newID);
//				$("#company-active-" + id).attr("id", "company-active-" + newID);
//				$("#company-active-text-" + id).attr("id", "company-active-text-" + newID);
//				$("#company-info-" + id).attr("id", "company-info-" + newID);
//				$("#company-" + newID).find("company-save-button").unbind();
//				$("#company-" + newID).find("company-save-button").bind("click", function(e) {
//					editCompanyFull(newID);
//				});
//
//				$("#company-info-" + newID).prev().unbind();
//				$("#company-info-" + newID).prev().bind("click", function(e) {
//					toggleHiddenList("company-info-" + newID);
//				});
//
//				addDevicesSection(newID);
//
//				id = newID;
			}

			var isActive = $("#company-active-" + id).attr("checked") == "checked";
			var activeText = $("#company-active-text-" + id);
			activeText.text(isActive ? "Active" : "Inactive");

			if(isActive) {
				activeText.removeClass("inactive");
				activeText.addClass("active");
			}
			else {
				activeText.removeClass("active");
				activeText.addClass("inactive");
			}
		}
		else {
			//messageDiv.text("Company cannot be updated at this time.");
			//messageDiv.addClass("fail");
		}
	}, "xml");
}

function addDevicesSection(company) {
	var companyInfoDiv = $("#company-info-" + company);

	var deviceSubheader = $("<div></div>");
	deviceSubheader.addClass("subheader");
	deviceSubheader.text("Devices");

	companyInfoDiv.append(deviceSubheader);

	var addButtonDiv = $("<div></div>");
	addButtonDiv.addClass("license");
	addButtonDiv.attr("id", "license-new-" + company);

	var addLicenseButton = $("<button type='button'></button>");
	addLicenseButton.attr("id", "add-license-" + company);
	addLicenseButton.text("Add Device...");
	addLicenseButton.bind("click", function(e){
		addLicense(company);
	});

	addButtonDiv.append(addLicenseButton);

	companyInfoDiv.append(addButtonDiv);
}

function openDialog(id) {
	toggleMask();

	$("#" + id).css("display", "block");
}

function hideDialog() {
	$(".dialog").css("display", "none");

	toggleMask();
}

function toggleMask() {
	var mask = $("#mask");

	if(mask.css("display") != "block") {
		mask.css("display", "block");
	}
	else {
		mask.css("display", "none");
	}
}