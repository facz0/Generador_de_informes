package com.onpe.genereador_informes.model;

import java.time.LocalDate;

public class Informe {
    private int idInforme;
    private Encargado encargado;
    private Personal personal;
    private String periodo;
    private String mes;
    private String anio;
    private LocalDate fechaGeneracion;

    public Informe() {}

    public Informe(int idInforme, Encargado encargado, Personal personal, String periodo, String mes, String anio, LocalDate fechaGeneracion) {
        this.idInforme = idInforme;
        this.encargado = encargado;
        this.personal = personal;
        this.periodo = periodo;
        this.mes = mes;
        this.anio = anio;
        this.fechaGeneracion = fechaGeneracion;
    }

    public int getIdInforme() {
        return idInforme;
    }

    public void setIdInforme(int idInforme) {
        this.idInforme = idInforme;
    }

    public Encargado getEncargado() {
        return encargado;
    }

    public void setEncargado(Encargado encargado) {
        this.encargado = encargado;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}
