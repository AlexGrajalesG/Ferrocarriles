package com.ferrocarriles.servidor;

import com.ferrocarriles.models.Usuario;
import java.util.HashMap;
import java.util.UUID;

public class SessionManager {
    private static final HashMap<String, Usuario> sesiones = new HashMap<>();

    public static String crearSesion(Usuario usuario) {
        String sessionId = UUID.randomUUID().toString();
        sesiones.put(sessionId, usuario);
        return sessionId;
    }

    public static Usuario obtenerUsuario(String sessionId) {
        return sesiones.get(sessionId);
    }

    public static void eliminarSesion(String sessionId) {
        sesiones.remove(sessionId);
    }
}
