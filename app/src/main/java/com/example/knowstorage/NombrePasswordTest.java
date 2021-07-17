/*
* Esta clase abre una pagina para añadir el nombre,contraseña y duracion del test
* no usa credenciales pero si los datos del intent */
package com.example.knowstorage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NombrePasswordTest extends AppCompatActivity {


    EditText etNombre;
    EditText etPassword;
    EditText etTimeMin;
    EditText etTimeSec;
    String nombreTest;
    String passwordTest;
    String timeTest="";
    String min;
    String sec;
    ProgressDialog progressDialog;
    String URLValidarPassword="https://leanonmecc.com/wp-content/plugins/buscar_audio/validarNombreTest.php";
    RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nombrepassword);
        etNombre=findViewById(R.id.nombreTest);
        etPassword=findViewById(R.id.passwordText);
        etTimeMin=findViewById(R.id.minutos);
        etTimeSec=findViewById(R.id.segundos);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Preparando datos, No cierres la ventana");
        progressDialog.setCanceledOnTouchOutside(false);

    }
    /**************************************************************BOTONES Y OBJETOS DEL LAYOUT**********************************************************************/
    /*********************************Boton para ir a pagina que saca temas del profesor*/
    public void sacarTemas(View v){
        nombreTest=etNombre.getText().toString();//obtenemos nombre
        passwordTest=etPassword.getText().toString();//password
        sec=(etTimeSec.getText().toString());//duracion en sec
        min=(etTimeMin.getText().toString());//duracion en min
        if(!nombreTest.equals("") && !passwordTest.equals("") && !sec.equals("") && !etTimeMin.equals("") ){
            if(ponerTiempo(Integer.parseInt(sec),Integer.parseInt(min)))//si la duracion es correcta
                validarNombreTest();
            else
                Toast.makeText(getApplicationContext() , "Duración inválida",   Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext() , "Todos los campos deben estar llenos",   Toast.LENGTH_SHORT).show();
        }

    }
    public boolean ponerTiempo(int sec,int min){
        boolean resp=false;
        if(min>=60 || sec>=60){
            resp=false;
        }else{
            resp=true;
            int tiempo=(min*60+sec);
            timeTest=new String(Float.toString( (float)tiempo/60));
        }

        return resp;
    }
    /*************************************Funciones Secundarias******************************************************************/
    /***********************************Dirige a pagina de temas donde dice los temas a poner*/
    public void iniciarPaginaTemas(){
        Intent intent=new Intent(this,Temas.class);
        Toast.makeText(getApplicationContext() , etNombre.getText()+" "+etPassword.getText()+" "+timeTest,   Toast.LENGTH_SHORT).show();
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
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext() , "Datos válidos de test",   Toast.LENGTH_SHORT).show();//si no existe avanzar
                        iniciarPaginaTemas();
                    }else{
                        //si no existe decir que ya existe ese Nombre
                        Toast.makeText(getApplicationContext() , "El nombre ya existe elige otro",   Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext() , "Los datos no se pudieron enviar, por favor ingresalos de nuevo",   Toast.LENGTH_SHORT).show();
                progressDialog.hide();
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
        progressDialog.show();
        requestQueue.add(stringRequest);
    }
}

