/*
* Clase de el modulo de audio contiene sus caracteristicas a mostrar de un audio como NOMBRE del alumno, CALIFICACION, AUDIO y DESCRIPCION,*/
package com.example.knowstorage;

public class Audio {
    private String nombreAlumno;
    private String calificacion;
    private String audioLink;
    private String descripcion;


    public Audio(String nombreAlumno, String calificacion, String audioLink, String descripcion) {
        this.nombreAlumno = nombreAlumno;
        this.calificacion = calificacion;
        this.audioLink = audioLink;
        this.descripcion = descripcion;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
