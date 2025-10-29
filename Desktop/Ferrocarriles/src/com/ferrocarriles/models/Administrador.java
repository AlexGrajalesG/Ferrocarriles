package com.ferrocarriles.models;

/**
 * Clase Administrador - Hereda de Usuario
 * Tiene permisos completos para gestionar el sistema
 */
public class Administrador extends Usuario {
    private String[] permisos;

    public Administrador() {
        super();
        this.rol = "ADMIN";
    }

    public Administrador(int idUsuario, String nombre, String email, String passwordHash,
            String userName, boolean activo, String[] permisos) {
        super(idUsuario, nombre, email, passwordHash, "ADMIN", userName, activo);
        this.permisos = permisos;
    }

    @Override
    public String[] getPermisos() {
        return permisos != null ? permisos : new String[] { "CREAR", "EDITAR", "ELIMINAR", "GESTIONAR" };
    }

    public void setPermisos(String[] permisos) {
        this.permisos = permisos;
    }

    // Mtodos de negocio segn diagrama
    public void registrarTren(Tren tren) {
        System.out.println("Registrando tren: " + tren.getNombre());
    }

    public void editarTren(Tren tren) {
        System.out.println("Editando tren: " + tren.getNombre());
    }

    public void bajaDeTren(int idTren) {
        System.out.println("Dando de baja tren ID: " + idTren);
    }

    public void crearRutas(Ruta ruta) {
        System.out.println("Creando ruta: " + ruta.getNombre());
    }

    public void editarRuta(Ruta ruta) {
        System.out.println("Editando ruta: " + ruta.getNombre());
    }

    public void publicarRuta(int idRuta) {
        System.out.println("Publicando ruta ID: " + idRuta);
    }

    public void gestionEmpleados() {
        System.out.println("Gestionando empleados");
    }

    public void administrarHorarios() {
        System.out.println("Administrando horarios");
    }

    public void crearEstacion(Estacion estacion) {
        System.out.println("Creando estacin: " + estacion.getNombre());
    }

    public void editarEstacion(Estacion estacion) {
        System.out.println("Editando estacin: " + estacion.getNombre());
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", permisos=" + java.util.Arrays.toString(permisos) +
                '}';
    }
}
