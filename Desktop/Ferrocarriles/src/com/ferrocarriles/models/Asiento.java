package com.ferrocarriles.models;

/**
 * Clase Asiento segn diagrama de clases
 */
public class Asiento {
    // Método para compatibilidad con GestorVagones
    public void setIdVagon(int idVagon) {
        // No se almacena, solo para compatibilidad
    }

    // Método para compatibilidad con Vagon.java
    public boolean isDisponible() {
        return disponibilidad;
    }

    // Constructor extendido para compatibilidad con ServidorHTTP
    public Asiento(int idAsiento, String numeroAsiento, String clase, String nombre, boolean disponibilidad,
            int idVagon) {
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.clase = clase;
        this.nombre = nombre;
        this.disponibilidad = disponibilidad;
        // idVagon no existe en la clase, pero se ignora para compatibilidad
    }

    // Constructor que solo recibe el nombre
    public Asiento(String nombre) {
        this.nombre = nombre;
        this.disponibilidad = true;
        this.idAsiento = -1;
        this.numeroAsiento = null;
        this.clase = null;
    }

    private int idAsiento;
    private String numeroAsiento;
    private String clase;
    private String nombre;
    private boolean disponibilidad;

    public Asiento() {
    }

    public Asiento(int idAsiento, String numeroAsiento, String clase, String nombre, boolean disponibilidad) {
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.clase = clase;
        this.nombre = nombre;
        this.disponibilidad = disponibilidad;
    }

    // Mtodos de negocio segn diagrama
    public void reservar(String nombrePasajero) {
        if (disponibilidad) {
            this.disponibilidad = false;
            this.nombre = nombrePasajero;
            System.out.println("Asiento " + numeroAsiento + " reservado para " + nombrePasajero);
        } else {
            System.out.println("ERROR: Asiento " + numeroAsiento + " ya est ocupado por " + nombre);
        }
    }

    public void liberar() {
        if (!disponibilidad) {
            this.disponibilidad = true;
            System.out.println("Asiento " + numeroAsiento + " liberado (anterior ocupante: " + nombre + ")");
            this.nombre = null;
        } else {
            System.out.println("Asiento " + numeroAsiento + " ya estaba disponible");
        }
    }

    // Getters y Setters
    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(String numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "idAsiento=" + idAsiento +
                ", numeroAsiento='" + numeroAsiento + '\'' +
                ", clase='" + clase + '\'' +
                ", nombre='" + nombre + '\'' +
                ", disponibilidad=" + disponibilidad +
                '}';
    }
}