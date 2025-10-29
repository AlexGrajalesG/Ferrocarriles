package com.ferrocarriles.servidor.handlers;

import com.ferrocarriles.servidor.ServidorHTTP;
import com.sun.net.httpserver.HttpExchange;
import com.ferrocarriles.gestor.GestorTrenes;
import com.ferrocarriles.models.Tren;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class TrenHandler {
    public void handleEditar(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = ServidorHTTP
                    .parsearFormulario(ServidorHTTP.leerInputStream(exchange.getRequestBody()));
            try {
                int id = Integer.parseInt(params.getOrDefault("idTren", "0"));
                String nombre = params.getOrDefault("nombre", "");
                boolean estado = Boolean.parseBoolean(params.getOrDefault("estado", "true"));
                Tren trenExistente = gestorTrenes.buscarPorId(id);
                if (trenExistente != null) {
                    trenExistente.setNombre(nombre);
                    trenExistente.setEstado(estado);
                    gestorTrenes.actualizar(id, trenExistente);
                    ServidorHTTP.guardarTrenesEnJSON();
                }
            } catch (Exception e) {
            }
        }
        exchange.getResponseHeaders().set("Location", "/dashboard?rol=admin");
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }

    private GestorTrenes gestorTrenes;

    public TrenHandler(GestorTrenes gestorTrenes) {
        this.gestorTrenes = gestorTrenes;
    }

    public void handleCrear(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = ServidorHTTP
                    .parsearFormulario(ServidorHTTP.leerInputStream(exchange.getRequestBody()));
            try {
                List<Tren> trenes = ServidorHTTP.toList(gestorTrenes.obtenerTodos());
                int nuevoId = trenes.stream().mapToInt(Tren::getIdTren).max().orElse(0) + 1;
                String nombre = params.getOrDefault("nombre", "");
                boolean estado = Boolean.parseBoolean(params.getOrDefault("estado", "true"));
                Tren nuevo = new Tren(nuevoId, nombre, estado);
                gestorTrenes.agregar(nuevo);
                ServidorHTTP.guardarTrenesEnJSON();
            } catch (Exception e) {
            }
        }
        exchange.getResponseHeaders().set("Location", "/dashboard?rol=admin");
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }

    public void handleEliminar(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = ServidorHTTP
                    .parsearFormulario(ServidorHTTP.leerInputStream(exchange.getRequestBody()));
            try {
                int id = Integer.parseInt(params.getOrDefault("idTren", "0"));
                gestorTrenes.eliminar(id);
                ServidorHTTP.guardarTrenesEnJSON();
            } catch (Exception e) {
            }
        }
        exchange.getResponseHeaders().set("Location", "/dashboard?rol=admin");
        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }
}
