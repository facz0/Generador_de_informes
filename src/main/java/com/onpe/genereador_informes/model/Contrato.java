package com.onpe.genereador_informes.model;

import java.time.LocalDate;

public class Contrato {

    private int idContrato;
    private String numeroContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Empleado empleado;
    private Cargo cargo;
    private Area area;

    public Contrato (){}

    public Contrato(int idContrato, String numeroContrato, LocalDate fechaInicio, LocalDate fechaFin,
                    String estado, Empleado empleado, Cargo cargo, Area area) {
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.empleado = empleado;
        this.cargo = cargo;
        this.area = area;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "idContrato: " + idContrato +
                " | numeroContrato: " + numeroContrato +
                " | fechaInicio: " + fechaInicio +
                " | fechaFin: " + fechaFin +
                " | estado: " + estado +
                " | empleado: " + empleado +
                " | cargo: " + cargo +
                " | area: " + area;
    }
}
