package com.example.knowstorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    CallbackManager callbackManager;
    RequestQueue requestQueue;
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

        /*saber hash*/
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.knowstorage",                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
        /**/
        /*para continuar con FB*/
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

                            if(respuesta==true){ //si ya esta solo mandarlo a la Success donde se mostrarán sus audios etc...
                                Intent intent = new Intent(MainActivity.this, Success.class);
                                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }else{//si no esta mandarlo a una pagina nueva donde diga si es profesor o alumno e insertarlo en la DB y despues al success
                                Intent intent = new Intent(MainActivity.this, Registro.class);
                                Toast.makeText(MainActivity.this, msj, Toast.LENGTH_SHORT).show();
                                intent.putExtra("id",idUsuario);
                                intent.putExtra("nombre",userName);
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
    /*para LOM*/
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
                        if(respuesta==true){
                            Intent intent = new Intent(MainActivity.this, Success.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this, "Usuario o contraseña inválido", Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    /*if (response.equals("success")) {
                        Intent intent = new Intent(MainActivity.this, Success.class);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("failure")) {
                        Toast.makeText(MainActivity.this, "Usuario o contraseña inválido", Toast.LENGTH_SHORT).show();
                    }*/
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
        }else{
            Toast.makeText(this, "No puede estar vacío ningun campo!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    public String getUser(){
        return this.user;
    }
}