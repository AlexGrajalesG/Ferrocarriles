package com.ferrocarriles.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Vagon segn diagrama de clases
 */
public class Vagon {
    private int idVagon;
    private String tipo; // "Pasajeros" o "Carga"
    private List<Asiento> listaAsientos; // Para pasajeros
    private List<Integer> listaPasajeros;
    private boolean disponibilidad;
    // Para vagones de carga
    private List<Maleta> listaMaletas; // Solo para tipo "Carga"

    public Vagon() {
        this.listaAsientos = new ArrayList<>();
        this.listaPasajeros = new ArrayList<>();
        this.listaMaletas = new ArrayList<>();
    }

    public Vagon(int idVagon, String tipo, boolean disponibilidad) {
        this.idVagon = idVagon;
        this.tipo = tipo;
        this.disponibilidad = disponibilidad;
        this.listaAsientos = new ArrayList<>();
        this.listaPasajeros = new ArrayList<>();
        this.listaMaletas = new ArrayList<>();
    }

    // Métodos de negocio
    public int contarDisponibles() {
        if ("Pasajeros".equalsIgnoreCase(tipo)) {
            int disponibles = 0;
            for (Asiento a : listaAsientos) {
                if (a.isDisponible())
                    disponibles++;
            }
            System.out.println("Vagón " + idVagon + " tiene " + disponibles + " asientos disponibles");
            return disponibles;
        } else {
            // Para carga, retorna espacio disponible en maletas
            int totalMaletas = listaMaletas != null ? listaMaletas.size() : 0;
            int maxMaletas = 2 * (listaPasajeros != null ? listaPasajeros.size() : 0);
            return maxMaletas - totalMaletas;
        }
    }

    public int getCapacidad() {
        if ("Pasajeros".equalsIgnoreCase(tipo)) {
            return listaAsientos != null ? listaAsientos.size() : 0;
        } else {
            // Para carga, capacidad máxima de maletas
            return 2 * (listaPasajeros != null ? listaPasajeros.size() : 0);
        }
    }

    private int idTren;

    public int getIdTren() {
        return idTren;
    }

    public void setIdTren(int idTren) {
        this.idTren = idTren;
    }

    // Getters y Setters
    public int getIdVagon() {
        return idVagon;
    }

    public void setIdVagon(int idVagon) {
        this.idVagon = idVagon;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Asiento> getListaAsientos() {
        return listaAsientos;
    }

    public void setListaAsientos(List<Asiento> listaAsientos) {
        this.listaAsientos = listaAsientos;
    }

    public List<Maleta> getListaMaletas() {
        return listaMaletas;
    }

    public void setListaMaletas(List<Maleta> listaMaletas) {
        this.listaMaletas = listaMaletas;
    }

    public List<Integer> getListaPasajeros() {
        return listaPasajeros;
    }

    public void setListaPasajeros(List<Integer> listaPasajeros) {
        this.listaPasajeros = listaPasajeros;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    @Override
    public String toString() {
        return "Vagon{" +
                "idVagon=" + idVagon +
                ", tipo='" + tipo + '\'' +
                ", listaAsientos=" + listaAsientos +
                ", listaPasajeros=" + listaPasajeros +
                ", listaMaletas=" + listaMaletas +
                ", disponibilidad=" + disponibilidad +
                '}';
    }
}