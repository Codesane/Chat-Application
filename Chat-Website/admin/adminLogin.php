<?php
	session_start();
	require_once("../core/dbConnection.php");
	class AdminLogin {
		
		const NO_ERROR 				= 0;
		const ERROR_USER_NO_EXIST 	= 1;
		const ERROR_USER_NOT_ADMIN 	= 2;
		
		function login($username, $password) {
			$safeUsername = $this->safeString($username);
			$safePassword = $this->safeString($password);
			
			return $this->queryUser($safeUsername, $safePassword);
		}
		
		function queryUser($un, $pw) {
			$query = mysql_query("SELECT user_id, admin FROM `users` WHERE `username` = '{$un}' AND `password` = '{$pw}' LIMIT 1");
			if(!mysql_num_rows($query)) {
				return self::ERROR_USER_NO_EXIST;
			} else {
				$getRows = mysql_fetch_assoc($query);
				if($getRows["admin"] == 1) {
					$_SESSION['sid'] = $getRows['user_id'];
					return self::NO_ERROR;
				} else {
					return self::ERROR_USER_NOT_ADMIN;
				}
			}
		}
		
		function safeString($str) {
			return mysql_real_escape_string(htmlentities($str));
		}
		
	}
	if(isset($_POST['un'], $_POST['pw'])) {
		$login = new AdminLogin();
		echo $login->login($_POST['un'], $_POST['pw']);
	}
?>