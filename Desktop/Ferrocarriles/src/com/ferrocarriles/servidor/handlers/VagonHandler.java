package com.ferrocarriles.servidor.handlers;

import com.ferrocarriles.servidor.ServidorHTTP;
import com.sun.net.httpserver.HttpExchange;
import com.ferrocarriles.gestor.GestorVagones;
import com.ferrocarriles.models.VagonPasajeros;
import com.ferrocarriles.models.VagonEquipaje;
import java.io.IOException;
import java.util.Map;

public class VagonHandler {
    private GestorVagones gestorVagones;

    public VagonHandler(GestorVagones gestorVagones) {
        this.gestorVagones = gestorVagones;
    }

    public void handleCrear(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = ServidorHTTP
                    .parsearFormulario(ServidorHTTP.leerInputStream(exchange.getRequestBody()));
            try {
                int id = Integer.parseInt(params.getOrDefault("idVagon", "0"));
                String tipo = params.getOrDefault("tipo", "");
                boolean disponible = Boolean.parseBoolean(params.getOrDefault("disponible", "true"));
                if ("Pasajeros".equalsIgnoreCase(tipo)) {
                    VagonPasajeros nuevo = new VagonPasajeros(id);
                    gestorVagones.agregarVagonPasajeros(nuevo);
                } else if ("Equipaje".equalsIgnoreCase(tipo)) {
                    VagonEquipaje nuevo = new VagonEquipaje(id);
                    gestorVagones.agregarVagonEquipaje(nuevo);
                }
                ServidorHTTP.guardarVagonesEnJSON();
            } catch (Exception e) {
            }
        }
        exchange.getResponseHeaders().set("Location", "/dashboard/admin");
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }

    public void handleEliminar(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = ServidorHTTP
                    .parsearFormulario(ServidorHTTP.leerInputStream(exchange.getRequestBody()));
            try {
                int id = Integer.parseInt(params.getOrDefault("idVagon", "0"));
                // Intentar eliminar ambos tipos
                gestorVagones.eliminarVagonPasajeros(id);
                gestorVagones.eliminarVagonEquipaje(id);
                ServidorHTTP.guardarVagonesEnJSON();
            } catch (Exception e) {
            }
        }
        exchange.getResponseHeaders().set("Location", "/dashboard/admin");
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }
}
