$(document).ready(function(e) {
	var companyID;

	$.ajax({
		type: "GET",
		url: "api/inspection/172",
		async: false,
		datatype: "json"
	}).done(function(msg) {
			debugger;
		});

	$.ajax({
		type: "POST",
		url: "api/company/",
		async: false,
		datatype: "json",
		data: {
			name: "API TEST",
			phone: "888-888-8888",
			email: "abc@test.com",
			trialExpiration: "2013-10-11 00:00:00",
			isActive: true,
			sendQuote: false
		}
	}).done(function(msg) {
			companyID = msg.id;
			$("body").append("passed");
		});

	$.ajax({
		type: "POST",
		url: "api/company/" + companyID,
		async: false,
		datatype: "json",
		data: {
			method: "PUT",
			name: "API TEST 1",
			phone: "777-777-7777",
			email: "abc@test.com",
			trialExpiration: "2013-12-11 00:00:00",
			isActive: false,
			sendQuote: true
		}
	}).done(function(msg) {
			$("body").append("passed");
		});

	$.ajax({
		type: "DELETE",
		url: "api/company/" + companyID,
		async: false,
		datatype: "json"
	}).done(function(msg) {
			$("body").append("passed");
		});
});