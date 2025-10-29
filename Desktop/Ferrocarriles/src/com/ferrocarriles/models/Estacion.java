package com.ferrocarriles.models;

import java.util.Objects;

/**
 * Clase Estacion segn diagrama de clases
 * Representa una estacin ferroviaria
 * Implementa Comparable para poder ser usada en ArbolAVL
 */
public class Estacion implements Comparable<Estacion> {
    // Compatibilidad con dashboard: ciudad = municipio
    public String getCiudad() {
        return getMunicipio();
    }

    private int idEstacion;
    private String Nombre;
    private String Departamento;
    private String Municipio;
    private int capacidadTrenes;

    public Estacion() {
    }

    public Estacion(int idEstacion, String nombre, String departamento, String municipio, int capacidadTrenes) {
        this.idEstacion = idEstacion;
        this.Nombre = nombre;
        this.Departamento = departamento;
        this.Municipio = municipio;
        this.capacidadTrenes = capacidadTrenes;
    }

    /**
     * Constructor de copia (para historial)
     */
    public Estacion(Estacion otra) {
        this.idEstacion = otra.idEstacion;
        this.Nombre = otra.Nombre;
        this.Departamento = otra.Departamento;
        this.Municipio = otra.Municipio;
        this.capacidadTrenes = otra.capacidadTrenes;
    }

    // Getters y Setters
    public int getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(int idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDepartamento() {
        return Departamento;
    }

    public void setDepartamento(String departamento) {
        this.Departamento = departamento;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        this.Municipio = municipio;
    }

    public int getCapacidadTrenes() {
        return capacidadTrenes;
    }

    public void setCapacidadTrenes(int capacidadTrenes) {
        this.capacidadTrenes = capacidadTrenes;
    }

    @Override
    public String toString() {
        return "Estacion{" +
                "idEstacion=" + idEstacion +
                ", Nombre='" + Nombre + '\'' +
                ", Departamento='" + Departamento + '\'' +
                ", Municipio='" + Municipio + '\'' +
                ", capacidadTrenes=" + capacidadTrenes +
                '}';
    }

    /**
     * Comparar por ID para ordenamiento en estructuras
     */
    @Override
    public int compareTo(Estacion otra) {
        return Integer.compare(this.idEstacion, otra.idEstacion);
    }

    /**
     * equals y hashCode para Tabla Hash
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Estacion estacion = (Estacion) o;
        return idEstacion == estacion.idEstacion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEstacion);
    }
}