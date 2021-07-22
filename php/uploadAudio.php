<?php
    include 'conexion.php';
    if($_FILES['upload']){
        $name=$_FILES['upload']['name'];
        $tmp=$_FILES['upload']['tmp_name'];
        $filePath=$_SERVER['DOCUMENT_ROOT']."/wp-content/plugins/buscar_audio/audios/".$name;

        if(move_uploaded_file($tmp,$filePath)){
            echo "Se subió correctamente";
        }else{
            echo "Se subió correctamente";
        }
    }
?>