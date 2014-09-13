function Pushable(url) {
	this.url = url;
	this.pushed = false;
	this.result = null;
}

Pushable.prototype.push = function() {
	if(!this.pushed && navigator.onLine) {
		var fullURL = this.url;
		var pushable = this;
		
		this.result = $.ajax({
			url: this.url,
			async: false,
			data: this.getParameters(),
			error: this.onError,
			success: this.onSuccess,
			dataType: "text"
		});
	}
}

Pushable.prototype.getParameters = function() {
	//throw "Pushable.getParameters should never be called directly. This method should be overwritten by a child class";
	return "a=b";
}

Pushable.prototype.onSuccess = function(data) {
	this.pushed = true;
	console.log("SUCCESS!")
}

Pushable.prototype.onError = function(jqXHR, message, error) {
	this.pushed = false;
	console.log("PUSH FAILED: " + this.url + "?" + this.getParameters());
}

// TEST METHODS

function testPushable() {
	var pushable = new ChildPushable("pushable-test");
	pushable.push();
}

function ChildPushable(url) {
	Pushable.call(this, url);
}

ChildPushable.prototype = new Pushable();
ChildPushable.prototype.constructor = ChildPushable;

ChildPushable.prototype.getParameters = function() {
	return "c=d";
}

ChildPushable.prototype.onSuccess = function(data) {
	this.pushed = true;
	console.log("Child Success!");
}