/*
* Clase personalizada para mostrar los Tests*/
package com.example.knowstorage;

public class Test {
    private String nombreTest;
    private String passwordTest;
    private String duracion;


    public Test(String nombreTest, String passwordTest, String duracion) {
        this.nombreTest = nombreTest;
        this.passwordTest = passwordTest;
        this.duracion = duracion;

    }

    public String getNombreTest() {
        return nombreTest;
    }

    public void setNombreTest(String nombreTest) {
        this.nombreTest = nombreTest;
    }

    public String getPasswordTest() {
        return passwordTest;
    }

    public void setPasswordTest(String passwordTest) {
        this.passwordTest = passwordTest;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }
}
