<?php
    include 'conexion.php';
    if(isset($_POST["nombreTest"]) && isset($_POST["passwordTest"]) && isset($_POST["duracion"]) && isset($_POST["ID_PROFESOR"]) && isset($_POST["temas"])){//si existe el post
        $id_profesor=$_POST["ID_PROFESOR"];
        $nombreT=$_POST["nombreTest"];
        $passT=$_POST["passwordTest"];
        $duracion=$_POST["duracion"];
        $temas=$_POST["temas"];
        $datos=array('ID_PROFESOR'=>$id_profesor,'NOMBRE_TEST'=>$nombreT,'PASSWORD'=>$passT,'DURACION'=>$duracion,'TEMAS'=>$temas);
        $result=$wpdb->insert('TestAudio',$datos);      
        if($result){
            $resp["valida"]=true;//se registró correctamente
            $resp["mensaje"]="Se ha creado el test con éxito";
        }else{
            $resp["valida"]=false;//no se registró
            $resp["mensaje"]="No se pudo crear el test";
        }
        echo json_encode($resp);

    }
?>