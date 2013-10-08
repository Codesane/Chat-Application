

$(document).ready(function() {
	new Slider().init;
});

function Slider() {
	
	var centerOffsetPerSlide = 500;
	var topHeaderOffsetPerSlide = 34;
	var slideSpeed = 1000;
	var curSlideIndex = 1;
	
	var totalSlides = $("#about-slide-container").children().length;
	
	this.init = new function() {
		$("#about-slide-left-arrow").bind("click", function() {
			slideLeft();
		});
		$("#about-slide-right-arrow").bind("click", function() {
			slideRight();
		});
		/** Just adds it to everything. */
		addHeightModAllExc(0);
	}
	
	/** Quite inefficient, should be improved in the future. */
	function addHeightModAllExc(excIndex) {
		$.each($("#about-slide-container").children(), function(index, val) {
			if(index != excIndex) {
				$(val).addClass("about-heightmod");
			} else {
				$(val).removeClass("about-heightmod");
			}
		});
	}
	
	function slideLeft() {
		if(curSlideIndex <= 1) return;
		curSlideIndex -= 1;
		slide(-1);
	}
	
	function slideRight() {
		if(curSlideIndex >= totalSlides) return;
		curSlideIndex += 1;
		slide(1);
	}
	
	function slide(slideMod) {
		$("#about-slide-container").animate({
			left: "-=" + centerOffsetPerSlide * slideMod
		}, slideSpeed);
		$("#about-index-location-slider-background").animate({
			width: "+=" + topHeaderOffsetPerSlide * slideMod
		}, slideSpeed);
		addHeightModAllExc(curSlideIndex - 1);
	}
}