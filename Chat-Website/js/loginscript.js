$(document).ready(function() {
	new AdminLogin().init;
});

function AdminLogin() {
	
	var ERRORS = [
		"Incorrect Username or Password.",
		"You do not have Admin privileges."
	];
	
	this.init = new function() {
		$("#login_username, #login_pw").on("keydown", function(e) {
			if(e.which == 13) {
				postAdminLogin();
			}
		});
		$("#admin_login_button").bind("click", function() {
			postAdminLogin();
		});
	}
	
	function postAdminLogin() {
		$.post("admin/adminLogin.php", {un:$("#login_username").val(), pw:$("#login_pw").val()}, function(data) {
			if(!isNaN(parseInt(data))) {
				if(data == 0) {
					displaySuccess("Success!");
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
	function displaySuccess(msg) {
		$.get("core/widgets/displayErrorWidget.php", {err_msg : msg, success: ""}, function(data) {
			$("#login-error-container").html(data);
			$(".comp-row-col-container").fadeOut(300, function() {
				setTimeout(function() {
					document.location.href = "";
				}, 1000);
			});
		});
	}
	function displayErrorMessage(errMsg) {
		$.get("core/widgets/displayErrorWidget.php", {err_msg : errMsg}, function(data) {
			$("#login-error-container").html(data);
			$("#login_pw").val("");
			$("#reg_username").select();
		});
	}
}