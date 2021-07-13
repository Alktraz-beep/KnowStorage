<?php

	require_once 'conexion.php';
	$idUsuario=$_POST['id'];//sera el login
	$userName=$_POST['nombre'];//sera la contraseña
	$user=get_user_by('login',$idUsuario);
	//$password='315253427aA.';
	
    if ( empty($user)) {//sino esta en lom
        $rows=$wpdb->get_results("SELECT ROL FROM UsuariosKnowStorage WHERE ID='$idUsuario' AND PASSWORD='$userName'");
		foreach($rows as $row){  
			$resp["rol"]=$row->ROL; 
            
		}
        if($resp["rol"]=="a" || $resp["rol"]=="p"){
			$resp["validar"]=true;//se encontró 
            $resp["mensaje"]="Ya existe en la base de datos";
            
		}else{
			$resp["validar"]=false;//no está en ninguno
            $resp["mensaje"]="No existe en la base";
		}
    } else {   
        
        $resp["validar"]=true;
        $resp["mensaje"]="Ya existe en la base de datos";
        
    }
	echo json_encode($resp);
	
?>