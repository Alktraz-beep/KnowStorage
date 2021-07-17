<?php
    include 'conexion.php';
    if(isset($_POST["nombreTest"])){//si existe el post
        $nombreTest=$_POST["nombreTest"];
        $rows=$wpdb->get_results("SELECT * FROM TestAudio WHERE NOMBRE_TEST='$nombreTest'");
        if(count($rows)>0){
            $resp["existe"]=true;//ya existe
        }else{
            $resp["existe"]=false;//No existe
        }
        echo json_encode($resp);

    }
?>