package com.ferrocarriles.servidor.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import com.ferrocarriles.gestor.GestorUsuarios;
import com.ferrocarriles.models.Usuario;

public class UsuarioHandler {
    // Verifica la sesión y retorna el usuario autenticado, o null si no existe
    private Usuario verificarSesion(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                String[] kv = cookie.trim().split("=");
                if (kv.length == 2 && kv[0].equals("sessionId")) {
                    return com.ferrocarriles.servidor.SessionManager.obtenerUsuario(kv[1]);
                }
            }
        }
        return null;
    }

    private GestorUsuarios gestor;

    public UsuarioHandler(GestorUsuarios gestor) {
        this.gestor = gestor;
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }

        // Leer datos del formulario
        String body = new String(exchange.getRequestBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        String[] params = body.split("&");
        String userName = "";
        String password = "";
        for (String param : params) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                if (kv[0].equals("userName"))
                    userName = java.net.URLDecoder.decode(kv[1], "UTF-8");
                if (kv[0].equals("password"))
                    password = java.net.URLDecoder.decode(kv[1], "UTF-8");
            }
        }

        Usuario usuario = gestor.login(userName, password);
        if (usuario == null) {
            // Login fallido, mostrar error
            String html = "<div class='alert alert-danger fade show' style='margin-bottom:1rem;'>Usuario o contraseña incorrectos</div>";
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, html.length());
            try (java.io.OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            return;
        }

        // Crear sesión y enviar cookie
        String sessionId = com.ferrocarriles.servidor.SessionManager.crearSesion(usuario);
        String rol = usuario.getRol();
        String destino = "/dashboard";
        if ("OPERADOR".equalsIgnoreCase(rol)) {
            destino = "/dashboard-operador";
        } else if ("ADMIN".equalsIgnoreCase(rol)) {
            destino = "/dashboard?rol=admin";
        } else if ("PASAJERO".equalsIgnoreCase(rol)) {
            destino = "/dashboard?rol=pasajero";
        }
        exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId + "; Path=/; HttpOnly");
        exchange.getResponseHeaders().add("Location", destino);
        exchange.sendResponseHeaders(302, -1);
    }

    public void handleDashboardAdmin(HttpExchange exchange) throws IOException {
        Usuario usuario = verificarSesion(exchange);
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
        // Aquí iría la lógica del dashboard admin
    }

    public void handleDashboardOperador(HttpExchange exchange) throws IOException {
        Usuario usuario = verificarSesion(exchange);
        if (usuario == null || !"OPERADOR".equalsIgnoreCase(usuario.getRol())) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
        // Aquí iría la lógica del dashboard operador
    }

    public void handleDashboardPasajero(HttpExchange exchange) throws IOException {
        Usuario usuario = verificarSesion(exchange);
        if (usuario == null || !"PASAJERO".equalsIgnoreCase(usuario.getRol())) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
        // Aquí iría la lógica del dashboard pasajero
    }

    public void handleListar(HttpExchange exchange) throws IOException {
    }

    public void handleCrear(HttpExchange exchange) throws IOException {
        if (verificarSesion(exchange) == null) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
    }

    public void handleEditar(HttpExchange exchange) throws IOException {
        if (verificarSesion(exchange) == null) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
    }

    public void handleEliminar(HttpExchange exchange) throws IOException {
        if (verificarSesion(exchange) == null) {
            exchange.getResponseHeaders().add("Location", "/login.html");
            exchange.sendResponseHeaders(302, -1);
            return;
        }
    }
}
