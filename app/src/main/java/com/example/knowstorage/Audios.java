/*
*Clase para poner lista de audios con diferente audio de los TEST de PROFESORES
* no tiene credenciales */
package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Audios extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView audios;
    List<Audio> listaAudios=new ArrayList<Audio>();
    ListAdapterAudios listAdapterAudios;
    /*servicio PHP*/
    String URLobtenerAudiosP="https://leanonmecc.com/wp-content/plugins/buscar_audio/obtenerAudios.php";//url de servicio php
    String URLobtenerAudiosA="https://leanonmecc.com/wp-content/plugins/buscar_audio/obtenerAudiosAlumno.php";//url de servicio php
    RequestQueue requestQueue;
    String nombreTest;
    /*para dialog*/
    ProgressDialog progressDialog;
    String variable="";
    /*Para obtener datos por el rol*/
    String rol;
    String id;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audios);
        nombreTest=getIntent().getStringExtra("nombreTest");//aqui va el nombre del test pasandolo de una activity a otra
        audios=findViewById(R.id.audios);//buscamos la listview
        audios.setOnItemClickListener(this);//para que sea clickeable cada item
        /*servicio php*/
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        /*dialog*/
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Cargando audios no cierres la ventana");
        progressDialog.setCanceledOnTouchOutside(false);
        /*para obtener la lista de audios*/
        sharedPreferences=getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        rol=sharedPreferences.getString("rol","");
        id=sharedPreferences.getString("id","");
        if(rol.equals("p")){
            variable=nombreTest;
            obtenerAudios(URLobtenerAudiosP);
        }else if(rol.equals("a")){
            variable=id;
            obtenerAudios(URLobtenerAudiosA);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*ir a nueva activity VisualizadorAudio**/
        Intent intent = new Intent(Audios.this,VisualizadorAudio.class);
        intent.putExtra("nombreAlumno",listAdapterAudios.getItem(position).getNombreAlumno());
        intent.putExtra("audioLink",listAdapterAudios.getItem(position).getAudioLink());
        intent.putExtra("calificacion",listAdapterAudios.getItem(position).getCalificacion());
        intent.putExtra("descripcion",listAdapterAudios.getItem(position).getDescripcion());
        startActivity(intent);
        finish();
    }
    /***********************************************FUNCIONES SECUNDARIAS*******************************************/

    /*busca servicio php y muestra los resultados de audios*/
    public void obtenerAudios(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);//aqui se obtiene la respuesta de servicio PHP
                    boolean valida=obj.getBoolean("valida");
                    if(valida){
                        progressDialog.hide();

                        /*Aqui se obtiene el array*/
                        JSONArray arrayAudios=obj.getJSONArray("audios");
                        for(int i=0;i<arrayAudios.length();i++){
                            JSONObject audio=(JSONObject) arrayAudios.get(i);//obtiene cada ifo de cada audio del array
                            listaAudios.add(new Audio(audio.getString("NOMBRE_ALUMNO"),audio.getString("CALIFICACION"),audio.getString("AUDIO"),audio.getString("DESCRIPCION")));
                        }
                        Toast.makeText(getApplicationContext() , "Audios actuales",   Toast.LENGTH_SHORT).show();

                        /*añadirlos al Listview después de haberlos insertado*/
                        listAdapterAudios=new ListAdapterAudios(Audios.this,R.layout.item_audiorow,listaAudios);
                        audios.setAdapter(listAdapterAudios);
                    }else{
                        Toast.makeText(getApplicationContext() , "Aun no hay audios",   Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext() , "Hubo un error: "+e.getMessage(),   Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext() , "no se pudo obtener su información",   Toast.LENGTH_SHORT).show();
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data=new HashMap<>();
                data.put("nombreTest",variable);
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

}