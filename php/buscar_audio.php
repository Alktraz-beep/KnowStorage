<?php
	/*
 * Plugin Name:         Buscar audios
 * Description:         permite buscar audios en la base de datos
 * Author:              Josue Yafte
 * Author URI:          Josue Yafte
 * Plugin URI:          Leanonme.cc
 * Text Domain:         Josue Yafte
 * Requires at least:   4.6
 * Requires PHP:        5.6
*/
	include 'conexion.php';
	$nombre=$_GET['nombre'];

	$consulta="SELECT * FROM Audios_LeanOnMe";

	
	$result=$wpdb->get_results("SELECT * FROM Audios_LeanOnMe WHERE NOMBRE='$nombre'");
	if ($wpdb->last_error) {
  		echo 'You done bad! ' . $wpdb->last_error;
	}
	foreach ( $result as $page )
	{
	   echo $page->NOMBRE.',';
	   echo $page->AUDIO.',';
	   echo $page->CALIFICACION.',';
	   echo $page->TIPO.',';
	   echo $page->DESCRIPCION.',';
	   echo $page->TEMA.',';
	   echo "<br>";
	}
	//echo ('<br> escribiste otra vez '.$wpdb->prefix);
	
?>