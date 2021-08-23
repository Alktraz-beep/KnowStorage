package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Storage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage);
    }
    /****************************************************Boton que manda a una pagina que graba la knowstorage (LTTA sin evaluar)*/
    public void grabarKnowstorage(View v){
        Intent intent = new Intent(this, GrabadoraKnowStorage.class);
        startActivity(intent);
    }
    /****************************************************Boton que manda a una pagina que graba la knowstorage (LTTA sin evaluar)*/
    public void verKnowstorage(View v){
        Intent intent = new Intent(this, ContenedorAudiosSinEvaluar.class);
        startActivity(intent);
    }
}