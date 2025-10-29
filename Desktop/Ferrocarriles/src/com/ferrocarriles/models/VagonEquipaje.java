package com.ferrocarriles.models;

import java.util.ArrayList;
import java.util.List;

public class VagonEquipaje {
    // Constructor extendido para compatibilidad con ServidorHTTP
    public VagonEquipaje(int idVagon, int idTren, boolean disponibilidad) {
        this.idVagon = idVagon;
        this.maletas = new ArrayList<>();
        this.disponibilidad = disponibilidad;
    }

    private int idVagon;
    private List<Maleta> maletas;
    private boolean disponibilidad;

    public VagonEquipaje(int idVagon) {
        this.idVagon = idVagon;
        this.maletas = new ArrayList<>();
        this.disponibilidad = true;
    }

    public int getIdVagon() {
        return idVagon;
    }

    public List<Maleta> getMaletas() {
        return maletas;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    @Override
    public String toString() {
        return "VagonEquipaje{" +
                "idVagon=" + idVagon +
                ", maletas=" + maletas +
                ", disponibilidad=" + disponibilidad +
                '}';
    }
}
