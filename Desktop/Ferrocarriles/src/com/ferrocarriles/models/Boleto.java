package com.ferrocarriles.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase Boleto segn diagrama de clases
 */
import com.ferrocarriles.models.PersonaContacto;

public class Boleto {
    /**
     * Calcula el precio del boleto considerando:
     * - disponibilidad (asiento libre)
     * - peso del equipaje
     * - distancia (simulada por idRuta)
     * Puedes ajustar la lógica según reglas reales.
     */
    public double calcularPrecio() {
        double base = 200.0; // base por asiento disponible
        double precioEquipaje = 0.0;
        for (Equipaje eq : equipaje) {
            // $10 por cada kg
            precioEquipaje += eq.getPeso() * 10.0;
        }
        double distancia = 100.0 + (rutaId * 50.0); // Simulación: idRuta multiplica distancia
        double precioDistancia = distancia * 0.5; // $0.5 por km
        double total = base + precioEquipaje + precioDistancia;
        return total;
    }

    private int idBoleto;
    private Date fechaCompra;
    private Date fechaSalida;
    private Date fechaLlegada;
    private int asiento; // idAsiento
    private int pasajeroId;
    private String categoriaPasajero;
    private int trenId;
    private int rutaId;
    private int estacionOrigenId;
    private int estacionDestinoId;
    private List<Equipaje> equipaje;
    private double precio;
    private boolean estado; // true = vlido/activo, false = cancelado
    private List<Integer> caminoEstaciones; // Secuencia de estaciones (resultado de Dijkstra)
    private PersonaContacto personaContacto;

    public Boleto() {
        this.equipaje = new ArrayList<>();
        this.caminoEstaciones = new ArrayList<>();
        this.estacionOrigenId = -1;
        this.estacionDestinoId = -1;
        this.categoriaPasajero = "";
    }

    public Boleto(int idBoleto, Date fechaCompra, Date fechaSalida, Date fechaLlegada, int asiento,
            int pasajeroId, String categoriaPasajero, int trenId, int rutaId, int estacionOrigenId,
            int estacionDestinoId, double precio,
            boolean estado, List<Equipaje> equipaje,
            List<Integer> caminoEstaciones, PersonaContacto personaContacto) {
        this.idBoleto = idBoleto;
        this.fechaCompra = fechaCompra;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.asiento = asiento;
        this.pasajeroId = pasajeroId;
        this.categoriaPasajero = categoriaPasajero;
        this.trenId = trenId;
        this.rutaId = rutaId;
        this.estacionOrigenId = estacionOrigenId;
        this.estacionDestinoId = estacionDestinoId;
        this.precio = precio;
        this.estado = estado;
        this.equipaje = equipaje != null ? equipaje : new ArrayList<>();
        this.caminoEstaciones = caminoEstaciones != null ? caminoEstaciones : new ArrayList<>();
        this.personaContacto = personaContacto;
    }

    public Date getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(Date fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public String getCategoriaPasajero() {
        return categoriaPasajero;
    }

    public void setCategoriaPasajero(String categoriaPasajero) {
        this.categoriaPasajero = categoriaPasajero;
    }

    public PersonaContacto getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(PersonaContacto personaContacto) {
        this.personaContacto = personaContacto;
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

    public List<Integer> getCaminoEstaciones() {
        return caminoEstaciones;
    }

    public void setCaminoEstaciones(List<Integer> caminoEstaciones) {
        this.caminoEstaciones = caminoEstaciones;
    }

    // Mtodos de negocio segn diagrama
    public boolean validar() {
        if (!estado) {
            System.out.println("ERROR: Boleto " + idBoleto + " est cancelado");
            return false;
        }

        Date ahora = new Date();
        if (fechaSalida.before(ahora)) {
            System.out.println("ERROR: Boleto " + idBoleto + " expirado (fecha salida: " + fechaSalida + ")");
            return false;
        }

        System.out.println("Boleto " + idBoleto + " vlido para fecha " + fechaSalida);
        return true;
    }

    public void asignarAsiento(int numeroAsiento) {
        this.asiento = numeroAsiento;
        System.out.println("Asiento " + numeroAsiento + " asignado al boleto " + idBoleto);
    }

    public boolean abordar() {
        if (validar()) {
            System.out.println("Pasajero con boleto " + idBoleto + " puede abordar en asiento " + asiento);
            return true;
        } else {
            System.out.println("ERROR: Pasajero con boleto " + idBoleto + " NO puede abordar");
            return false;
        }
    }

    public void cancelar() {
        if (estado) {
            this.estado = false;
            System.out.println("Boleto " + idBoleto + " cancelado. Precio: $" + precio);
        } else {
            System.out.println("Boleto " + idBoleto + " ya estaba cancelado");
        }
    }

    // Getters y Setters
    public int getIdBoleto() {
        return idBoleto;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public int getAsiento() {
        return asiento;
    }

    public void setAsiento(int asiento) {
        this.asiento = asiento;
    }

    public int getPasajeroId() {
        return pasajeroId;
    }

    public void setPasajeroId(int pasajeroId) {
        this.pasajeroId = pasajeroId;
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

    public List<Equipaje> getEquipaje() {
        return equipaje;
    }

    public void setEquipaje(List<Equipaje> equipaje) {
        this.equipaje = equipaje;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Boleto{" +
                "idBoleto=" + idBoleto +
                ", fechaCompra=" + fechaCompra +
                ", fechaSalida=" + fechaSalida +
                ", fechaLlegada=" + fechaLlegada +
                ", asiento=" + asiento +
                ", pasajeroId=" + pasajeroId +
                ", categoriaPasajero=" + categoriaPasajero +
                ", trenId=" + trenId +
                ", rutaId=" + rutaId +
                ", estacionOrigenId=" + estacionOrigenId +
                ", estacionDestinoId=" + estacionDestinoId +
                ", equipaje=" + equipaje +
                ", personaContacto=" + personaContacto +
                ", precio=" + precio +
                ", estado=" + estado +
                '}';
    }
}