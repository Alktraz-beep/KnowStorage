/*
* Clase Alumno que permite que el alumno envie un audio con la calificación*/
package com.example.knowstorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class Alumno extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alumno);
        sharedPreferences=getSharedPreferences("Sesion", Context.MODE_PRIVATE);
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
    /*******************************Funciones Secundarias*/
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
