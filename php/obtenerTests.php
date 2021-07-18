<?php
    header("Content-type: application/json; charset=utf-8");
    include 'conexion.php';
    if(isset($_POST["nombreProfesor"])){

        $nombreProfesor=$_POST["nombreProfesor"];
        $resp["tests"]=array();
        $rows=$wpdb->get_results("SELECT * FROM TestAudio WHERE ID_PROFESOR='$nombreProfesor'");
        if(count($rows)>0){
            $validar=true;
            $resp["valida"]=$validar;
            foreach($rows as $row){
                $test=array(
                    "NOMBRE_TEST"=>$row->NOMBRE_TEST,
                    "PASSWORD_TEST"=>$row->PASSWORD,
                    "DURACION"=>$row->DURACION,
                    "TEMAS"=>$row->TEMAS,
                );
                array_push($resp["tests"],$test);
            }
        }else{
            $validar=false;
            $resp["valida"]=$validar;
        }
        echo json_encode($resp, JSON_UNESCAPED_UNICODE);
    }
?>