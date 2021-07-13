/*
* Success class es la home de la aplicacion donde sera relativa al alumno o profesor aqui solo ingresan usuarios loggeados
* credenciales: Sesion
* Variables: id,password,rol
* */
package com.example.knowstorage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

public class Success extends AppCompatActivity {
    TextView etBienvenido;
    String bienvenido="¡Bienvenido ";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success);
        etBienvenido=findViewById(R.id.textView);
        etBienvenido.setText(bienvenido+"!");//nombre
        sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
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
    /*****************************************FUNCIONES SECUNDARIAS**********************************************************/
    /*pone los preferences en "" para que lo valide Success y Main*/
    public void limpiarPreferences(){
        sharedPreferences.edit().clear().apply();
        iniciarPaginaMain();
    }
    /*Valida si tienen algo los preferences y si los tiene se mantiene sino se va a Main*/
    public  void validarSesion(){
        String u=sharedPreferences.getString("id","");//dame id si esta vacia dame ""
        String p=sharedPreferences.getString("password","");//dame password si esta vacia dame ""
        String r=sharedPreferences.getString("rol","");//dame rol si esta vacia dame ""
        if(u.equals("") && p.equals("") && r.equals("")){//si esta vacio
            iniciarPaginaMain();
        }
    }
    /*Abre la pagina Main inicial*/
    public void iniciarPaginaMain(){
        Intent intent=new Intent(Success.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
