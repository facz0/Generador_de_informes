package com.onpe.genereador_informes.model;

public class Actividad {
    private int idActividad;
    private int idCargoArea;
    private String descripcion;

    public Actividad() {}

    public Actividad(int idActividad, int idCargoArea, String descripcion) {
        this.idActividad = idActividad;
        this.idCargoArea = idCargoArea;
        this.descripcion = descripcion;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdCargoArea() {
        return idCargoArea;
    }

    public void setIdCargoArea(int idCargoArea) {
        this.idCargoArea = idCargoArea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
