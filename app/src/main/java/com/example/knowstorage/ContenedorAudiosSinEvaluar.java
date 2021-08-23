package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContenedorAudiosSinEvaluar extends AppCompatActivity implements  AdapterView.OnItemClickListener{
    ListView listViewAudios;
    List<Audio> listaAudios=new ArrayList<Audio>();
    ArrayAdapter listAdapterAudios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contenedoraudiossinevaluar);
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File dir=contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        listViewAudios=findViewById(R.id.listaAudios);
        listViewAudios.setOnItemClickListener( this);
        //File dir = new File(dirPath);
        File[] filelist = dir.listFiles();
        String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();
        }
        listAdapterAudios=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,theNamesOfFiles);
        listViewAudios.setAdapter(listAdapterAudios);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*ir a nueva activity VisualizadorAudio**/
        Intent intent = new Intent(this,Reproductor.class);
        intent.putExtra("nombre",listAdapterAudios.getItem(position).toString());
        startActivity(intent);
        //finish();
    }
}