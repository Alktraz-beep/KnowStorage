package com.example.knowstorage;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.Profile;

public class Success extends AppCompatActivity {
    TextView etBienvenido;
    String bienvenido="¡Bienvenido ";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success);
        etBienvenido=findViewById(R.id.textView);
        etBienvenido.setText(bienvenido+"!");//+nombre

    }
}
