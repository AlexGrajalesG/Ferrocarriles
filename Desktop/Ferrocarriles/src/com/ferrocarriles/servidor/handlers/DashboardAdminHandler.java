package com.ferrocarriles.servidor.handlers;

import com.ferrocarriles.gestor.GestorTrenes;
import com.ferrocarriles.models.Tren;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DashboardAdminHandler {
        private com.ferrocarriles.gestor.GestorEstaciones gestorEstaciones;
        private com.ferrocarriles.gestor.GestorTrenes gestorTrenes;
        private com.ferrocarriles.gestor.GestorVagones gestorVagones;
        private com.ferrocarriles.gestor.GestorRutas gestorRutas;

        public DashboardAdminHandler(com.ferrocarriles.gestor.GestorEstaciones gestorEstaciones,
                        com.ferrocarriles.gestor.GestorTrenes gestorTrenes,
                        com.ferrocarriles.gestor.GestorVagones gestorVagones,
                        com.ferrocarriles.gestor.GestorRutas gestorRutas) {
                this.gestorEstaciones = gestorEstaciones;
                this.gestorTrenes = gestorTrenes;
                this.gestorVagones = gestorVagones;
                this.gestorRutas = gestorRutas;
        }

        public void handle(HttpExchange exchange) throws IOException {
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><title>Dashboard Admin</title>");
                html.append("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet'>");
                html.append("</head><body><div class='container mt-4'>");
                html.append("<h2>Gestión de Trenes</h2>");
                // Formulario para agregar tren
                html.append("<form method='POST' action='/agregarTren' class='mb-4'>");
                html.append("<div class='row'><div class='col'><input type='text' name='nombre' class='form-control' placeholder='Nombre del tren' required></div>");
                html.append("<div class='col'><select name='tipo' class='form-control'><option value='Transiberiano'>Transiberiano</option><option value='Carabela'>Carabela</option></select></div>");
                html.append("<div class='col'><input type='number' name='capacidadCarga' class='form-control' placeholder='Capacidad de carga'></div>");
                html.append("<div class='col'><input type='number' name='kilometraje' class='form-control' placeholder='Kilometraje'></div>");
                html.append("<div class='col'><input type='number' name='cantidadVagonesPasajeros' class='form-control' placeholder='Vagones pasajeros' required></div>");
                html.append("<div class='col'><button type='submit' class='btn btn-success'>Agregar Tren</button></div></div>");
                html.append("</form>");
                // Listado de trenes
                html.append("<h4>Trenes registrados</h4>");
                List<Tren> trenes = com.ferrocarriles.servidor.ServidorHTTP.toList(gestorTrenes.obtenerTodos());
                html.append("<table class='table table-bordered'><thead><tr><th>ID</th><th>Nombre</th><th>Tipo</th><th>Estado</th><th>Acciones</th></tr></thead><tbody>");
                for (Tren tren : trenes) {
                        html.append("<tr>");
                        html.append("<td>" + tren.getIdTren() + "</td>");
                        html.append("<td>" + tren.getNombre() + "</td>");
                        html.append("<td>" + tren.getTipo() + "</td>");
                        html.append("<td>" + (tren.isEstado() ? "Activo" : "Inactivo") + "</td>");
                        html.append("<td>");
                        // Formulario para dar de baja tren
                        html.append("<form method='POST' action='/bajaTren' style='display:inline; margin-right:5px;'><input type='hidden' name='idTren' value='"
                                        + tren.getIdTren()
                                        + "'><button type='submit' class='btn btn-danger btn-sm'>Dar de baja</button></form>");
                        // Formulario para agregar vagón
                        html.append("<form method='POST' action='/agregarVagon' style='display:inline;'>"
                                        + "<input type='hidden' name='idTren' value='" + tren.getIdTren() + "'>"
                                        + "<select name='tipo' class='form-select form-select-sm d-inline-block' style='width:auto;'>"
                                        + "<option value='Pasajeros'>Pasajeros</option>"
                                        + "<option value='Equipaje'>Equipaje</option>"
                                        + "</select> "
                                        + "<input type='number' name='capacidad' class='form-control form-control-sm d-inline-block' style='width:80px;' placeholder='Capacidad' min='1' required> "
                                        + "<input type='text' name='clase' class='form-control form-control-sm d-inline-block' style='width:80px;' placeholder='Clase'> "
                                        + "<button type='submit' class='btn btn-primary btn-sm'>Agregar Vagón</button>"
                                        + "</form>");
                        html.append("</td>");
                        html.append("</tr>");
                }
                html.append("</tbody></table>");
                html.append("</div></body></html>");
                byte[] resp = html.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, resp.length);
                OutputStream os = exchange.getResponseBody();
                os.write(resp);
                os.close();
        }
}
