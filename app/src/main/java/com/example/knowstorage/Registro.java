package com.example.knowstorage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    private String URLregistroFBGOOGLE="https://leanonmecc.com/wp-content/plugins/buscar_audio/registroFBGOOGLE.php";//url de script para registro
    private String id;
    private String userName;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        id=getIntent().getExtras().getString("id");
        userName=getIntent().getExtras().getString("nombre");
        requestQueue= Volley.newRequestQueue(getApplicationContext());


    }
    /*Para Boton de alumno*/
    public void registrarAlumno(View view){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLregistroFBGOOGLE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);
                    boolean registrado=obj.getBoolean("valida");
                    String msj=obj.getString("mensaje");
                    if(registrado==true){ //si se logro registrar
                        Intent intent = new Intent(Registro.this, Success.class);
                        Toast.makeText(Registro.this, msj, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }else{//si no mandarlo al inicio
                        Intent intent = new Intent(Registro.this, MainActivity.class);
                        Toast.makeText(Registro.this, msj, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }

                }catch (JSONException e){
                        e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Registro.this, "Error Registro: "+error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data=new  HashMap<>();
                data.put("id",id);
                data.put("nombre",userName);
                data.put("rol","a");
                return  data;
            }
        };
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        requestQueue.add(stringRequest);
    }
    /*Para boton de profesor*/
    public void registrarProfesor(View view){

    }
}
