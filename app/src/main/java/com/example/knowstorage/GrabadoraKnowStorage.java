package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class GrabadoraKnowStorage extends AppCompatActivity {
    //botones
    Button stop;
    Button playpause;
    Button grabar;
    Button guardar;
    String nombreAudio;
    EditText nombreEt;
    MediaRecorder mediaRecorder;//para grabar
    MediaPlayer mediaPlayer;//para reproducir
    private static int MICROPHONE_PERMISSON=200;
    public static final int STORAGE_PERMISION_CODE=4665;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grabadoraknogstorage);
        nombreEt=findViewById(R.id.nombreAudioEt);
        stop=findViewById(R.id.button12);
        grabar=findViewById(R.id.button11);
        playpause=findViewById(R.id.button13);
        guardar=findViewById(R.id.button14);
        /**permisos*/
        if(isMicrophonePresent()) {
            getMicrophnePermission();
        }
        requestStoragePermission();
    }
    /*****************************************************BOTON GRABAR*/
    public void grabarGrabacion(View view){
        nombreAudio=nombreEt.getText().toString();
        if(!nombreAudio.equals("")){
            try{
                //mediaPlayer=null;

                mediaRecorder=new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
                mediaRecorder.setOutputFile(getRecordingPath(nombreAudio));
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.prepare();
                mediaRecorder.start();
                mediaPlayer=null;
                nombreEt.setEnabled(false);//deja de poder ser editado
                stop.setEnabled(true);
                grabar.setEnabled(false);
                Toast.makeText(getApplicationContext() , "Se ha comenzado a grabar",   Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext() , "Ha ocurrido un error"+e.getMessage(),   Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext() , "Primero ponle nombre a tu audio",   Toast.LENGTH_SHORT).show();
        }
    }
    /****************************para el boton de stop **/
    public void pararGrabacion(View view){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
        mediaPlayer=MediaPlayer.create(GrabadoraKnowStorage.this, Uri.parse(getRecordingPath(nombreAudio)));//le pone el file al mediaplayer
        Toast.makeText(getApplicationContext() , "Se ha terminado de grabar",   Toast.LENGTH_SHORT).show();
        stop.setEnabled(false);
        grabar.setEnabled(true);
    }
    /*****************************************Para reproducir audio***/
    public void playpause(View view){
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
    /*************************************************************************Guardar audio*/
    public void guardarGrabacion(View v){
        //Intent intent = new Intent(this, GrabadoraKnowStorage.class);
        finish();
    }
    /**********************************************************Metodos secundarios*/
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
    public void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISION_CODE);
        }
    }
    /*Guardamos en su carpeta de musica*/
    private String getRecordingPath(String nombre){
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File music_dir=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file= new File(music_dir,nombre+".wav");//lo gugarda en el path de android music
        return file.getPath();
    }
}