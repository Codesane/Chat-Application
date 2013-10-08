

<?php
	/* The first value in the array inside of the main array defines;
	   First value: Key,
	   Second value is the Title,
	   Third value is the location of the body document.
	   Fourth is the footer location.
	*/
	$displaySet = array(
		array("register", "Register", "core/widgets/createAccount.php", "core/widgets/createAccountFooter.php"),
		array("admin", "Admin", "admin/admin.php", "admin/adminFooter.php"),
		array("dlclient", "Download Client", "core/widgets/downloadWidget.php", "admin/adminFooter.php"),
		array("about", "About", "core/widgets/aboutWidget.php", "admin/adminFooter.php")
	);
	
	/* Will display the first row of the array by default. That is the create account site. */
	$displayIndex = 0;
	
	if(isset($_GET['q'])) {
		$qVal = $_GET['q'];
		for($i = 0; $i < count($displaySet); $i++) {
			if($qVal == $displaySet[$i][0]) {
				$displayIndex = $i;
			}
		}
	}
?>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>Basic Chat Application</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

        <link rel="stylesheet" href="css/normalize.css">
		<link rel="stylesheet" href="css/main.css">
		<link rel="stylesheet" href="css/component_styles.css" />
        <script src="js/vendor/modernizr-2.6.2.min.js"></script>
    </head>
    <body>
        <!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

        <!-- Add your site or application content here -->
		
		<div id="app-header-title">
			CChat Alpha 1.0.0.
		</div>
        <div id="create-account" class="comp-mid-display-header">
			<div id="create-account-header" class="comp-mid-display-header-menu">
				<div class="comp-mid-display-header-menu-title">
					<?php
						echo $displaySet[$displayIndex][1];
					?>
				</div>
			</div>
			<div id="create-account-widget">
				<?php
					require $displaySet[$displayIndex][2];
				?>
			</div>
			<div class="comp-mid-display-footer">
				<div class="comp-mid-display-footer-item">
					<?php
						require $displaySet[$displayIndex][3];
					?>
				</div>
			</div>
		</div>

        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script>window.jQuery || document.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')</script>
        <script src="js/plugins.js"></script>
        <script src="js/main.js"></script>
		<?php
			if($displaySet[$displayIndex][0] == "register") {
				?>
					<script src="js/regscript.js"></script>
				<?php
			} else if($displaySet[$displayIndex][0] == "admin") {
				?>
					<script src="js/loginscript.js"></script>
				<?php
			} else if($displaySet[$displayIndex][0] == "about") {
				?>
					<script src="js/aboutscript.js"></script>
				<?php
			}
		?>

        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <script>
            var _gaq=[['_setAccount','UA-XXXXX-X'],['_trackPageview']];
            (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
            g.src='//www.google-analytics.com/ga.js';
            s.parentNode.insertBefore(g,s)}(document,'script'));
        </script>
    </body>
</html>
