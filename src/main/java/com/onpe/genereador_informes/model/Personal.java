package com.onpe.genereador_informes.model;

public class Personal {
    private int idPersonal;
    private String dni;
    private String nombre;
    private String apellido;
    private Odpe odpe;
    private Gerencia gerencia;
    private CargoArea cargoArea;
    private String estado;

    public Personal() {}

    public Personal(int idPersonal, String dni, String nombre, String apellido, Odpe odpe, Gerencia gerencia, CargoArea cargoArea, String estado) {
        this.idPersonal = idPersonal;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.odpe = odpe;
        this.gerencia = gerencia;
        this.cargoArea = cargoArea;
        this.estado = estado;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(int idPersonal) {
        this.idPersonal = idPersonal;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Odpe getOdpe() {
        return odpe;
    }

    public void setOdpe(Odpe odpe) {
        this.odpe = odpe;
    }

    public Gerencia getGerencia() {
        return gerencia;
    }

    public void setGerencia(Gerencia gerencia) {
        this.gerencia = gerencia;
    }

    public CargoArea getCargoArea() {
        return cargoArea;
    }

    public void setCargoArea(CargoArea cargoArea) {
        this.cargoArea = cargoArea;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Helper methods mapping from old Empleado model to smooth transition in views
    public String getNombres() {
        return nombre;
    }
    
    public void setNombres(String nombres) {
        this.nombre = nombres;
    }
    
    public String getApellidos() {
        return apellido;
    }
    
    public void setApellidos(String apellidos) {
        this.apellido = apellidos;
    }

    @Override
    public String toString() {
        return "ID: " + idPersonal + " | DNI: " + dni + " | Nombre: " + nombre + " | Apellido: " + apellido + " | Estado: " + estado;
    }
}
