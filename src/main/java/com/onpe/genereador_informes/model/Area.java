package com.onpe.genereador_informes.model;

public class Area {
    private int idArea;
    private String nombreArea;

    public Area(){}

    public Area(int idArea, String nombreArea){
        this.idArea = idArea;
        this.nombreArea = nombreArea;
    }

    public int getIdArea() {
        return this.idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getNombreArea() {
        return this.nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    @Override
    public String toString() {
        return "ID: " + idArea + " | Área: " + nombreArea;
    }
}
