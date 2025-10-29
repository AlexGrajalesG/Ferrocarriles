package com.ferrocarriles.models;

import java.util.Date;

/**
 * Clase Salida segn diagrama de clases
 * Representa un horario de salida de un tren en una ruta especfica
 */
import java.util.List;

public class Salida {
    private int idSalida;
    private Date fechaHoraSalida;
    private int trenId;
    private int rutaId;
    private boolean disponible;
    private List<Integer> recorridoEstaciones;

    public Salida() {
        this.recorridoEstaciones = null;
    }

    public Salida(int idSalida, Date fechaHoraSalida, int trenId, int rutaId, boolean disponible,
            List<Integer> recorridoEstaciones) {
        this.idSalida = idSalida;
        this.fechaHoraSalida = fechaHoraSalida;
        this.trenId = trenId;
        this.rutaId = rutaId;
        this.disponible = disponible;
        this.recorridoEstaciones = recorridoEstaciones;
    }

    public List<Integer> getRecorridoEstaciones() {
        return recorridoEstaciones;
    }

    public void setRecorridoEstaciones(List<Integer> recorridoEstaciones) {
        this.recorridoEstaciones = recorridoEstaciones;
    }

    // Getters y Setters
    public int getIdSalida() {
        return idSalida;
    }

    public void setIdSalida(int idSalida) {
        this.idSalida = idSalida;
    }

    public Date getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(Date fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public int getTrenId() {
        return trenId;
    }

    public void setTrenId(int trenId) {
        this.trenId = trenId;
    }

    public int getRutaId() {
        return rutaId;
    }

    public void setRutaId(int rutaId) {
        this.rutaId = rutaId;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Salida{" +
                "idSalida=" + idSalida +
                ", fechaHoraSalida=" + fechaHoraSalida +
                ", trenId=" + trenId +
                ", rutaId=" + rutaId +
                ", disponible=" + disponible +
                '}';
    }
}