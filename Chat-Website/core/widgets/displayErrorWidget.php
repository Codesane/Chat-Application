<?php
	if(isset($_GET['err_msg'])) {
		$isSuccessful = false;
		if(isset($_GET['success'])) {
			$isSuccessful = true;
		}
		$displayError = $_GET['err_msg'];
		?>
			<div class="comp-error-container">
				<div class="comp-error-row">
					<div class="comp-error-close <?php if($isSuccessful) echo "comp-error-message-successful"; ?>">x</div>
					<div class="comp-error-message <?php if($isSuccessful) echo "comp-error-message-successful"; ?>"><?php echo $displayError; ?></div>
				</div>
			</div>
			<script type="text/javascript">
				$(document).ready(function() {
					$(".comp-error-row").slideDown(300);
				});
				$(".comp-error-close").bind("click", function() {
					$(this).parent().slideUp(300);
				});
			</script>
		<?php
	}
?>
