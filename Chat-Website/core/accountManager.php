<?php
	require_once("dbConnection.php");
	class AccountManager {
		
		// Error codes returned to the client and displayed to the user as an error message.
		const ACCOUNT_CREATED = 0;
		const USER_ALREADY_EXISTS = 1;
		const PASSWORDS_NOMATCH = 2;
		const INVALID_CHARACTERS = 3;
		const USERNAME_TOO_LONG = 4;
		const USERNAME_TOO_SHORT = 5;
		const PASSWORD_TOO_LONG = 6;
		const PASSWORD_TOO_SHORT = 7;
		const MYSQL_ERROR_OCCURRED = 8;
		///////////////////////////////
		const MAX_USERNAME_LENGTH = 15;
		const MAX_PASSWORD_LENGTH = 25;
		
		const PREG_MATCH_ARGS = '/^[A-Za-z0-9_]+$/';
		
		function register($username, $password, $repeatPassword) {
			if(strcmp($password, $repeatPassword)) {
				return self::PASSWORDS_NOMATCH;
			} else {
				return $this->createAccount($username, $password);
			}
		}
		
		/** Returns  */
		function createAccount($username, $password) {
			$isValid = $this->isValidInput($username, $password);
			if($isValid != 0) {
				return $isValid;
			}
			if($this->userExists($username)) {
				return self::USER_ALREADY_EXISTS;
			}
			return $this->performRegistration($username, $password);
		}
		
		function isValidInput($username, $password) {
			if(!preg_match(self::PREG_MATCH_ARGS, $username) || !preg_match(self::PREG_MATCH_ARGS, $password)) {
				return self::INVALID_CHARACTERS;
			} else if(strlen($username) > self::MAX_USERNAME_LENGTH) {
				return self::USERNAME_TOO_LONG;
			} else if(strlen($password) > self::MAX_PASSWORD_LENGTH) {
				return self::PASSWORD_TOO_LONG;
			} else {
				return 0;
			}
		}
		
		function safeString($str) {
			return mysql_real_escape_string(htmlentities($str));
		}
		
		function userExists($user) {
			$ueQuery = mysql_query("SELECT `user_id` FROM `users` WHERE `username` LIKE '{$this->safeString($user)}' LIMIT 1");
			return (mysql_num_rows($ueQuery));
		}
		
		function performRegistration($username, $password) {
			$safeUsername = $this->safeString($username);
			$safePassword = $this->safeString($password);
			
			$regUserQuery = mysql_query("INSERT INTO `users`(`username`, `password`, `timeRegisteredMillis`) VALUES('{$safeUsername}', '{$safePassword}', '0')") or die(mysql_error());
			
			if(!$regUserQuery) {
				return self::MYSQL_ERROR_OCCURRED;
			} else {
				return self::ACCOUNT_CREATED;
			}
		}
		
	}
	
	if(isset($_POST['un'], $_POST['pw1'], $_POST['pw2'])) {
		$accManager = new AccountManager();
		echo $accManager->register($_POST['un'], $_POST['pw1'], $_POST['pw2']);
	} else {
		echo "ACCESS DENIED!";
	}
	
?>