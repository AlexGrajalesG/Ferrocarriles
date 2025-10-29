package com.ferrocarriles.models;


public class Equipaje {
    private int idEquipaje;
    private double peso;
    private int pasajeroId;
    private String tipo;

    public Equipaje() {
        this.tipo = "Ligero";
    }

    public Equipaje(int idEquipaje, double peso, int pasajeroId) {
        this.idEquipaje = idEquipaje;
        this.peso = peso;
        this.pasajeroId = pasajeroId;
        this.tipo = "Ligero";
    }

    public boolean validarPeso() {
        final double PESO_MAXIMO = 23.0; 

        if (peso <= PESO_MAXIMO) {
            System.out.println("Equipaje " + idEquipaje + " vlido: " + peso + " kg");
            return true;
        } else {
            System.out.println("ERROR: Equipaje " + idEquipaje + " excede peso mximo: " + peso + " kg (mx. "
                    + PESO_MAXIMO + " kg)");
            return false;
        }
    }

    // Getters y Setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdEquipaje() {
        return idEquipaje;
    }

    public void setIdEquipaje(int idEquipaje) {
        this.idEquipaje = idEquipaje;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getPasajeroId() {
        return pasajeroId;
    }

    public void setPasajeroId(int pasajeroId) {
        this.pasajeroId = pasajeroId;
    }

    @Override
    public String toString() {
        return "Equipaje{" +
                "idEquipaje=" + idEquipaje +
                ", peso=" + peso +
                ", pasajeroId=" + pasajeroId +
                '}';
    }
}