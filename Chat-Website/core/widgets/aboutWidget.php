<?php
	$defaultPath = "./core/widgets/about_slides/";
?>
<div id="about-slider-arrows-container">
	<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
	<div id="about-slide-left-arrow" class="about-arrow-common"></div>
	<div id="about-index-location-container">
		<div id="about-index-slider-container">
			<div id="about-index-location-slider-background"></div>
			<div id="about-index-location-slider-frame"></div>
		</div>
	</div>
	<div id="about-slide-right-arrow" class="about-arrow-common"></div>
	<div class="clearfix"></div>
</div>
<div id="about-slide-wrapper">
	<div id="about-slide-container">
		<?php
			include $defaultPath . "about_intro.html";
			include $defaultPath . "about_chat.html";
			include $defaultPath . "about_client.html";
			include $defaultPath . "about_server.html";
			include $defaultPath . "about_database.html";
			include $defaultPath . "about_integration.html";
			include $defaultPath . "summary.html";
		?>
	</div>
</div>