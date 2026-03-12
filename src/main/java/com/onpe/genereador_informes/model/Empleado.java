package com.onpe.genereador_informes.model;

public class Empleado {
    private int id_empleado;
    private String dni;
    private String nombres;
    private String apellidos;
    private Odpe odpe;

    public Empleado(){}

    public Empleado(int id_empleado, String dni, String nombres, String apellidos) {
        this.id_empleado = id_empleado;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public int getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(int id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Odpe getOdpe() {
        return odpe;
    }

    public void setOdpe(Odpe odpe) {
        this.odpe = odpe;
    }

    @Override
    public String toString(){
        return "ID: " + id_empleado + " | DNI: " + dni + " | Nombres: " + nombres + " | Apellidos: " + apellidos;
    }
}
