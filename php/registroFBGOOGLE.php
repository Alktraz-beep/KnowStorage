<?php
include "conexion.php";
$idUsuario=$_POST['id'];//sera el login
$userName=$_POST['nombre'];//sera la contraseña
$rol=$_POST['rol'];//sera la contraseña
/*insert en tabla UsuariosAudio  */
$datos=array('ID'=>$idUsuario,'PASSWORD'=>$userName,'ROL'=>$rol);
$result=$wpdb->insert('UsuariosKnowStorage',$datos);//se inserta en la tabla de datos para su sesion
if($result){
    $resp["valida"]=true;//se registró correctamente
    $resp["mensaje"]="Se registró correctamente";
}else{
    $resp["valida"]=false;//no se registró
    $resp["mensaje"]="No se pudo registrar";
}

echo json_encode($resp);
?>