package com.onpe.genereador_informes.model;

public class Gerencia {
    private int idGerencia;
    private String nombreGerencia;

    public Gerencia() {}

    public Gerencia(int idGerencia, String nombreGerencia) {
        this.idGerencia = idGerencia;
        this.nombreGerencia = nombreGerencia;
    }

    public int getIdGerencia() {
        return idGerencia;
    }

    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    public String getNombreGerencia() {
        return nombreGerencia;
    }

    public void setNombreGerencia(String nombreGerencia) {
        this.nombreGerencia = nombreGerencia;
    }

    @Override
    public String toString() {
        return nombreGerencia;
    }
}
