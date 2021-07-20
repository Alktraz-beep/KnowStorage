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
    String resultados;
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
                    //etResultados.setText("Calificacion: "+califDuracion+" Duracion "+duracionAudio);
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
    /*****************************************ALGORITMO PARA CALIFICAR VELOCIDAD[][][][]*/
    public void calificarVelocidad(int palabrasT){
        if((float)palabrasT<(float)((duracion/60)*MIN_PALABRAS)){//menor a 600 palabras
            califVelocidad-=(((((float)duracion/60)*(float)MIN_PALABRAS)-(float)palabrasT)/(((float)duracion/60)*(float)MIN_PALABRAS))*100;
        }else if((float)palabrasT>(float)((duracion/60)*MAX_PALABRAS)){//mayor a 750 palabras
            califVelocidad+=(((((float)duracion/60)*(float)MAX_PALABRAS)-(float)palabrasT)/(((float)duracion/60)*(float)MAX_PALABRAS))*100;
        }
    }
    /*************RELLENA ARRAYS Y DEVUELVE LA CANTIDAD DE PALABRAS DICHAS POR EL ALUMNO**/
    public int obtenerPalabrasYLlenarArrays(String st){
        StringTokenizer stringTokenizer=new StringTokenizer(st," \n-()*");
        int palabrasT=stringTokenizer.countTokens();
        return palabrasT;
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