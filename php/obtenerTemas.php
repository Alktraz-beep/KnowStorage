<?php
     include 'conexion.php';
     if(isset($_POST["nombreTest"]) && isset($_POST["passwordTest"])){//obtenemos el nombre del test
         $nombreTest=$_POST["nombreTest"];
         $passwordTest=$_POST["passwordTest"];

         $resp["rubrica"]=array();
         $rows=$wpdb->get_results("SELECT * FROM TestAudio WHERE NOMBRE_TEST='$nombreTest' AND PASSWORD='$passwordTest'");//obtiene todo donde el nombre del test es el elegido
         if(count($rows)>0){//si si hay audios
             $validar=true;
             $resp["valida"]=$validar;
             foreach($rows as $row){//por cada renglon de resultado
                 $audio=array(
                     "DURACION"=>$row->DURACION,//nombre de alumno
                     "TEMAS"=>$row->TEMAS,//calificación
                 );
                 array_push($resp["rubrica"],$audio);//se inserta cada informacion de audio
             }
         }else{//si no hay audios
             $validar=false;
             $resp["valida"]=$validar;//guarda en valida si es que no se encontró ningun audio
         }
         echo json_encode($resp);
     }
?>