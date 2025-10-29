package com.ferrocarriles.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Tren segn diagrama de clases
 */
public class Tren {
    private String tipo; // "Transiberiano" o "Carabela"
    private int capacidadCarga; // Solo relevante para trenes de carga
    private double kilometraje;

    public Tren(int idTren, String nombre, String tipo, int capacidadCarga, double kilometraje, boolean estado,
            int idEstacionActual) {
        this.idTren = idTren;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadCarga = capacidadCarga;
        this.kilometraje = kilometraje;
        this.estado = estado;
        this.listaVagones = new ArrayList<>();
        this.idEstacionActual = idEstacionActual;
    }

    // Constructor antiguo para compatibilidad
    public Tren(int idTren, String nombre, boolean estado) {
        this(idTren, nombre, "Transiberiano", 0, 0.0, estado, -1);
    }

    private int idTren;
    private String nombre;
    // capacidad ya no se usa como número fijo, se calcula dinámicamente
    private List<Integer> listaVagones;
    private boolean estado;
    private int idEstacionActual;

    public int getIdEstacionActual() {
        return idEstacionActual;
    }

    public void setIdEstacionActual(int idEstacionActual) {
        this.idEstacionActual = idEstacionActual;
    }

    public Tren() {
        this.listaVagones = new ArrayList<>();
    }

    // Constructor para Jackson, ignora capacidad
    public Tren(int idTren, String nombre, int capacidad, boolean estado) {
        this(idTren, nombre, "Transiberiano", 0, 0.0, estado, -1);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidadCarga() {
        return capacidadCarga;
    }

    public void setCapacidadCarga(int capacidadCarga) {
        this.capacidadCarga = capacidadCarga;
    }

    public double getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(double kilometraje) {
        this.kilometraje = kilometraje;
    }

    // Mtodos de negocio segn diagrama
    public void agregarVagon(int idVagon) {
        listaVagones.add(idVagon);
        System.out.println("Vagón " + idVagon + " agregado al tren " + nombre);
    }

    public void eliminarVagon(int idVagon) {
        if (listaVagones.remove(Integer.valueOf(idVagon))) {
            System.out.println("Vagn " + idVagon + " eliminado del tren " + nombre);
        } else {
            System.out.println("ERROR: Vagn " + idVagon + " no encontrado en el tren " + nombre);
        }
    }

    public boolean validarEstado() {
        if (estado && !listaVagones.isEmpty()) {
            System.out.println("Tren " + nombre + " en estado vlido y operativo");
            return true;
        } else {
            System.out
                    .println("Tren " + nombre + " NO operativo: estado=" + estado + ", vagones=" + listaVagones.size());
            return false;
        }
    }

    // Getters y Setters
    public int getIdTren() {
        return idTren;
    }

    public void setIdTren(int idTren) {
        this.idTren = idTren;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // La capacidad total se calcula como la suma de asientos de todos los vagones
    // de pasajeros
    public int getCapacidadTotal(List<VagonPasajeros> vagonesPasajeros) {
        int suma = 0;
        for (Integer idVagon : listaVagones) {
            for (VagonPasajeros v : vagonesPasajeros) {
                if (v.getIdVagon() == idVagon && v.getAsientos() != null) {
                    suma += v.getAsientos().size();
                }
            }
        }
        return suma;
    }

    // Métodos específicos para agregar vagones de pasajeros y equipaje
    public void agregarVagonPasajeros(int idVagonPasajeros) {
        listaVagones.add(idVagonPasajeros);
        System.out.println("Vagón de pasajeros " + idVagonPasajeros + " agregado al tren " + nombre);
    }

    public void agregarVagonEquipaje(int idVagonEquipaje) {
        listaVagones.add(idVagonEquipaje);
        System.out.println("Vagón de equipaje " + idVagonEquipaje + " agregado al tren " + nombre);
    }

    public List<Integer> getListaVagones() {
        return listaVagones;
    }

    public void setListaVagones(List<Integer> listaVagones) {
        this.listaVagones = listaVagones;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Tren{" +
                "idTren=" + idTren +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", capacidadCarga=" + capacidadCarga +
                ", kilometraje=" + kilometraje +
                ", listaVagones=" + listaVagones +
                ", estado=" + estado +
                ", idEstacionActual=" + idEstacionActual +
                '}';
    }
}