package com.onpe.genereador_informes.model;

public class Actividad {
    private int idActividad;
    private int idCargo;
    private String descripcion;

    public Actividad(int idActividad, int isCargo, String descripcion) {
        this.idActividad = idActividad;
        this.idCargo = isCargo;
        this.descripcion = descripcion;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
