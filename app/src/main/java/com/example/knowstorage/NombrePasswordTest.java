package com.example.knowstorage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NombrePasswordTest extends AppCompatActivity {


    EditText etNombre;
    EditText etPassword;
    EditText etTime;
    String nombreTest;
    String passwordTest;
    String timeTest;
    String URLValidarPassword="https://leanonmecc.com/wp-content/plugins/buscar_audio/validarNombreTest.php";
    RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nombrepassword);
        etNombre=findViewById(R.id.nombreTest);
        etPassword=findViewById(R.id.passwordText);
        etTime=findViewById(R.id.timeTest);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

    }
    /*********************************Boton para ir a pagina que saca temas del profesor*/
    public void sacarTemas(View v){
        nombreTest=etNombre.getText().toString();//obtenemos nombre
        passwordTest=etNombre.getText().toString();//password
        timeTest=etTime.getText().toString();//duracion de las cajas de texto
        if(!nombreTest.equals("") && !passwordTest.equals("") && !timeTest.equals("")){
            validarNombreTest();
        }else{
            Toast.makeText(getApplicationContext() , "Todos los campos deben estar llenos",   Toast.LENGTH_SHORT).show();
        }

    }
    /*************************************Funciones Secundarias******************************************************************/
    /***********************************Dirige a pagina de temas donde dice los temas a poner*/
    public void iniciarPaginaTemas(){
        Intent intent=new Intent(this,Temas.class);
        Toast.makeText(getApplicationContext() , etNombre.getText()+" "+etPassword.getText()+" "+etTime.getText(),   Toast.LENGTH_SHORT).show();
        intent.putExtra("nombreTest",nombreTest);//envialas variables
        intent.putExtra("passwordTest",passwordTest);
        intent.putExtra("timeTest",timeTest);
        startActivity(intent);
    }
    /**********************************abre servicio php para revisar si existe el nombre en la base de datos y envia a la siguiente pagina*/
    public void validarNombreTest(){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URLValidarPassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj=new JSONObject(response);//aqui se obtiene la respuesta de servicio PHP
                    boolean existe=obj.getBoolean("existe");
                    if(!existe){
                        Toast.makeText(getApplicationContext() , "Test registrado con éxito",   Toast.LENGTH_SHORT).show();//si no existe avanzar
                        iniciarPaginaTemas();
                    }else{
                        //si no existe decir que ya existe ese Nombre
                        Toast.makeText(getApplicationContext() , "El nombre ya existe elige otro",   Toast.LENGTH_SHORT).show();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext() , "Los datos no se pudieron enviar, por favor ingresalos de nuevo",   Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data=new HashMap<>();
                data.put("nombreTest",nombreTest);
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
}
