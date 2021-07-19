<?php
     include 'conexion.php';
     if(isset($_POST["nombreTest"])){//obtenemos el nombre del test
         $nombreTest=$_POST["nombreTest"];
         //$nombreTest="test1";
         $resp["audios"]=array();
         $rows=$wpdb->get_results("SELECT * FROM Audios_LeanOnMe WHERE ID_USUARIO='$nombreTest'");//obtiene todo donde el nombre del test es el elegido
         if(count($rows)>0){//si si hay audios
             $validar=true;
             $resp["valida"]=$validar;
             foreach($rows as $row){//por cada renglon de resultado
                 $audio=array(
                     "NOMBRE_ALUMNO"=>$row->NOMBRE,//nombre de alumno
                     "CALIFICACION"=>$row->CALIFICACION,//calificación
                     "DESCRIPCION"=>$row->DESCRIPCION,//la duracion del audio
                     "AUDIO"=>$row->AUDIO,//link del audio subido
                 );
                 array_push($resp["audios"],$audio);//se inserta cada informacion de audio
             }
         }else{//si no hay audios
             $validar=false;
             $resp["valida"]=$validar;//guarda en valida si es que no se encontró ningun audio
         }
         echo json_encode($resp);
     }
?>