package com.onpe.genereador_informes.model;

public class Encargado {
    private int idEncargado;
    private String nombre;
    private String cargo;

    public Encargado() {}

    public Encargado(int idEncargado, String nombre, String cargo) {
        this.idEncargado = idEncargado;
        this.nombre = nombre;
        this.cargo = cargo;
    }

    public int getIdEncargado() {
        return idEncargado;
    }

    public void setIdEncargado(int idEncargado) {
        this.idEncargado = idEncargado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
