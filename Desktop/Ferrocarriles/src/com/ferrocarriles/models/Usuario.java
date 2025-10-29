package com.ferrocarriles.models;

/**
 * Representa un usuario del sistema con sus credenciales y permisos
 */
public abstract class Usuario {
    protected int idUsuario;
    protected String nombre;
    protected String email;
    protected String passwordHash;
    protected String rol;
    protected String userName;
    protected boolean activo;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String email, String passwordHash,
            String rol, String userName, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.userName = userName;
        this.activo = activo;
    }

    public abstract String[] getPermisos();

    /**
     * Mtodo para iniciar sesin (validar credenciales)
     */
    public boolean login(String userName, String password) {
        String hash = hashPassword(password.trim());
        // Comparar username ignorando espacios y mayúsculas/minúsculas
        boolean userMatch = this.userName.trim().equalsIgnoreCase(userName.trim());
        boolean passMatch = this.passwordHash.equals(hash);
        return userMatch && passMatch && this.activo;
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.trim().getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", userName='" + userName + '\'' +
                ", activo=" + activo +
                '}';
    }
}