package com.onpe.genereador_informes.model;

public class CargoArea {
    private int idCargoArea;
    private Cargo cargo;
    private Area area;

    public CargoArea() {}

    public CargoArea(int idCargoArea, Cargo cargo, Area area) {
        this.idCargoArea = idCargoArea;
        this.cargo = cargo;
        this.area = area;
    }

    public int getIdCargoArea() {
        return idCargoArea;
    }

    public void setIdCargoArea(int idCargoArea) {
        this.idCargoArea = idCargoArea;
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
        String nCargo = cargo != null ? cargo.getNombreCargo() : "Sin Cargo";
        String nArea = area != null ? area.getNombreArea() : "Sin Area";
        return nCargo + " - " + nArea;
    }
}
