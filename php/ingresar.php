<?php
if(isset($_POST['usuario']) && isset($_POST['password'])){
	require_once 'conexion.php';
	include 'validar.php';
	$usuario=validar($_POST['usuario']);
	$password=validar($_POST['password']);
	//$usuario="Josue";
	//$password="315253427a.";
	$sql="SELECT * FROM $wpdb->users WHERE WHERE user_login= $usuario AND user_pass='".md5($password)."'";
	
	$result=$wpdb->query($sql);
	
	if($result->num_rows>0){
		echo "success";
	}else{
		echo "failure";
	}
	
}
	
	
?>
