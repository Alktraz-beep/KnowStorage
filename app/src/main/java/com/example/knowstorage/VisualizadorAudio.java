package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VisualizadorAudio extends AppCompatActivity {
    Button playpausa;
    MediaPlayer mp;
    private String nombreAlumno;
    private String calificacion;
    private String audioLink;
    private String descripcion;
    TextView nombreAlumnoTV;
    TextView descripcionTV;
    TextView calificacionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizador_audio);
        playpausa=(Button) findViewById(R.id.pausaplay);
        /*obtener el intento con las variables*/
        nombreAlumno=getIntent().getStringExtra("nombreAlumno");
        calificacion=getIntent().getStringExtra("calificacion");
        audioLink=getIntent().getStringExtra("audioLink");
        descripcion=getIntent().getStringExtra("descripcion");
        /********ponerlos en el view********/
        nombreAlumnoTV=findViewById(R.id.nombreAudio);
        nombreAlumnoTV.setText("Alumno: "+nombreAlumno);
        descripcionTV=findViewById(R.id.descripcionAudio);
        descripcionTV.setText("Descripción: "+descripcion);
        calificacionTV=findViewById(R.id.calificacionAudio);
        calificacionTV.setText("Calificación: "+calificacion);
        /*asignar url*/
        mp=MediaPlayer.create(this, Uri.parse(audioLink));
    }
    public  void playPausa(View v){
        if(mp.isPlaying()){
            mp.pause();
            playpausa.setBackgroundResource(R.drawable.pausa);
            Toast.makeText(getApplicationContext() , "Pausa",   Toast.LENGTH_SHORT).show();
        }else{
            mp.start();
            playpausa.setBackgroundResource(R.drawable.play);
            Toast.makeText(getApplicationContext() , "Play",   Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.pause();
    }
}