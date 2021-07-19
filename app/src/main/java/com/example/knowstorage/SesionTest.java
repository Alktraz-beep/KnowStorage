package com.example.knowstorage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class SesionTest extends AppCompatActivity {
    EditText etNombreTest;
    EditText etPasswordTest;
    TextView rubrica;//es la suma de la duracion con temas trabajados
    private String duracion;//los duracion que regresa la string del request
    private String temas;//los duracion que regresa la string del request
    private String nombreTest;
    private String passwordTest;
    private String rubricaEvaluacion="";
    /*Para el servicio PHP*/
    private String URLobtenerTemas="https://leanonmecc.com/wp-content/plugins/buscar_audio/obtenerTemas.php";
    RequestQueue requestQueue;
    /*para el dialog*/
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sesion_test);
        //**botones y edittext y etc del layout
        etNombreTest=findViewById(R.id.nombreTest);
        etPasswordTest=findViewById(R.id.passwordTest);
        rubrica=findViewById(R.id.rubrica);
        /*dialog*/
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Cargando rúbrica");
        progressDialog.setCanceledOnTouchOutside(false);
        /*para php*/
        requestQueue= Volley.newRequestQueue(getApplicationContext());
    }
    /*************************Boton de buscar que llama a la funcion para transformar los temas*/
    public void buscar(View v){
        if(!etNombreTest.getText().toString().equals("") && !etPasswordTest.getText().toString().equals("")){
            nombreTest=etNombreTest.getText().toString();
            passwordTest=etPasswordTest.getText().toString();
            //llamada a request para servicio php
            obtenerTemas(URLobtenerTemas);
        }else{
            Toast.makeText(SesionTest.this, "No pueden estar vacíos los campos", Toast.LENGTH_SHORT).show();
        }
    }
    /************************Boton que si la variable String rubricaEvaluacion esta vacia es que aun no ha buscado los temas*/
    public  void  siguiente(View v){
        if(!rubricaEvaluacion.equals("")){
            /*ir a siguiente pagina de evaluacion*/
            Intent intent=new Intent(this, LTTA.class);
            intent.putExtra("duracion",duracion);
            intent.putExtra("nombreTest",nombreTest);
            intent.putExtra("temas",temas);
            startActivity(intent);
        }else{
            Toast.makeText(SesionTest.this, "No has buscado el test", Toast.LENGTH_SHORT).show();
        }
    }
    /*****************************************************************Funciones secundarias**********************************************************************/
    /*busca servicio php y muestra los resultados de test*/
    public void obtenerTemas(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);//aqui se obtiene la respuesta de servicio PHP
                    boolean valida=obj.getBoolean("valida");
                    if(valida){
                        progressDialog.hide();

                        /*Aqui se obtiene el array*/
                        JSONArray arrayAudios=obj.getJSONArray("rubrica");
                        //for(int i=0;i<arrayAudios.length();i++){
                            JSONObject test=(JSONObject) arrayAudios.get(0);//obtiene cada ifo de cada audio del array
                            //listaAudios.add(new Audio(audio.getString("NOMBRE_ALUMNO"),audio.getString("CALIFICACION"),audio.getString("AUDIO"),audio.getString("DESCRIPCION")));
                            duracion=test.getString("DURACION");
                            temas=test.getString("TEMAS");
                            rubricaEvaluacion="Duración(min): "+duracion+"\nTemas: "+transformarTemas(temas);
                            rubrica.setText(rubricaEvaluacion);
                        //}
                        Toast.makeText(getApplicationContext() , "Datos del test a evaluar",   Toast.LENGTH_SHORT).show();
                    }else if(!valida){
                        Toast.makeText(getApplicationContext() , "Contraseña o Nombre incorrecto",   Toast.LENGTH_SHORT).show();
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
                data.put("nombreTest",nombreTest);
                data.put("passwordTest",passwordTest);
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
    public String transformarTemas(String t){
        StringTokenizer stringTokenizer=new StringTokenizer(t," \n");
        String transformados="";
        while(stringTokenizer.hasMoreTokens()){
            String palabra=stringTokenizer.nextToken();
            if(palabra.contains("**tema")){
                transformados+=palabra+"\n";
            }
        }
        return transformados;
    }
}