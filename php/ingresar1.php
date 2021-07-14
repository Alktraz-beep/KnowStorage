<?php

	require_once 'conexion.php';
	$usuario=$_POST['usuario'];
	$password=$_POST['password'];
	$user=get_user_by('login',$usuario);
	//$password='315253427aA.';
	/*inicializacion por defecto */
	$resp["validar"]=false;
	$tablaUsuariosKnowStorage=$wpdb->prefix."UsuariosKnowStorage";
	if($user && wp_check_password($password,$user->user_pass,$user->ID)){
        $resp["validar"]=true;
		$resp["rol"]="a";//si ya esta en LOM es alumno
    } else {   //de lo contrario a buscarlo en  la tabla para usuarios de fb y google
		$rows=$wpdb->get_results("SELECT ROL FROM UsuariosKnowStorage WHERE ID='$usuario' AND PASSWORD='$password'");
		if(!$wpdb->last_error){
			foreach($rows as $row){  
				$resp["rol"]=$row->ROL; 
			}
			if($resp["rol"]=="a" || $resp["rol"]=="p"){
				$resp["validar"]=true;//se encontró 
			}else if($resp==null || $resp==""){
				$resp["validar"]=false;//no está
			}
		}else{
			$resp["validar"]=false;//no está
		}
		
		
    }
	echo json_encode($resp);
	
?>