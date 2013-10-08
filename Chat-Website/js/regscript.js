$(document).ready(function() {
	new Register().init;
});
function Register() {
	
	var ERRORS = [
		"A user with this name already exists.",
		"The passwords does not match.",
		"Invalid characters. Please use A-z 0-9",
		"Username too long, maximum 15 characters.",
		"Usename too short, minimum of 3 characters.",
		"Password too long, maximum 25 characters.",
		"Password too short, minimum 8 characters."
	];
	
	this.init = new function() {
		$(".comp-input-text-password, .comp-input-button-submit").on("keydown", function(e) {
			if(e.which == 13) {
				postRegister();
			}
		});
		$(".comp-input-button-submit").bind("click", function() {
			postRegister();
		});
	};
	
	
	function postRegister() {
		var un_cs = $("#reg_username").val();
		var pw1_cs = $("#reg_pw").val();
		var pw2_cs = $("#reg_rpw").val();
		if(un_cs.length < 3) {
			displayErrorMessage(ERRORS[4]);
			return;
		} else if(pw1_cs.length < 8 || pw2_cs.length < 8) {
			displayErrorMessage(ERRORS[6]);
			return;
		}
		$.post("core/accountManager.php", {un:un_cs, pw1:pw1_cs, pw2:pw2_cs}, function(data) {
			if(!isNaN(parseInt(data))) {
				if(data == 0) {
					alert("Registration Complete!\nYou may now login to your account.");
				} else {
					if(data > ERRORS.length || data < 0) {
						alert("Unexpected Error...");
					} else {
						displayErrorMessage(ERRORS[data - 1]);
					}
				}
			}
		});
	}
	function displayErrorMessage(errMsg) {
		$.get("core/widgets/displayErrorWidget.php", {err_msg : errMsg}, function(data) {
			$("#ca-error-container").html(data);
			$("#reg_pw, #reg_rpw").val("");
			$("#reg_username").select();
		});
	}
}