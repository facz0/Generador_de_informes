package com.onpe.genereador_informes.model;

public class Odpe {
    private int idOdpe;
    private String nombreOdpe;

    public Odpe() {}

    public Odpe(int idOdpe, String nombreOdpe) {
        this.idOdpe = idOdpe;
        this.nombreOdpe = nombreOdpe;
    }

    public int getIdOdpe() {
        return idOdpe;
    }

    public void setIdOdpe(int idOdpe) {
        this.idOdpe = idOdpe;
    }

    public String getNombreOdpe() {
        return nombreOdpe;
    }

    public void setNombreOdpe(String nombreOdpe) {
        this.nombreOdpe = nombreOdpe;
    }

    @Override
    public String toString() {
        return "Odpe{" +
                "idOdpe=" + idOdpe +
                ", nombreOdpe='" + nombreOdpe + '\'' +
                '}';
    }
}
