package com.ferrocarriles.servidor.handlers;

import com.ferrocarriles.servidor.ServidorHTTP;
import com.sun.net.httpserver.HttpExchange;
import com.ferrocarriles.gestor.GestorRutas;
import com.ferrocarriles.models.Ruta;
import java.io.IOException;
import java.util.Map;

public class RutaHandler {
    private GestorRutas gestorRutas;

    public RutaHandler(GestorRutas gestorRutas) {
        this.gestorRutas = gestorRutas;
    }

    public void handleCrear(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = ServidorHTTP
                    .parsearFormulario(ServidorHTTP.leerInputStream(exchange.getRequestBody()));
            try {
                int id = Integer.parseInt(params.getOrDefault("idRuta", "0"));
                String nombre = params.getOrDefault("nombre", "");
                int origenId = Integer.parseInt(params.getOrDefault("origen", "0"));
                int destinoId = Integer.parseInt(params.getOrDefault("destino", "0"));
                double distancia = Double.parseDouble(params.getOrDefault("distancia", "0"));
                boolean activa = Boolean.parseBoolean(params.getOrDefault("activa", "true"));
                Ruta nueva = new Ruta(id, nombre, distancia, origenId, destinoId, activa);
                gestorRutas.agregar(nueva);
                ServidorHTTP.guardarRutasEnJSON();
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
                int id = Integer.parseInt(params.getOrDefault("idRuta", "0"));
                gestorRutas.eliminar(id);
                ServidorHTTP.guardarRutasEnJSON();
            } catch (Exception e) {
            }
        }
        exchange.getResponseHeaders().set("Location", "/dashboard/admin");
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }
}
