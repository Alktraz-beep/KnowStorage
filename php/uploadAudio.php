<?php
    include 'conexion.php';
    
    $id_usuario=$_POST['id_usuario'];//aqui va el id del usuario
    $nombre=$_POST['nombre'];//aqui va el nombre del audio que le puso
    $calificacion=$_POST['calificacion'];//aqui va la calificacion total
    $descripcion=$_POST['descripcion'];//aqui la des de la calificacion
    $nombreTest=$_POST['nombreTest'];//aqui el nombre del test

    if($_FILES['upload']){
        $name=$_FILES['upload']['name'];
        $tmp=$_FILES['upload']['tmp_name'];
        $filePath=$_SERVER['DOCUMENT_ROOT']."/wp-content/plugins/buscar_audio/audios/".$name;
        //falta subir a db
        if(move_uploaded_file($tmp,$filePath)){
            echo "Se subió correctamente";
            //sacamos la url
            $audioLink="https://leanonmecc.com/wp-content/plugins/buscar_audio/audios/".$name;
            $datos=array('ID_USUARIO'=> $id_usuario,'NOMBRE'=>$nombre,'AUDIO'=>$audioLink,'CALIFICACION'=>$calificacion,'DESCRIPCION'=>$descripcion,'NOMBRE_TEST'=>$nombreTest);
            $result=$wpdb->insert('Audios_LeanOnMe',$datos);//se inserta en la tabla de datos para su sesion
        }else{
            echo "Se subió correctamente";
        }
        /**SUBIENDO A */

    }
?>