package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Reproductor extends AppCompatActivity {
    String nombreAudio;
    TextView nombreAudioTV;
    Button playpausa;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reproductor);
        nombreAudioTV=findViewById(R.id.audioName);
        playpausa=findViewById(R.id.reproducirAudio);

        nombreAudio=getIntent().getStringExtra("nombre");
        nombreAudioTV.setText(nombreAudio);
        mp=MediaPlayer.create(this, Uri.parse(getRecordingPath(nombreAudio)));
    }
    public  void reproducepausa(View v){
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
    private String getRecordingPath(String nombre){
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File music_dir=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file= new File(music_dir,nombre);//lo gugarda en el path de android music
        return file.getPath();
    }
}