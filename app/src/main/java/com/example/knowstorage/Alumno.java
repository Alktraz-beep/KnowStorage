/*
* Clase Alumno que permite que el alumno envie un audio con la calificación*/
package com.example.knowstorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class Alumno extends AppCompatActivity {
    /***Información de sesion***/
    SharedPreferences sharedPreferences;
    private String id;
    private String password;
    TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alumno);
        textView=findViewById(R.id.datos);
        sharedPreferences=getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        id=sharedPreferences.getString("id","");
        textView.setText("¡Bienvenido!\nID: "+id);
        validarSesion();
    }
    /***************************Boton de logout/salir*/
    public void logout(View v){
        AccessToken token;
        token= AccessToken.getCurrentAccessToken();
        if(token!=null){
            //esta iniciado con FB
            LoginManager.getInstance().logOut();
        }
        limpiarPreferences();
    }
    /*********************************Boton para ir a pagina que pide nombre y contraseña de un test**/
    public void aplicarTest(View v){
        Intent intent = new Intent(this, SesionTest.class);
        startActivity(intent);
    }
    /************************************************************Boton que muestra todos los test*/
    public void verMisAudios(View v){
        Intent intent = new Intent(this, Audios.class);
        startActivity(intent);
    }
    /*************************************************************BOTON que muestra la storage(grabar sin evaluar y escucharlos)*/
    public void verMiStorage(View v){
        Intent intent = new Intent(this, Storage.class);
        startActivity(intent);
    }
    /*******************************Funciones Secundarias*******************************************************************************************/
    public void limpiarPreferences(){
        sharedPreferences.edit().clear().apply();
        iniciarPaginaMain();
    }
    public  void iniciarPaginaMain(){
        Intent intent = new Intent(Alumno.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public  void validarSesion(){
        String u=sharedPreferences.getString("id","");//dame id si esta vacia dame ""
        String p=sharedPreferences.getString("password","");//dame password si esta vacia dame ""
        String r=sharedPreferences.getString("rol","");//dame rol si esta vacia dame ""
        if(u.equals("") && p.equals("") && r.equals("")){//si esta vacio
            iniciarPaginaMain();
        }
    }
}
