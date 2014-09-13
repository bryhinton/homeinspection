$(document).ready(function() {
	var timeCard = new TimeCard();
});

function TimeCard() {
	var time = null;
	var breakTime = null;
	var interval = null;

	this.initialize();
}

TimeCard.prototype.initialize = function() {
	if(localStorage.time) {
		this.time = new Date(parseInt(localStorage.time));

		if(localStorage.breakStart) {
			$("#clock-in").css("display", "none");
			$("#clock-out").css("display", "none");
			$("#break").css("display", "none");
		}
		else {
			$("#clock-in").css("display", "none");
			$("#break-back").css("display", "none");

			this.startTimeCount();
		}
	}
	else {
		$("#clock-out").css("display", "none");
		$("#break").css("display", "none");
		$("#break-back").css("display", "none");
	}

	$("#clock-in").bind("click", this.handleClockIn.bind(this));
	$("#clock-out").bind("click", this.handleClockOut.bind(this));
	$("#break").bind("click", this.handleBreak.bind(this));
	$("#break-back").bind("click", this.handleBreakBack.bind(this));
};

TimeCard.prototype.handleClockIn = function() {
	this.time = new Date();
	localStorage.time = this.time.getTime();

	$("#clock-in").css("display", "none");
	$("#clock-out").css("display", "inline-block");
	$("#break").css("display", "inline-block");
	$("#time-count").css("display", "block");
	$("#break-back").css("display", "none");

	this.startTimeCount();
};

TimeCard.prototype.handleClockOut = function() {
	this.showExtraFields();
//	localStorage.removeItem("time");
//	localStorage.removeItem("breakTime");
//	localStorage.removeItem("breakStart");
//	this.time = null;
//	clearInterval(this.interval);
//
//	$("#clock-in").css("display", "inline-block");
//	$("#clock-out").css("display", "none");
//	$("#break").css("display", "none");
//	$("#break-back").css("display", "none");
//	$("#time-count").css("display", "none");
};

TimeCard.prototype.handleBreak = function () {
	if(!this.breakStart) {
		this.breakStart = new Date();
		localStorage.breakStart = this.breakStart.getTime();
		clearInterval(this.interval);

		$("#break").css("display", "none");
		$("#clock-out").css("display", "none");
		$("#clock-in").css("display", "none");
		$("#time-count").css("display", "none");
		$("#break-back").css("display", "inline-block");
	}
};

TimeCard.prototype.handleBreakBack = function () {
	if(this.breakStart) {
		this.breakTime = new Date(Date.now() - this.breakStart.getTime() + (localStorage.breakTime ? parseInt(localStorage.breakTime) : 0));
		localStorage.breakTime = this.breakTime.getTime();
		localStorage.time = this.time.getTime();
		localStorage.removeItem("breakStart");
		this.breakStart = null;

		this.startTimeCount();

		$("#break").css("display", "inline-block");
		$("#clock-out").css("display", "inline-block");
		$("#time-count").css("display", "block");
		$("#clock-in").css("display", "none");
		$("#break-back").css("display", "none");
	}
};

TimeCard.prototype.startTimeCount = function () {
	this.interval = setInterval(function() {
		var dateDiff = Date.now() - this.time.getTime() - (localStorage.breakTime ? parseInt(localStorage.breakTime) : 0);
		var totalTime = new Date(dateDiff);
		var days = totalTime.getUTCDate() - 1;
		var hours = totalTime.getUTCHours();
		var minutes = totalTime.getMinutes();
		var seconds = totalTime.getSeconds();

		var text = "";

		if(hours > 0) {
			if(days > 0) {
				text += days + ":";

				if(hours < 10) {
					text += "0";
				}
			}

			text += hours + ":";
		}

		if(minutes < 10) {
			text += "0";
		}

		text += minutes + ":";

		if(seconds < 10) {
			text += "0";
		}

		text += seconds;

		$("#time-count").text(text);
	}.bind(this), 1000);
};

TimeCard.prototype.showExtraFields = function() {
	$.get("field-info?key=abc&companyid=" + localStorage["company-id"], function(data) {
		debugger;
	}, "json");
};