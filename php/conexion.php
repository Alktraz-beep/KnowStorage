<?php
		
	$testConnection = mysqli_connect('s7o043947477305.db.43947477.177.hostedresource.net:3311','s7o043947477305','wc7+eH2qp','s7o043947477305');

	if (!$testConnection) {
		die('Error: ' . mysqli_connect_error() . PHP_EOL);
	}
	//echo 'Database connection working!<br>';
	global $wpdb;
	if(!isset($wpdb))
	{
		//echo ('<br> isset'.$nombre);
	    require_once($_SERVER['DOCUMENT_ROOT'].'/wp-includes/wp-db.php');
		require_once($_SERVER['DOCUMENT_ROOT'].'/wp-includes/pluggable.php');
	    require_once($_SERVER['DOCUMENT_ROOT'].'/wp-config.php');
	}


?>