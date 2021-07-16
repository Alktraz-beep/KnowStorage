<?php
    include 'conexion.php';
    if(isset($_POST["nombreTest"])){//si existe el post
        
        $wpdb->get_results("SELECT * FROM TestAudios WHERE NOMBRE_TEST='$nombreTest'");
        if(!$wpdb->last_error){
            $resp["existe"]=true;//ya existe
        }else{
            $resp["existe"]=false;//No existe
        }
        echo json_encode($resp);

    }
?>