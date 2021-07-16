/*
*Clase Registro que hace que se puedan registrar de acuerdo a su sesion de fb y una vez hecho los manda a Success
* Credenciales: Sesion
* variables id,password y rol
*  */
package com.example.knowstorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    private String URLregistroFBGOOGLE="https://leanonmecc.com/wp-content/plugins/buscar_audio/registroFBGOOGLE.php";//url de script para registro
    private String id;
    private String userName;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;//para la espera
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        id=getIntent().getExtras().getString("id");
        userName=getIntent().getExtras().getString("nombre");
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        sharedPreferences=getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registrando ");
        progressDialog.setCanceledOnTouchOutside(false);

    }
    /*****************************Para Boton de alumno*/
    public void registrarAlumno(View view){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLregistroFBGOOGLE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);
                    boolean registrado=obj.getBoolean("valida");
                    String msj=obj.getString("mensaje");
                    progressDialog.hide();
                    if(registrado==true){ //si se logro registrar
                        Toast.makeText(Registro.this, msj, Toast.LENGTH_SHORT).show();
                        subirPreferences(id,userName,"a");
                        iniciarPaginaAlumno();//aqui debo enviar hacia alumno.class en lugar de Success que es del profesor
                    }else{//si no mandarlo al inicio
                        Toast.makeText(Registro.this, msj, Toast.LENGTH_SHORT).show();
                        iniciarPaginaMain();
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
        progressDialog.show();
    }
    /****************************Para boton de profesor*/
    public void registrarProfesor(View view){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLregistroFBGOOGLE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);
                    boolean registrado=obj.getBoolean("valida");
                    String msj=obj.getString("mensaje");
                    progressDialog.hide();
                    if(registrado==true){ //si se logro registrar
                        Toast.makeText(Registro.this, msj, Toast.LENGTH_SHORT).show();
                        subirPreferences(id,userName,"p");
                        iniciarPaginaSuccess();
                    }else{//si no mandarlo al inicio
                        Toast.makeText(Registro.this, msj, Toast.LENGTH_SHORT).show();
                        iniciarPaginaMain();
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
                data.put("rol","p");//se inserta pero con p de profesor
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
        progressDialog.show();
    }
    /********************************************FUNCIONES SECUNDARIAS****************************************************/
    /*Inicia la paggina main de Success*/
    public void iniciarPaginaSuccess(){
        Intent intent = new Intent(Registro.this, Success.class);
        startActivity(intent);
        finish();
    }
    /*Inicia la paggina main de login*/
    public  void iniciarPaginaMain(){
        Intent intent = new Intent(Registro.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    /*sube las preferencias una vez registrado*/
    public void subirPreferences(String u,String p,String r){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("id",u);
        editor.putString("password",p);
        editor.putString("rol",r);
        editor.commit();
    }
    /*inicia una pagina diferente de alumno*/
    public void iniciarPaginaAlumno(){
        Intent intent = new Intent(Registro.this, Alumno.class);
        startActivity(intent);
        finish();
    }
    /*cuando se destruye si se interrumpe hacer que desloggee de facebook*/

    @Override
    protected void onStop() {
        LoginManager.getInstance().logOut();
        super.onStop();
    }
}
