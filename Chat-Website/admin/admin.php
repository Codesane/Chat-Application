<link rel="stylesheet" href="css/widgets/admin.css">
<link href='http://fonts.googleapis.com/css?family=Source+Code+Pro' rel='stylesheet' type='text/css'>
<?php
	/* Stars the session as well. */
	require_once("adminManager.php");
	$adminManager = new AdminManager();
	
	if (isset($_SESSION['sid'])) {
		$adminManager->displaySettings(isset($_GET['settings_e']));
	} else {
		// if the admin session is not set.
		$adminManager->displayLoginForm();
	}
	
?>

