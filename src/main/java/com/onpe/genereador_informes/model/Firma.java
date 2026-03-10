package com.onpe.genereador_informes.model;

public class Firma {
    private int id_firma;
    private String nombreEncargado;
    private String descripcion;

    public Firma(int id_firma, String nombreEncargado, String descripcion){
        this.id_firma = id_firma;
        this.nombreEncargado = nombreEncargado;
        this.descripcion = descripcion;
    }

    public int getId_firma() {
        return id_firma;
    }

    public void setId_firma(int id_firma) {
        this.id_firma = id_firma;
    }

    public String getNombreEncargado() {
        return nombreEncargado;
    }

    public void setNombreEncargado(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString(){
        return "ID: " + id_firma + " | Encargado: " + nombreEncargado + " | Descripción: " + descripcion;
    }
}
