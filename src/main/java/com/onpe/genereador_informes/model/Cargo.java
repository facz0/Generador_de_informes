package com.onpe.genereador_informes.model;

import java.util.ArrayList;
import java.util.List;

public class Cargo {
    private int idCargo;
    private String nombreCargo;
    private List<Actividad> listaActividades;

    public Cargo(){}

    public Cargo(int idCargo, String nombreCargo) {
        this.idCargo = idCargo;
        this.nombreCargo = nombreCargo;
        this.listaActividades = new ArrayList<>();
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public List<Actividad> getListaActividades() {
        return listaActividades;
    }

    @Override
    public String toString() {
        return "idCargo: " + idCargo +
                " | Cargo: " + nombreCargo +
                " | Actividades: " + listaActividades;
    }

    public void setListaActividades(List<Actividad> listaActividades) {
        this.listaActividades = listaActividades;
    }

    public void agregarActividad(Actividad actividad){
        this.listaActividades.add(actividad);
    }
}
