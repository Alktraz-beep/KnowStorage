/*
* esta clase permite reconocer las palabras del profesor y así captar los temas
* posteriormente se añaden a la base de datos estas variables*/
package com.example.knowstorage;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Temas extends Activity{
    TextView Datos;
    String nombreTest;
    String passwordTest;
    String duracion;
    EditText temas;
    RequestQueue requestQueue;//para la respuesta PHP
    ProgressDialog progressDialog;
    String URLguardarTest="https://leanonmecc.com/wp-content/plugins/buscar_audio/guardarTest.php";//link donde accedo a guardar test
    SharedPreferences sharedPreferences;//para obtener el nombre id
    String id;
    /*VARIABLES DE RECONOCIMIENTO DE VOZ*/
    private boolean test=false;//bandera de reconocimiento
    private boolean acomodarTexto=false;//bandera de reconocimiento finalizado para acomodar texto
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
        /*para meter datos a la DB*/
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creando test...");
        progressDialog.setCanceledOnTouchOutside(false);
        sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);//abro credenciales de SESION
        id=sharedPreferences.getString("id","");//para obtener el id del usuario*/
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
    /*****************************BOTON guarda en la db todos los datos recabados*/
    public void confirmar(View v){
        if(!temas.getText().toString().equals("")){
            guardarTest();
        }else{
            Toast.makeText(getApplicationContext() , "No puede estar vacío",   Toast.LENGTH_SHORT).show();
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
    /**********************************************FUNCIOONES SECUNDARIAS*************************************************************************************/

    /***********************************************Funcion para guardarTest en DB*/
    public void guardarTest(){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLguardarTest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);//aqui se obtiene la respuesta de servicio PHP
                    boolean existe=obj.getBoolean("valida");
                    String msj=obj.getString("mensaje");
                    if(existe){
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext() , msj,   Toast.LENGTH_SHORT).show();//se creó test
                        iniciarPaginaMain();
                    }else{
                        //si no existe decir que ya existe ese Nombre
                        Toast.makeText(getApplicationContext() , msj,   Toast.LENGTH_SHORT).show();//no se creó
                        progressDialog.hide();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    progressDialog.hide();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext() , "No se pudo crear, por favor inténtalo nuevamente",   Toast.LENGTH_SHORT).show();
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data=new HashMap<>();
                data.put("ID_PROFESOR",id);
                data.put("nombreTest",nombreTest);
                data.put("passwordTest",passwordTest);
                data.put("duracion",duracion);
                data.put("temas",temas.getText().toString());
                return  data;
            }
        };
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        progressDialog.show();
        requestQueue.add(stringRequest);
    }
    /**********************************************************************************FUNCION PARA INICIAR UNA PAGINA MAIN*/
    public void iniciarPaginaMain(){
        Intent intent=new Intent(Temas.this,MainActivity.class);
        startActivity(intent);
        finish();
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
