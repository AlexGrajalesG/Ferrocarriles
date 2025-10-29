package com.ferrocarriles.models;

public class Maleta {
    private int idPasajero;
    private double peso; // Máximo 90kg

    public Maleta(int idPasajero, double peso) {
        this.idPasajero = idPasajero;
        this.peso = peso;
    }

    public int getIdPasajero() {
        return idPasajero;
    }

    public void setIdPasajero(int idPasajero) {
        this.idPasajero = idPasajero;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    @Override
    public String toString() {
        return "Maleta{" +
                "idPasajero=" + idPasajero +
                ", peso=" + peso +
                '}';
    }
}
