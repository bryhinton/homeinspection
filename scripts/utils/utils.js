function toggleElement(id) {
	var element = $("#" + id);

	if(element) {
		if(element.css("display") != "block")
		{
			element.css("display", "block");
		}
		else {
			element.css("display", "none");
		}
	}
}

$.fn.animateRotate = function(angle, start, duration, easing, complete) {
	return this.each(function() {
		var $elem = $(this);

		$({deg: start}).animate({deg: angle}, {
			duration: duration,
			easing: easing,
			step: function(now) {
				$elem.css({
					transform: 'rotate(' + now + 'deg)'
				});
			},
			complete: complete || $.noop
		});
	});
};

$('#MyDiv2').animateRotate(90);