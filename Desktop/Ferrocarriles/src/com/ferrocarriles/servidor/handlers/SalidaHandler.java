package com.ferrocarriles.servidor.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import com.ferrocarriles.gestor.GestorSalidas;

public class SalidaHandler {
    private GestorSalidas gestor;

    public SalidaHandler(GestorSalidas gestor) {
        this.gestor = gestor;
    }

    public void handleListar(HttpExchange exchange) throws IOException {
    }

    public void handleCrear(HttpExchange exchange) throws IOException {
    }

    public void handleEditar(HttpExchange exchange) throws IOException {
    }

    public void handleEliminar(HttpExchange exchange) throws IOException {
    }
}
