package com.example.knowstorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class LTTA extends Activity {
    /*****PARA EVALUACION*****/
    private static int MAX_PALABRAS=150;
    private static int MIN_PALABRAS=120;
    String duracionString;
    String temas;
    float duracion;
    TextView etResultados;
    float califDuracion=100.0f;// aqui se guarda la calificacion de la duracion
    float califTemas=100.0f;// aqui se guarda la calificacion de la temas
    float califVelocidad=100.0f;// aqui se guarda la calificacion de la velocidad
    float califTotal=0.0f;
    ArrayList<String> copiaPalabrasClave =new ArrayList<String>();//aqui se ponen las palabras clave dichas
    ArrayList<ArrayList<String>> Temas =new ArrayList<ArrayList<String>>();//aqui es un array de arrays que contiene cada tema
    ArrayList<ArrayList<String>> Temas2 =new ArrayList<ArrayList<String>>();//es la copia de los temas
    int cantidadPalabras=0;//aqui se almacena la cantidad palabras dichas por el alumno
    /*****PARA GRABACION******/
    private static int MICROPHONE_PERMISSON=200;
    EditText editText;
    String nombreAudio;
    MediaRecorder mediaRecorder;//para grabar
    MediaPlayer mediaPlayer;//para reproducir
    boolean test=false;
    /***PARA RECONOCIMIENTO DE VOZ***/
    private int RecordAudioRequestCode = 1;
    String aviso="";//dice si se acabo o no el proceso  en Toast
    SpeechRecognizer speech=null;
    Intent recognizerIntent;
    String LOG_TAG="VoiceRecognitionActivity";
    String text = "";
    /******PARA GUARDAR EN DB*****/
    String nombreTest;
    String descripcion="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ltta);
        editText=findViewById(R.id.ponNombre);
        etResultados=findViewById(R.id.resultados);
        /*                     OBTENCION DE DATOS PARA INSERTAR Y EVALUAR               */
        nombreTest=getIntent().getStringExtra("nombreTest");
        duracionString=getIntent().getStringExtra("duracion");
        temas=getIntent().getStringExtra("temas");
        duracion=Float.parseFloat(duracionString)*60;//duracion limite total
        /*                    permisos                         */
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
            }
        }
        if(isMicrophonePresent()) {
            getMicrophnePermission();
        }
        /*                  Para reconocimiento de voz                         */
        speech=SpeechRecognizer.createSpeechRecognizer(this);//inicializa el speech
        startListening();
        iniciaSpeech();
    }
    /****************************Boton PARA CUANDO SE CONFIRMA*/
    public void confirmarAudio(View view){
        float duracionAudio=0.0f;//en segundos
        float duracionMinutosAudio=0.0f;//en minutos
        if(!text.equals("")){//significa que ya hizo audio
            try{
                int time=mediaPlayer.getDuration();//lanza -1 de lo contrario
                if(time!=-1){
                    duracionAudio=(float)time/(float) 1000;
                    duracionMinutosAudio=duracionAudio/60;
                    calificarDuracion(duracionAudio);
                    String noDicho= calificarTemas();
                    calificarVelocidad(cantidadPalabras);
                    califTotal=califDuracion*.3f+califVelocidad*.3f+califTemas*.4f;
                    descripcion="Calificación Total: "+String.format("%.2f",califTotal)
                            +"\nCalificación de duración: "+String.format("%.2f",califDuracion)+" Duracion: "+String.format("%.2f",duracionMinutosAudio)
                            +"\nCalificación de fluidez: "+String.format("%.2f",califVelocidad)
                            +"\nCalificación de temas: "+String.format("%.2f",califTemas)
                            +"\nTemas faltantes: "+noDicho+"\n"+transformarTemas(temas);
                    etResultados.setText(descripcion);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }else{
            Toast.makeText(getApplicationContext() , "No has grabado nada revisa tu audio",   Toast.LENGTH_SHORT).show();
        }
    }
    public void enviar(View view){
        if(!etResultados.getText().toString().equals("")){
            //hacer envio php
        }else{
            Toast.makeText(getApplicationContext() , "No has sido evaluado",   Toast.LENGTH_SHORT).show();
        }
    }
    /***************************Para boton de record y lo guarda en musica*/
    public void record(View view){
        nombreAudio=editText.getText().toString();
        if(!nombreAudio.equals("")){
            try{
                mediaRecorder=new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(getRecordingPath(nombreAudio));
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start();
                text="";//reinicia el texto
                editText.setEnabled(false);//deja de poder ser editado
                test=true;//para que solo durante este momento guarde el texto
                Toast.makeText(getApplicationContext() , "Se ha comenzado a grabar"+aviso,   Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext() , "Ha ocurrido un error"+e.getMessage(),   Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext() , "Primero ponle nombre a tu audio",   Toast.LENGTH_SHORT).show();
        }
    }
    /****************************para el boton de stop **/
    public void stop(View view){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
        test=false;
        mediaPlayer=MediaPlayer.create(LTTA.this, Uri.parse(getRecordingPath(nombreAudio)));//le pone el file al mediaplayer
        Toast.makeText(getApplicationContext() , "Se ha terminado de grabar",   Toast.LENGTH_SHORT).show();
    }
    /*****************************************Para reproducir audio***/
    public void play(View view){
            //mediaPlayer.cr(getRecordingPath(nombreAudio));
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                Toast.makeText(getApplicationContext() , "Pausa",   Toast.LENGTH_SHORT).show();
            }else{
                mediaPlayer.start();
                Toast.makeText(getApplicationContext() , "Reproduciendo grabación",   Toast.LENGTH_SHORT).show();
            }
        }
    }
    /******************************************************FUNCIONES SECUNDARIAS********************************************************************
     * *********************************************************************************************************************************************/
    public String calificarTemas(){
        ////primero llenamos el array de temas de temas
        int numTemas=0;
        StringTokenizer stringTokenizer=new StringTokenizer(temas," \n");
        while (stringTokenizer.hasMoreTokens()){
            String str=stringTokenizer.nextToken();
            if(str.contains("**")){//aqui vemos cuantos temas hay sacamos numero de temas
                numTemas++;
                //ArrayList<String > auxiliar=new ArrayList<>();
                Temas.add(new ArrayList<String>());
                Temas.get(numTemas-1).add(str);
            }else{
                Temas.get(numTemas-1).add(str);
            }

        }
        /*for(int i=0;i<numTemas;i++){
            ArrayList<String > auxiliar=new ArrayList<>();

        }*/

        copiarArraydeArrays(Temas,Temas2);//copia el array Temas a Temas2
        /*Sacamos cuantas palabras dijo*/
        StringTokenizer stringTokenizer1=new StringTokenizer(text," -(),");
        cantidadPalabras=stringTokenizer1.countTokens();//obtenemos la cantidad de palabras
        while(stringTokenizer1.hasMoreTokens()){
            String string=stringTokenizer1.nextToken();
            for(int i=0;i<Temas.size();i++){
                if(!string.equalsIgnoreCase("tema") && !string.equalsIgnoreCase("temas"))
                    compara(Temas.get(i),string,copiaPalabrasClave);//ve si son iguales y las deposita en copiaPalabrasClave las dichas y se las quita a Temas
            }
        }
        /*Evaluacion primero saca temas no dichos*/
        int cont=0;
        String indiceDeTema="";
        for(int i=0;i<Temas2.size();i++){
            if(Temas2.get(i).size()-Temas.get(i).size()>=1){//si es mayor igual a 1 significa que Temas2 es mayor y hay una diferencia (si dijo temas)
                cont++;//si mencionó sobre temas
            }else{//si es menor a 1 significa que la diferencia no es nada es decir no dijo esos temas
                indiceDeTema+=","+(i+1);
            }
        }
        /*Luego hace evaluacion*/
        if(cont==Temas2.size()){
            califTemas=100;
        }else{
            califTemas=((float)cont/(float)Temas2.size())*100;
        }
        return indiceDeTema;
    }
    /*FUNCION QUE COMPARA SI UNA STRING SE PARECE A ALGUNA DE UN AREEGLO DE STRINGS*/
    public static boolean compara(ArrayList<String> pc,String palabra,ArrayList<String> copiapc){
        boolean iguales=false;
        for (int i=0;i<pc.size();i++){
            if(pc.get(i).equalsIgnoreCase(palabra)==true){
                iguales=true;
                copiapc.add(pc.get(i));
                pc.remove(i);
                //System.out.println("se encontro: "+palabra+" "+pc.get(i));
            }
        }

        return iguales;
    }
    public static void imprimirArray(ArrayList<String> array){
        System.out.println("---------Array:");
        for(int i=0;i<array.size();i++){
            System.out.println(array.get(i));
        }
    }
    public String transformarTemas(String t){
        StringTokenizer stringTokenizer=new StringTokenizer(t," \n");
        String transformados="";
        int cont=0;
        while(stringTokenizer.hasMoreTokens()){
            String palabra=stringTokenizer.nextToken();

            if(palabra.contains("**")){
                cont++;
                transformados+=cont+" :"+palabra+"\n";
            }
        }
        return transformados;
    }
    /******************************Solo imprime para revisar que estén bien*/
    public void imprimirTemas(ArrayList<ArrayList<String>> arrayofarray){
        System.out.println("Temas");
        for(int i=0;i<arrayofarray.size();i++){
            for(int j=0;j<arrayofarray.get(i).size();j++){
                System.out.println(arrayofarray.get(i).get(j));
            }
        }
    }
    /********************************Copia del array a al array b*/
    public void copiarArraydeArrays(ArrayList<ArrayList<String>> arrayA,ArrayList<ArrayList<String>> arrayB){
        for(int i=0;i<arrayA.size();i++){
            ArrayList<String> lista=new ArrayList<String>();//auxiliar
            for(int j=0;j<arrayA.get(i).size();j++){
                lista.add(arrayA.get(i).get(j));//obtengo cada array y lo añado
            }
            arrayB.add(lista);
        }
    }
    /*****************************************ALGORITMO PARA CALIFICAR VELOCIDAD[][][][]*/
    public void calificarVelocidad(int palabrasT){
        if((float)palabrasT<(float)((duracion/60)*MIN_PALABRAS)){//menor a 600 palabras
            califVelocidad-=(((((float)duracion/60)*(float)MIN_PALABRAS)-(float)palabrasT)/(((float)duracion/60)*(float)MIN_PALABRAS))*100;
        }else if((float)palabrasT>(float)((duracion/60)*MAX_PALABRAS)){//mayor a 750 palabras
            califVelocidad+=(((((float)duracion/60)*(float)MAX_PALABRAS)-(float)palabrasT)/(((float)duracion/60)*(float)MAX_PALABRAS))*100;
        }
    }

    /*******************************************ALGORITMO PARA CALIFICAR DURACION [][][]*/
    public void calificarDuracion(float durAudio){
        if(durAudio<duracion){//valor absoluto
            califDuracion+=((durAudio-duracion)/duracion)*100;//formula
        }else if(duracion>duracion){
            califDuracion-=((durAudio-duracion)/duracion)*100;
        }
    }

    /*****************************************funcion para cargar e iniciar el speech*/
    public void iniciaSpeech(){
        speech.setRecognitionListener(
                new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {
                        startListening();
                    }

                    @Override
                    public void onError(int error) {
                        startListening();
                        //Toast.makeText(getApplicationContext() , "Hubo un error"+getErrorText(error),   Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResults(Bundle results) {
                        Log.i(LOG_TAG,"onResults");
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);//contiene todas las cadenas dichas

                        for (String result:matches){
                            if(test){
                                text+=result+" ";//result contiene cadenas de caracteres con espacios
                                aviso=" Ha iniciado el test";
                                Toast.makeText(getApplicationContext() , "Analizado correctamente",   Toast.LENGTH_SHORT).show();
                            }

                            //etResultados.setText(text);
                        }
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }


                }
        );

    }
    /*inicia el proceso de reconocimiento de voz*/
    public void startListening(){
        recognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-MX");//lenguaje
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());//package
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);//Modelo
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1);//tiempo en silencio
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);//resultados
        speech.startListening(recognizerIntent);
    }
/*Aqui vemos si funciona el microfono y sino ponemos permisos*/
    public boolean isMicrophonePresent(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }else {
            return false;
        }
    }
    /*obtenemos permisos para recording*/
    public void getMicrophnePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSON );
        }
    }
    /*Guardamos en su carpeta de musica*/
    private String getRecordingPath(String nombre){
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File music_dir=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file= new File(music_dir,nombre+".mp3");
        return file.getPath();
    }
    /***********************************************Funcion que obtiene el error de reconocimiento*/
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
        }
    }
}