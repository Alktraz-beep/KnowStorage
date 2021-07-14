/*CLase principal que se encarga del inicio de sesion con 2 botones uno para inicio desde LOM y otro desde FB
credencial: Sesion
variables: id,password,rol
* */
package com.example.knowstorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText etUser,etPassword;
    private String user,password;
    private String URL="https://leanonmecc.com/wp-content/plugins/buscar_audio/ingresar1.php";
    private String URLvalidarDB="https://leanonmecc.com/wp-content/plugins/buscar_audio/validar.php";
    private ImageView profile;
    private LoginButton login;
    private TextView info;
    private TextView textView2;//el de abajo
    private String userName="user";
    private String idUsuario="user";
    ProgressDialog progressDialog;
    CallbackManager callbackManager;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user=password="";
        etUser=findViewById(R.id.etUser);
        etPassword=findViewById(R.id.etPassword);
        textView2=findViewById(R.id.textView2);
        login=findViewById(R.id.login_button);
        //login.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));//se añade permisos
        info=findViewById(R.id.info);
        callbackManager=CallbackManager.Factory.create();
        /*INICIALIZacion de request*/
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        sharedPreferences=getSharedPreferences("Sesion", Context.MODE_PRIVATE);//es el nombre de las credenciales de sesion
        /*validacion de sesion*/
        validarSesion();
        /*dialog de espera*/
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Procesando solicitud...");
        /**/
        /**********************************para continuar con FB********************************************************************************************/
        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //info.setText("User Id: "+loginResult.getAccessToken().getUserId());
                /*CODIGO SI SE INTENTA LOGGEAR CON FB*/
                /*Obtenemos su nombre*/
                Profile profile=Profile.getCurrentProfile();//se obtiene su
                userName=profile.getName();
                idUsuario=loginResult.getAccessToken().getUserId();
                /*verificar si esta en la DB */
                StringRequest stringRequest=new StringRequest(Request.Method.POST, URLvalidarDB, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            boolean respuesta=obj.getBoolean("validar");
                            String msj=obj.getString("mensaje");
                            progressDialog.hide();
                            if(respuesta==true){ //si ya esta solo mandarlo a la Success donde se mostrarán sus audios etc...
                                String rol=obj.getString("rol");
                                guardarPreferences(idUsuario,userName,rol);
                                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_SHORT).show();
                                iniciarPaginaSuccess();
                            }else{//si no esta mandarlo a una pagina nueva donde diga si es profesor o alumno e insertarlo en la DB y despues al success
                                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_SHORT).show();
                                iniciarPaginaRegistro();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: "+error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> data= new HashMap<>();
                        data.put("id",idUsuario);
                        data.put("nombre",userName);
                        return data;
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

            @Override
            public void onCancel() {
                info.setText("Cancel ");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Error"+error.toString());
            }
        });
    }
    /***************************************para LOM******************************************************************************************/
    public void login(View view){
        user=etUser.getText().toString().trim();
        password=etPassword.getText().toString().trim();
        if(!user.equals("") && !password.equals("") ){
            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj=new JSONObject(response);
                        boolean respuesta=obj.getBoolean("validar");

                        progressDialog.dismiss();
                        if(respuesta==true){
                            String rol=obj.getString("rol");
                            /*guardar preferences*/
                            guardarPreferences(user,password,rol);
                            Toast.makeText(MainActivity.this, "Bienvenid@", Toast.LENGTH_SHORT).show();
                            if(rol.equals("p")){
                                iniciarPaginaSuccess();//si es profesor
                            }else if(rol.equals("a")){
                                iniciarPaginaAlumno();
                            }

                        }else{
                            Toast.makeText(MainActivity.this, "Usuario o contraseña inválido", Toast.LENGTH_SHORT).show();
                            info.setText("Usuario o contraseña inválida");
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Error: "+error.toString().trim(), Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> data= new HashMap<>();
                    data.put("usuario",user);
                    data.put("password",password);
                    return data;
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
        }else{
            Toast.makeText(this, "No puede estar vacío ningun campo!", Toast.LENGTH_SHORT).show();
        }
    }
/****************************FUNCIONES SECUNDARIAS***************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    public String getUser(){
        return this.user;
    }
    /*Funcion que guarda usuario,pasword y rol*/
    public  void guardarPreferences(String u,String p,String r){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("id",u);
        editor.putString("password",p);
        editor.putString("rol",r);
        editor.commit();

    }
    /*funcion que valida si hay alguna preference*/
    public void validarSesion(){
        String u=sharedPreferences.getString("id","");//dame el id si no hay dame ""
        String p=sharedPreferences.getString("password","");//dame el pass si no hay dame ""
        String r=sharedPreferences.getString("rol","");//dame el rol si no hay dame ""
        if(!u.equals("") && !p.equals("") && !r.equals("")){//si estan con algo
            if(r.equals("p")){
                iniciarPaginaSuccess();//hacia succes si es profesor
            }else if(r.equals("a")){
                iniciarPaginaAlumno();
            }

        }//de lo contrario continua
    }
    /*Inicia la pagina success PARA PROFESORES*/
    public  void iniciarPaginaSuccess(){
        Intent intent = new Intent(MainActivity.this, Success.class);
        startActivity(intent);
        finish();
    }
    /*Inicia la pagina success PARA ALUMNOS*/
    public  void iniciarPaginaAlumno(){
        Intent intent = new Intent(MainActivity.this, Alumno.class);
        startActivity(intent);
        finish();
    }
    /*Inicia la pagina registro*/
    public  void  iniciarPaginaRegistro(){
        Intent intent = new Intent(MainActivity.this, Registro.class);
        intent.putExtra("id",idUsuario);
        intent.putExtra("nombre",userName);
        startActivity(intent);
        finish();
    }
}