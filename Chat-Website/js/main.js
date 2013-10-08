$(document).ready(function() {
	$("#download_client_exe").bind("click", function() {
		document.location.href = "downloads/client/exe/ClientLauncher.exe";
	});
	$("#download_client_jar").bind("click", function() {
		document.location.href = "downloads/client/jar/ClientLauncher.jar";
	});
});
