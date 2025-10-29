package com.ferrocarriles.models;


public class Pasajero extends Usuario {
    private PersonaContacto contactoEmergencia;

    public Pasajero() {
        super();
        this.rol = "PASAJERO";
    }

    public Pasajero(int idUsuario, String nombre, String email, String passwordHash,
            String userName, boolean activo, PersonaContacto contactoEmergencia) {
        super(idUsuario, nombre, email, passwordHash, "PASAJERO", userName, activo);
        this.contactoEmergencia = contactoEmergencia;
    }

    @Override
    public String[] getPermisos() {
        return new String[] { "COMPRAR_BOLETOS", "VER_BOLETOS", "AGREGAR_EQUIPAJE" };
    }

    // Mtodos de negocio segn diagrama
    public void comprarBoletos(Boleto boleto) {
        System.out.println("Comprando boleto para pasajero: " + this.nombre);
    }

    public void verBoletos() {
        System.out.println("Viendo boletos del pasajero: " + this.nombre);
    }

    public void atenderCliente() {
        System.out.println("Atendiendo cliente: " + this.nombre);
    }

    public void agregarEquipaje(Equipaje equipaje) {
        System.out.println("Agregando equipaje para pasajero: " + this.nombre);
    }

    public PersonaContacto getContactoEmergencia() {
        return contactoEmergencia;
    }

    public void setContactoEmergencia(PersonaContacto contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }

    @Override
    public String toString() {
        return "Pasajero{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", contactoEmergencia=" + contactoEmergencia +
                '}';
    }
}