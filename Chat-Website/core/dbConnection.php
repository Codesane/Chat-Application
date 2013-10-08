<?php
	class ServerConnectionException extends Exception {}
	class DatabaseConnectionException extends Exception {}
	
	class DatabaseConnector {
		const HOST     = "localhost";
		const USER     = "root";
		const PASS     = "";
		const DATABASE = "chatapplication";
		
		function connect() {
			try {
				$this->connectToServer();
				$this->connectToDatabase();
				/* Perhaps tell the user that an error occurred and that we can't connect to the database for now. Perhaps try again later? */
			} catch(ServerConnectionException $e) {
				echo $e->getMessage();
			} catch(DatabaseConnectionException $e) {
				echo $e->getMessage();
			}
		}
		function connectToServer() {
			if(!@mysql_connect(self::HOST, self::USER, self::PASS)) {
				throw new ServerConnectException("Unable to connect to Server.");
			}
		}
		function connectToDatabase() {
			if(!@mysql_select_db(self::DATABASE)) {
				throw new DatabaseConnectionException("Unable to connect to Database.");
			}
		}
	}
	$db = new DatabaseConnector();
	$db->connect();
?>
