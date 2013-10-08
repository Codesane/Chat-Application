<?php
	session_start();
	require_once("core/dbConnection.php");
	
	/** Settings for the edit page,
	 *  Contains the string for the  */
	const SERVER_NAME_INDEX = "";
	const SERVER_PORT_INDEX = "";
	const CLIENT_VERSION_INDEX = "";
	const SERVER_MOTD_INDEX = "";
	
	class AdminManager {
		
		public function __construct() {
			
		}
		
		private function getUserList($fromIndex, $toIndex) {
			$query = mysql_query("SELECT * FROM `users` LIMIT {$fromIndex}, {$toIndex}") or die(mysql_error());
			if(mysql_num_rows($query)) {
				echo "return user list.";
			}
		}
		
		private function getServerSettings() {
			$query = mysql_query("SELECT * FROM `server_settings`");
			return mysql_fetch_assoc($query);
		}
		
		
		function printMenu() {
			?>
				<div id="admin-menu">
					<ul>
						<li>All Users</li>
						<li>Search</li>
					</ul>
				</div>
			<?php
		}
		
		function displaySettings($editable) {
			$this->printMenu();
			$settings = $this->getServerSettings();
			?>
				<div id="admin-container"><br />
					<center><h2><b>Server Settings</b></h2></center><hr />
					<div id="settings-container">
						<div class="comp-row">
							<div class="comp-row-col1">
								Server Name:
							</div>
							<div class="comp-row-col2">
							<?php
								if($editable) {
									?>
										<input id="admin-server-name" type="text" value="<?php echo "Simple Server"; ?>" class="comp-input-text-password admin-modified-inputtext" />
									<?php
								} else {
									echo "Simple Server";
								}
							?>
							</div>
							<div class="clearfix"></div>
							<div class="comp-row-col1">
								Port Number:
							</div>
							<div class="comp-row-col2">
							<?php
								if($editable) {
									?>
										<input id="admin-server-port" type="text" value="<?php echo "13337"; // Get the already set port. ?>" class="comp-input-text-password admin-modified-inputtext" />
									<?php
								} else {
									echo "13337";
								}
							?>
							</div>
							<div class="clearfix"></div>
							<div class="comp-row-col1">
								Client Version
							</div>
							<div class="comp-row-col2">
							<?php
								if($editable) {
									?>
										<input id="admin-client-version" type="text" value="<?php echo "1.0.0";?>" class="comp-input-text-password admin-modified-inputtext" />
									<?php
								} else {
									echo "1.0.0";
								}
							?>
							</div>
							<div class="clearfix"></div>
							<?php
								if($editable) {
									?>
										<label for="admin-motd-display" style="float:left; margin-top:10px;"><b>Message of the Day:</b></label>
										<textarea id="admin-server-motd"><?php echo "Welcome to the Chat!"; ?></textarea>
									<?php
								} else {
									?>
										<label for="admin-motd-display" style="float:left; margin-top:10px;"><b>Message of the Day:</b></label>
										<div id="admin-motd-display" style="float:left;"><?php echo "Welcome to the Chat!"; ?></div>
									<?php
								}
							?>
						</div>
						<br />
						<?php
							if($editable) {
								echo '<div id="admin-edit-settings-div"><a href="?q=admin">Save Settings</a></div>';
							} else {
								echo '<div id="admin-edit-settings-div"><a href="?q=admin&settings_e">Edit Settings</a></div>';
							}
						?>
					</div>
				</div>
			<?php
		}
		
		function displayUsersOnline() {
			?>
				<div id="admin-container">
					<center>
						<table width="500" id="admin-user-list">
							<tr><td width="50"><b>Id</b></td><td><b>Name</b></td><td><b>Registered</b></td><td width="50"><b>Banned</b></td><td><b>Status</b></td><td><b>Admin</b></td></tr>
							<?php
								for($i = 0; $i < 16; $i++) {
									echo "<tr><td>1</td><td>Felix</td><td>2012-08-10</td><td>No</td><td>Offline</td><td>Yes</td></tr>";
								}
							?>
						</table>
					</center>
				</div>
			<?php
		}
		
		function displayLoginForm() {
			?>
				<div id="login-form-container">
					<div id="login-error-container">
						
					</div>
				<div class="clearfix"></div>
					<div style="margin-bottom:50px;" class="comp-row-col-container">
						<div class="comp-row">
							<div class="comp-row-col1">Username: </div>
							<div class="comp-row-col2"><input id="login_username" class="comp-input-text-password" type="text" placeholder="Username..." /></div>
						</div>
						<div class="comp-row">
							<div class="comp-row-col1">Password: </div>
							<div class="comp-row-col2"><input id="login_pw" class="comp-input-text-password" type="password" placeholder="*********" /></div>
						</div>
						<div class="comp-row">
							<div class="comp-row-col1"></div>
							<div class="comp-row-col2" style="margin-top:10px;"><input type="button" id="admin_login_button" class="comp-input-button-submit" value="Login"></div>
						</div>
					</div>
				</div>
			<?php
		}
		
	}
	
?>
