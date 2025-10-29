package com.ferrocarriles.models;

/**
 * Clase Ruta segn diagrama de clases
 */
import java.util.ArrayList;
import java.util.List;

public class Ruta {
    // Constructor extendido para compatibilidad con RutaHandler
    public Ruta(int idRuta, String nombre, double distancia, int estacionOrigenId, int estacionDestinoId,
            boolean estado) {
        this.idRuta = idRuta;
        this.nombre = nombre;
        this.distancia = distancia;
        this.estacionOrigenId = estacionOrigenId;
        this.estacionDestinoId = estacionDestinoId;
        this.estado = estado;
        this.estacionesIntermedias = new ArrayList<>();
    }

    private int idRuta;
    private String nombre;
    private double distancia;
    private int estacionOrigenId;
    private int estacionDestinoId;
    private boolean estado;
    private List<Integer> estacionesIntermedias;

    public Ruta() {
        this.estacionesIntermedias = new ArrayList<>();
    }

    public Ruta(int idRuta, String nombre, double distancia, int estacionOrigenId, int estacionDestinoId,
            boolean estado, List<Integer> estacionesIntermedias) {
        this.idRuta = idRuta;
        this.nombre = nombre;
        this.distancia = distancia;
        this.estacionOrigenId = estacionOrigenId;
        this.estacionDestinoId = estacionDestinoId;
        this.estado = estado;
        this.estacionesIntermedias = estacionesIntermedias != null ? estacionesIntermedias : new ArrayList<>();
    }

    public List<Integer> getEstacionesIntermedias() {
        return estacionesIntermedias;
    }

    public void setEstacionesIntermedias(List<Integer> estacionesIntermedias) {
        this.estacionesIntermedias = estacionesIntermedias;
    }

    // Mtodo de negocio segn diagrama
    public void cambiarEstado(boolean nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("Estado de ruta " + nombre + " cambiado a: " + (nuevoEstado ? "ACTIVA" : "INACTIVA"));
    }

    // Getters y Setters
    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public int getEstacionOrigenId() {
        return estacionOrigenId;
    }

    public void setEstacionOrigenId(int estacionOrigenId) {
        this.estacionOrigenId = estacionOrigenId;
    }

    public int getEstacionDestinoId() {
        return estacionDestinoId;
    }

    public void setEstacionDestinoId(int estacionDestinoId) {
        this.estacionDestinoId = estacionDestinoId;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "idRuta=" + idRuta +
                ", nombre='" + nombre + '\'' +
                ", distancia=" + distancia +
                ", estacionOrigenId=" + estacionOrigenId +
                ", estacionDestinoId=" + estacionDestinoId +
                ", estado=" + estado +
                '}';
    }
}