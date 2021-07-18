/**En esta clase salen todos los tests guardados por el profesor
 * si usa credenciales solo es consulta de audios y de tests*/
package com.example.knowstorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class MisTest extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView tests;
    List<Test> listaTest=new ArrayList<Test>();
    ListAdapter listAdapter;
    /*para PHP*/
    String URLobtenerTests="https://leanonmecc.com/wp-content/plugins/buscar_audio/obtenerTests.php";
    RequestQueue requestQueue;
    String nombreProfesor;
    SharedPreferences sharedPreferences;//para obtener el nombre del profesor o ID
    /*para dialog*/
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mistest);
        tests=findViewById(R.id.tests);
        tests.setOnItemClickListener(this);
        /*PARA SERVICIOS PHP*/
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);//abro credenciales de SESION
        nombreProfesor=sharedPreferences.getString("id","");//para obtener el id del usuario o "" de lo contrario*/
        /*PARA DIALOG*/
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Cargando tus test no cierres la ventana");
        progressDialog.setCanceledOnTouchOutside(false);
        /*aqui se tiene que hacer un for con cada test que hizo el profe*/
        obtenerTests();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Que pasa si se clickea un item de ahi*/
        Intent intent=new Intent(MisTest.this,Audios.class);
        intent.putExtra("nombreTest",listAdapter.getItem(position).getNombreTest());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /********************************************************FUNCIONES SECUNDARIAS************************************************************/
    /**************************funcion que  obtiene los test de forma visual*/
    public  void obtenerTests(){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLobtenerTests, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);//aqui se obtiene la respuesta de servicio PHP
                    boolean valida=obj.getBoolean("valida");
                    if(valida){
                        progressDialog.hide();

                        /*Aqui se obtiene el array*/
                        JSONArray arrayTests=obj.getJSONArray("tests");
                        for(int i=0;i<arrayTests.length();i++){
                            JSONObject test=(JSONObject) arrayTests.get(i);
                            listaTest.add(new Test(test.getString("NOMBRE_TEST"),test.getString("PASSWORD_TEST"),test.getString("DURACION")));
                        }
                        Toast.makeText(getApplicationContext() , "Aqui están sus tests",   Toast.LENGTH_SHORT).show();

                        /*añadirlos al Listview después de haberlos insertado*/
                        listAdapter=new ListAdapter(MisTest.this,R.layout.item_row,listaTest);
                        tests.setAdapter(listAdapter);
                    }else{
                        Toast.makeText(getApplicationContext() , "Usted no tiene aun tests",   Toast.LENGTH_SHORT).show();
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
                data.put("nombreProfesor",nombreProfesor);
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
