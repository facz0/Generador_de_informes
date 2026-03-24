package com.onpe.genereador_informes.model;

import java.time.LocalDate;

public class Contrato {

    private int idContrato;
    private String numeroContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Personal personal;

    public Contrato (){}

    public Contrato(int idContrato, String numeroContrato, LocalDate fechaInicio, LocalDate fechaFin, Personal personal) {
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.personal = personal;
    }

    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    @Override
    public String toString() {
        return "idContrato: " + idContrato +
                " | numeroContrato: " + numeroContrato +
                " | fechaInicio: " + fechaInicio +
                " | fechaFin: " + fechaFin +
                " | personal: " + personal;
    }
}
