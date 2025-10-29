package com.ferrocarriles.models;

/**
 * Clase Operador - Hereda de Usuario
 * Puede vender boletos y gestionar equipaje
 */
public class Operador extends Usuario {

    public Operador() {
        super();
        this.rol = "OPERADOR";
    }

    public Operador(int idUsuario, String nombre, String email, String passwordHash,
            String userName, boolean activo) {
        super(idUsuario, nombre, email, passwordHash, "OPERADOR", userName, activo);
    }

    @Override
    public String[] getPermisos() {
        return new String[] { "VENDER_BOLETOS", "CONFIRMAR_BOLETOS", "GESTIONAR_EQUIPAJE" };
    }

    // Mtodos de negocio segn diagrama
    public Boleto venderBoleto(Boleto boleto) {
        System.out.println("Vendiendo boleto ID: " + boleto.getIdBoleto());
        return boleto;
    }

    public boolean confirmarBoleto(int idBoleto) {
        System.out.println("Confirmando boleto ID: " + idBoleto);
        return true;
    }

    public Boleto buscarBoleto(int idBoleto) {
        System.out.println("Buscando boleto ID: " + idBoleto);
        return null;
    }

    public void consultarHorarios() {
        System.out.println("Consultando horarios");
    }

    public void gestionEquipaje(Equipaje equipaje) {
        System.out.println("Gestionando equipaje ID: " + equipaje.getIdEquipaje());
    }

    @Override
    public String toString() {
        return "Operador{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
