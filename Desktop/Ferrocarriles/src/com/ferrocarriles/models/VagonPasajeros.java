package com.ferrocarriles.models;

import java.util.ArrayList;
import java.util.List;

public class VagonPasajeros {
    // Constructor extendido para compatibilidad con ServidorHTTP
    public VagonPasajeros(int idVagon, int idTren, boolean disponibilidad, List<Asiento> asientos,
            List<Integer> pasajeros) {
        this.idVagon = idVagon;
        this.asientos = asientos != null ? asientos : new ArrayList<>();
        this.listaPasajeros = pasajeros != null ? pasajeros : new ArrayList<>();
        this.disponibilidad = disponibilidad;
    }

    private int idVagon;
    private List<Asiento> asientos;
    private List<Integer> listaPasajeros;
    private boolean disponibilidad;

    public VagonPasajeros(int idVagon) {
        this.idVagon = idVagon;
        this.asientos = new ArrayList<>();
        this.listaPasajeros = new ArrayList<>();
        this.disponibilidad = true;
        inicializarAsientos();
    }

    private void inicializarAsientos() {
        for (int i = 0; i < 2; i++)
            asientos.add(new Asiento("Piloto"));
        for (int i = 0; i < 4; i++)
            asientos.add(new Asiento("Personal"));
        for (int i = 0; i < 4; i++)
            asientos.add(new Asiento("Premium"));
        for (int i = 0; i < 8; i++)
            asientos.add(new Asiento("Ejecutiva"));
        for (int i = 0; i < 22; i++)
            asientos.add(new Asiento("Estandar"));
    }

    public int getIdVagon() {
        return idVagon;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public List<Integer> getListaPasajeros() {
        return listaPasajeros;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    @Override
    public String toString() {
        return "VagonPasajeros{" +
                "idVagon=" + idVagon +
                ", asientos=" + asientos +
                ", listaPasajeros=" + listaPasajeros +
                ", disponibilidad=" + disponibilidad +
                '}';
    }
}
