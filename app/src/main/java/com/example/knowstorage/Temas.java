/*
* esta clase permite reconocer las palabras del profesor y así captar los temas
* posteriormente se añaden a la base de datos estas variables*/
package com.example.knowstorage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Temas extends Activity{
    TextView Datos;
    String nombreTest;
    String passwordTest;
    String duracion;
    EditText temas;

    /*VARIABLES DE RECONOCIMIENTO DE VOZ*/
    private boolean test=false;//bandera de reconocimiento
    private boolean acomodarTexto=false;//bandera de reconocimiento finalizado para acomodar texto
    /*medicion de duracion*/
    private  long start;
    private  long end;
    private  float duracionTest=0.0f;
    private int RecordAudioRequestCode = 1;
    /*para mensajes*/
    String aviso="";//dice si se acabo o no el proceso
    SpeechRecognizer speech=null;
    Intent recognizerIntent;
    String LOG_TAG="VoiceRecognitionActivity";
    String text = "";
    String textFinal = "";//contiene el texto final a poner en el edit text

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temas);
        Datos=findViewById(R.id.Datos);
        temas=findViewById(R.id.Temas);
        /*captura de variables activity pasada*/
        nombreTest= getIntent().getStringExtra("nombreTest");
        passwordTest= getIntent().getStringExtra("passwordTest");
        duracion=getIntent().getStringExtra("timeTest");
        Datos.setText(Datos.getText()+"\nTest:"+nombreTest+"\nPassword:"+passwordTest+"\nDuración:"+duracion);//pone los datos para confirmar
        /*para el speech*/


        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
            }
        }
        speech=SpeechRecognizer.createSpeechRecognizer(this);//inicializa el speech
        iniciaSpeech();
        startListening();


    }
    /*****************************************FUNCIONES PRIMARIAS****************************************************************************/
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
                            //text+=result+"";//result contiene cadenas de caracteres con espacios
                            if((result.contains(" finaliza test") || result.contains(" finaliza test ") || result.contains("finaliza test ") || result.contains("finaliza test")) && test) {//si se finaliza
                                aviso="Ha finalizado el test";
                                test=false;
                                acomodarTexto=true;
                                acomodarTexto();
                                Toast.makeText(getApplicationContext() , aviso,   Toast.LENGTH_SHORT).show();
                                speech.stopListening();
                            }else if((result.contains(" inicia test") || result.contains(" inicia test ") || result.contains("inicia test ") || result.contains("inicia test"))&& !test) {//si se inicia
                                aviso="Ha iniciado el test";
                                test=true;
                                Toast.makeText(getApplicationContext() , aviso,   Toast.LENGTH_SHORT).show();
                            }
                            /**************PARA HACER TOKENS*************/
                            if(test){//si se ha iniciado el test

                                if((!result.contains(" inicia test") && !result.contains(" inicia test ") && !result.contains("inicia test ") && !result.contains("inicia test")) ){
                                        text+=result+" ";//result contiene cadenas de caracteres con espacios
                                }
                                temas.setText(text);
                            }
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
    public void startListening(){
        recognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-MX");//lenguaje
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());//package
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);//Modelo
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 100000);//tiempo en silencio
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);//resultados
        speech.startListening(recognizerIntent);
    }
    /**********************************************FUNCIOONES SECUNDARIAS************************************************/
    /***********************************************Funcion para el boton de confirmación*/
    public void confirmar(View v){

    }
    /******************************Funcion que acomoda el texto a uno con **Tema# de manera visual*/
    public void acomodarTexto(){
        StringTokenizer st=new StringTokenizer(text," ");//separa el result por espacios
        textFinal="";
        while (st.hasMoreTokens()) {
            String aux=st.nextToken();//contiene cada palabra
            if(!aux.equals("tema")){
                textFinal+=aux+" ";
            }else{
                textFinal+="\n**"+aux;
            }
        }
        temas.setText(textFinal);
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
