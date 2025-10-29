package com.ferrocarriles.servidor;

import com.ferrocarriles.servidor.handlers.DashboardAdminHandler;
import com.ferrocarriles.servidor.handlers.TrenHandler;

import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;

import com.ferrocarriles.gestor.GestorEstaciones;
import com.ferrocarriles.gestor.GestorTrenes;
import com.ferrocarriles.gestor.GestorVagones;
import com.ferrocarriles.gestor.GestorRutas;
import com.ferrocarriles.gestor.GestorUsuarios;
import com.ferrocarriles.gestor.GestorBoletos;
import com.ferrocarriles.gestor.GestorEquipaje;

public class ServidorHTTP {
    // Utilidad para parsear datos de formulario POST
    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        String body = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
        Map<String, String> params = new HashMap<>();
        String[] pares = body.split("&");
        for (String par : pares) {
            String[] keyValue = par.split("=");
            if (keyValue.length == 2) {
                try {
                    params.put(java.net.URLDecoder.decode(keyValue[0], "UTF-8"),
                            java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                } catch (Exception e) {
                }
            }
        }
        return params;
    }

    // Redirección HTTP simple
    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        try {
            exchange.getResponseBody().close();
        } catch (Exception e) {
        }
        exchange.close();
    }

    private static final int PUERTO = 8080;

    private static GestorEstaciones gestorEstaciones;
    private static GestorTrenes gestorTrenes;
    private static GestorVagones gestorVagones;
    private static GestorRutas gestorRutas;
    private static GestorUsuarios gestorUsuarios;
    private static GestorBoletos gestorBoletos;
    private static GestorEquipaje gestorEquipaje;

    public static void main(String[] args) throws IOException {
        inicializarGestores();
        HttpServer servidor = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        configurarRutas(servidor);
        servidor.setExecutor(null);
        servidor.start();
        System.out.println("Servidor iniciado en http://localhost:" + PUERTO);
    }

    private static void inicializarGestores() throws IOException {
        gestorEstaciones = new GestorEstaciones();
        gestorTrenes = new GestorTrenes();
        gestorVagones = new GestorVagones();
        gestorRutas = new GestorRutas();
        gestorUsuarios = new GestorUsuarios();
        gestorBoletos = new GestorBoletos();
        gestorEquipaje = new GestorEquipaje();

        gestorEstaciones.cargarDesdeJSON();
        gestorTrenes.cargarDesdeJSON();
        gestorVagones.cargarPasajerosDesdeJSON();
        gestorVagones.cargarEquipajeDesdeJSON();
        gestorRutas.cargarDesdeJSON();
        gestorUsuarios.cargarDesdeJSON();
        gestorBoletos.cargarDesdeJSON();
        gestorEquipaje.cargarDesdeJSON();
    }

    private static void configurarRutas(HttpServer servidor) {
        servidor.createContext("/", ServidorHTTP::mostrarLogin);
        servidor.createContext("/login", ServidorHTTP::procesarLogin);
        servidor.createContext("/dashboard", ServidorHTTP::mostrarDashboard);
        servidor.createContext("/dashboard-operador", ServidorHTTP::mostrarDashboardOperador);
        servidor.createContext("/comprarBoleto", ServidorHTTP::comprarBoleto);
        servidor.createContext("/calcularEstimado", ServidorHTTP::calcularEstimado);
        // Endpoints CRUD para dashboard admin
        servidor.createContext("/agregarRuta", ServidorHTTP::agregarRuta);
        servidor.createContext("/editarRuta", ServidorHTTP::editarRuta);
        servidor.createContext("/borrarRuta", ServidorHTTP::borrarRuta);
        servidor.createContext("/agregarVagon", ServidorHTTP::agregarVagon);
        servidor.createContext("/editarVagon", ServidorHTTP::editarVagon);
        servidor.createContext("/borrarVagon", ServidorHTTP::borrarVagon);
        servidor.createContext("/agregarEstacion", ServidorHTTP::agregarEstacion);
        servidor.createContext("/editarEstacion", ServidorHTTP::editarEstacion);
        servidor.createContext("/borrarEstacion", ServidorHTTP::borrarEstacion);
        servidor.createContext("/agregarTren", ServidorHTTP::agregarTren);
        servidor.createContext("/bajaTren", ServidorHTTP::bajaTren);
    }

    // Endpoint para agregar tren
    private static void agregarTren(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String nombre = params.getOrDefault("nombre", "").trim();
        String tipo = params.getOrDefault("tipo", "Transiberiano").trim();
        int capacidadCarga = Integer.parseInt(params.getOrDefault("capacidadCarga", "0"));
        double kilometraje = Double.parseDouble(params.getOrDefault("kilometraje", "0"));
        int cantidadVagonesPasajeros = Integer.parseInt(params.getOrDefault("cantidadVagonesPasajeros", "1"));
        // Generar nuevo ID
        List<com.ferrocarriles.models.Tren> trenes = toList(gestorTrenes.obtenerTodos());
        int nuevoId = trenes.stream().mapToInt(com.ferrocarriles.models.Tren::getIdTren).max().orElse(0) + 1;
        gestorTrenes.crearTrenConVagones(nuevoId, nombre, tipo, capacidadCarga, kilometraje, cantidadVagonesPasajeros,
                gestorVagones);
        gestorTrenes.guardarEnJSON();
        redirect(exchange, "/dashboard?rol=admin");
    }

    // Endpoint para dar de baja tren
    private static void bajaTren(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idStr = params.getOrDefault("idTren", "");
        if (idStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int id = Integer.parseInt(idStr);
        com.ferrocarriles.models.Tren tren = gestorTrenes.buscarPorId(id);
        if (tren != null) {
            tren.setEstado(false); // Dar de baja (inactivo)
            gestorTrenes.actualizar(id, tren);
            gestorTrenes.guardarEnJSON();
        }
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void comprarBoleto(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        int estacionOrigen = Integer.parseInt(params.getOrDefault("estacionOrigen", "-1"));
        int estacionDestino = Integer.parseInt(params.getOrDefault("estacionDestino", "-1"));
        String tipoAsiento = params.getOrDefault("tipoAsiento", "Turista");
        String tipoEquipaje = params.getOrDefault("equipaje", "Ligero");
        int pesoEquipaje = Integer.parseInt(params.getOrDefault("pesoEquipaje", "0"));
        // Obtener userId desde la sesión o query
        String referer = exchange.getRequestHeaders().getFirst("Referer");
        int userId = -1;
        if (referer != null && referer.contains("userId=")) {
            String[] parts = referer.split("userId=");
            if (parts.length > 1) {
                try {
                    userId = Integer.parseInt(parts[1].split("&")[0]);
                } catch (Exception e) {
                }
            }
        }
        // Calcular ruta óptima usando el grafo
        com.ferrocarriles.estructuras.ListaEnlazadaDoble<Integer> caminoOptimo = gestorRutas
                .calcularRutaOptima(estacionOrigen, estacionDestino);
        if (caminoOptimo == null || caminoOptimo.getTamao() < 2 || userId == -1) {
            redirect(exchange, "/dashboard?rol=pasajero&userId=" + userId);
            return;
        }
        // Calcular precio sumando distancias de cada tramo
        double precioBasePorKm = 100.0;
        double precioPorKg = 10.0;
        double factorClase = tipoAsiento.equals("Premium") ? 2.0 : tipoAsiento.equals("Ejecutivo") ? 1.5 : 1.0;
        double factorEquipaje = tipoEquipaje.equals("Pesado") ? 2.0 : tipoEquipaje.equals("Mediano") ? 1.5 : 1.0;
        double distanciaTotal = 0.0;
        com.ferrocarriles.estructuras.ListaEnlazadaDoble.Nodo actual = caminoOptimo.getCabeza();
        Integer estacionActual = (Integer) actual.getDato();
        actual = actual.getSiguiente();
        while (actual != null) {
            Integer siguienteEstacion = (Integer) actual.getDato();
            // Buscar ruta entre estacionActual y siguienteEstacion
            com.ferrocarriles.estructuras.ListaEnlazadaDoble.Nodo nodoRuta = gestorRutas.obtenerTodas().getCabeza();
            while (nodoRuta != null) {
                com.ferrocarriles.models.Ruta r = (com.ferrocarriles.models.Ruta) nodoRuta.getDato();
                if ((r.getEstacionOrigenId() == estacionActual && r.getEstacionDestinoId() == siguienteEstacion) ||
                        (r.getEstacionOrigenId() == siguienteEstacion && r.getEstacionDestinoId() == estacionActual)) {
                    distanciaTotal += r.getDistancia();
                    break;
                }
                nodoRuta = nodoRuta.getSiguiente();
            }
            estacionActual = siguienteEstacion;
            actual = actual.getSiguiente();
        }
        double precio = distanciaTotal * precioBasePorKm * factorClase + pesoEquipaje * precioPorKg * factorEquipaje;
        // Crear boleto
        com.ferrocarriles.models.Boleto boleto = new com.ferrocarriles.models.Boleto();
        int nuevoId = 1;
        List<com.ferrocarriles.models.Boleto> boletosActuales = toList(gestorBoletos.obtenerTodos());
        if (!boletosActuales.isEmpty()) {
            nuevoId = boletosActuales.stream().mapToInt(com.ferrocarriles.models.Boleto::getIdBoleto).max().orElse(0)
                    + 1;
        }
        boleto.setIdBoleto(nuevoId);
        boleto.setPasajeroId(userId);
        boleto.setRutaId(-1); // -1 porque ahora el boleto representa varias rutas
        boleto.setEstacionOrigenId(estacionOrigen);
        boleto.setEstacionDestinoId(estacionDestino);
        // Guardar el camino más corto en el boleto
        java.util.List<Integer> caminoEstaciones = new ArrayList<>();
        com.ferrocarriles.estructuras.ListaEnlazadaDoble.Nodo nodoCamino = caminoOptimo.getCabeza();
        while (nodoCamino != null) {
            caminoEstaciones.add((Integer) nodoCamino.getDato());
            nodoCamino = nodoCamino.getSiguiente();
        }
        boleto.setCaminoEstaciones(caminoEstaciones);
        // Convertir tipoAsiento a un ID de asiento
        int idAsiento = 1;
        if ("Ejecutivo".equalsIgnoreCase(tipoAsiento)) {
            idAsiento = 2;
        } else if ("Premium".equalsIgnoreCase(tipoAsiento)) {
            idAsiento = 3;
        }
        boleto.setAsiento(idAsiento);
        boleto.setPrecio(precio);
        boleto.setFechaCompra(new java.util.Date());
        // Guardar equipaje
        List<com.ferrocarriles.models.Equipaje> equipajes = new ArrayList<>();
        com.ferrocarriles.models.Equipaje eq = new com.ferrocarriles.models.Equipaje();
        if (eq != null) {
            eq.setTipo(tipoEquipaje);
            eq.setPeso(pesoEquipaje);
        }
        equipajes.add(eq);
        boleto.setEquipaje(equipajes);
        gestorBoletos.vender(boleto);
        gestorBoletos.guardarEnJSON();
        redirect(exchange, "/dashboard?rol=pasajero&userId=" + userId);
    }

    private static void agregarRuta(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String nombre = params.getOrDefault("nombre", "").trim();
        String distanciaStr = params.getOrDefault("distancia", "").trim();
        String origenStr = params.getOrDefault("origen", "").trim();
        String destinoStr = params.getOrDefault("destino", "").trim();
        String estadoStr = params.getOrDefault("estado", "true").trim();
        if (nombre.isEmpty() || distanciaStr.isEmpty() || origenStr.isEmpty() || destinoStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        double distancia = Double.parseDouble(distanciaStr);
        int origen = Integer.parseInt(origenStr);
        int destino = Integer.parseInt(destinoStr);
        boolean estado = Boolean.parseBoolean(estadoStr);
        // Generar nuevo ID
        List<com.ferrocarriles.models.Ruta> rutas = ServidorHTTP.toList(gestorRutas.obtenerTodas());
        int nuevoId = rutas.stream().mapToInt(com.ferrocarriles.models.Ruta::getIdRuta).max().orElse(0) + 1;
        com.ferrocarriles.models.Ruta nueva = new com.ferrocarriles.models.Ruta();
        nueva.setIdRuta(nuevoId);
        nueva.setNombre(nombre);
        nueva.setDistancia(distancia);
        nueva.setEstacionOrigenId(origen);
        nueva.setEstacionDestinoId(destino);
        nueva.setEstado(estado);
        gestorRutas.agregar(nueva);
        gestorRutas.guardarEnJSON();
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void editarRuta(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idStr = params.getOrDefault("idRuta", "");
        String nombre = params.getOrDefault("nombre", "").trim();
        String distanciaStr = params.getOrDefault("distancia", "").trim();
        String origenStr = params.getOrDefault("origen", "").trim();
        String destinoStr = params.getOrDefault("destino", "").trim();
        String estadoStr = params.getOrDefault("estado", "true").trim();
        if (idStr.isEmpty() || nombre.isEmpty() || distanciaStr.isEmpty() || origenStr.isEmpty()
                || destinoStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int id = Integer.parseInt(idStr);
        double distancia = Double.parseDouble(distanciaStr);
        int origen = Integer.parseInt(origenStr);
        int destino = Integer.parseInt(destinoStr);
        boolean estado = Boolean.parseBoolean(estadoStr);
        com.ferrocarriles.models.Ruta ruta = gestorRutas.buscarPorId(id);
        if (ruta != null) {
            ruta.setNombre(nombre);
            ruta.setDistancia(distancia);
            ruta.setEstacionOrigenId(origen);
            ruta.setEstacionDestinoId(destino);
            ruta.setEstado(estado);
            gestorRutas.guardarEnJSON();
        }
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void borrarRuta(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idStr = params.getOrDefault("idRuta", "");
        if (idStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int id = Integer.parseInt(idStr);
        gestorRutas.eliminar(id);
        gestorRutas.guardarEnJSON();
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void agregarVagon(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        // Validar sesión y rol admin
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        com.ferrocarriles.models.Usuario usuario = null;
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                String[] kv = cookie.trim().split("=");
                if (kv.length == 2 && kv[0].equals("sessionId")) {
                    usuario = com.ferrocarriles.servidor.SessionManager.obtenerUsuario(kv[1]);
                }
            }
        }
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            redirect(exchange, "/login.html");
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idTrenStr = params.getOrDefault("idTren", "").trim();
        String capacidadStr = params.getOrDefault("capacidad", "").trim();
        String tipo = params.getOrDefault("tipo", "Pasajeros").trim();
        String clase = params.getOrDefault("clase", "Turista").trim();
        if (idTrenStr.isEmpty() || capacidadStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int idTren = Integer.parseInt(idTrenStr);
        int capacidad = Integer.parseInt(capacidadStr);
        int nuevoIdVagon;
        List<com.ferrocarriles.models.Asiento> asientos = new ArrayList<>();
        List<Map<String, Object>> asientosExistentes = new ArrayList<>();
        try {
            String jsonAsientos = new String(
                    java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("data/asientos.json")),
                    java.nio.charset.StandardCharsets.UTF_8);
            asientosExistentes = new com.fasterxml.jackson.databind.ObjectMapper().readValue(jsonAsientos, List.class);
        } catch (Exception e) {
        }
        int ultimoIdAsiento = asientosExistentes.stream()
                .mapToInt(a -> ((Number) a.get("idAsiento")).intValue()).max().orElse(0);

        // Obtener el nuevo ID de vagón según el tipo
        if ("Pasajeros".equalsIgnoreCase(tipo)) {
            List<com.ferrocarriles.models.VagonPasajeros> vagonesPasajeros = toList(
                    gestorVagones.obtenerTodosPasajeros());
            nuevoIdVagon = vagonesPasajeros.stream().mapToInt(com.ferrocarriles.models.VagonPasajeros::getIdVagon).max()
                    .orElse(0) + 1;
            // Constructor: VagonPasajeros(int idVagon, int idTren, boolean disponibilidad,
            // List<Asiento> asientos, List<Integer> pasajeros)
            List<Integer> pasajeros = new ArrayList<>();
            // Generar asientos
            for (int i = 1; i <= capacidad; i++) {
                int idAsiento = ultimoIdAsiento + i;
                com.ferrocarriles.models.Asiento asiento = new com.ferrocarriles.models.Asiento(idAsiento, "T" + i,
                        clase, null, true, nuevoIdVagon);
                asientos.add(asiento);
            }
            com.ferrocarriles.models.VagonPasajeros nuevoVagon = new com.ferrocarriles.models.VagonPasajeros(
                    nuevoIdVagon, idTren, true, asientos, pasajeros);
            gestorVagones.agregarVagonPasajeros(nuevoVagon);
            gestorVagones.guardarPasajerosEnJSON();
        } else if ("Equipaje".equalsIgnoreCase(tipo)) {
            List<com.ferrocarriles.models.VagonEquipaje> vagonesEquipaje = toList(gestorVagones.obtenerTodosEquipaje());
            nuevoIdVagon = vagonesEquipaje.stream().mapToInt(com.ferrocarriles.models.VagonEquipaje::getIdVagon).max()
                    .orElse(0) + 1;
            // Constructor: VagonEquipaje(int idVagon, int idTren, boolean disponibilidad)
            com.ferrocarriles.models.VagonEquipaje nuevoVagon = new com.ferrocarriles.models.VagonEquipaje(
                    nuevoIdVagon, idTren, true);
            gestorVagones.agregarVagonEquipaje(nuevoVagon);
            gestorVagones.guardarEquipajeEnJSON();
        } else {
            // Tipo no reconocido, no agregar
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        // Actualizar lista de vagones en el tren correspondiente
        com.ferrocarriles.models.Tren tren = gestorTrenes.buscarPorId(idTren);
        if (tren != null) {
            if ("Pasajeros".equalsIgnoreCase(tipo)) {
                tren.agregarVagonPasajeros(nuevoIdVagon);
            } else if ("Equipaje".equalsIgnoreCase(tipo)) {
                tren.agregarVagonEquipaje(nuevoIdVagon);
            }
            gestorTrenes.actualizar(idTren, tren);
            gestorTrenes.guardarEnJSON();
        }
        // Guardar asientos en JSON
        for (com.ferrocarriles.models.Asiento a : asientos) {
            Map<String, Object> m = new HashMap<>();
            m.put("idAsiento", a.getIdAsiento());
            m.put("idVagon", nuevoIdVagon);
            m.put("numeroAsiento", a.getNumeroAsiento());
            m.put("clase", a.getClase());
            m.put("nombre", null);
            m.put("disponibilidad", true);
            asientosExistentes.add(m);
        }
        try {
            String jsonFinal = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(asientosExistentes);
            java.nio.file.Files.write(java.nio.file.Paths.get("data/asientos.json"),
                    jsonFinal.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo guardar asientos.json: " + e.getMessage());
        }
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void editarVagon(HttpExchange exchange) throws IOException {
        // Validar sesión y rol admin
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        com.ferrocarriles.models.Usuario usuario = null;
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                String[] kv = cookie.trim().split("=");
                if (kv.length == 2 && kv[0].equals("sessionId")) {
                    usuario = com.ferrocarriles.servidor.SessionManager.obtenerUsuario(kv[1]);
                }
            }
        }
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            redirect(exchange, "/login.html");
            return;
        }
        // Implementar lógica de edición de vagón si es necesario
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void borrarVagon(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        // Validar sesión y rol admin
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        com.ferrocarriles.models.Usuario usuario = null;
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                String[] kv = cookie.trim().split("=");
                if (kv.length == 2 && kv[0].equals("sessionId")) {
                    usuario = com.ferrocarriles.servidor.SessionManager.obtenerUsuario(kv[1]);
                }
            }
        }
        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getRol())) {
            redirect(exchange, "/login.html");
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idStr = params.getOrDefault("idVagon", "");
        if (idStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int id = Integer.parseInt(idStr);
        // Eliminar según el tipo (se puede mejorar si se recibe el tipo por parámetro)
        // Por ahora, intentar eliminar de ambos tipos
        gestorVagones.eliminarVagonPasajeros(id);
        gestorVagones.eliminarVagonEquipaje(id);
        gestorVagones.guardarPasajerosEnJSON();
        gestorVagones.guardarEquipajeEnJSON();
        // También podrías eliminar los asientos asociados si lo deseas
        redirect(exchange, "/dashboard?rol=admin");
    }

    // Métodos mínimos, solo cabeceras. Implementa cada uno en su propio archivo si
    // lo deseas.
    private static void mostrarLogin(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                "<html lang='es'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<title>Login</title>" +
                "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet'>"
                +
                "<style>body { background: #f8f9fa; } .login-container { max-width: 400px; margin: 60px auto; padding: 32px; background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }</style>"
                +
                "</head>" +
                "<body>" +
                "<div class='login-container'>" +
                "<h2 class='mb-4 text-center'>Iniciar sesión</h2>" +
                "<form method='POST' action='/login'>" +
                "<div class='mb-3'>" +
                "<label for='userName' class='form-label'>Usuario</label>" +
                "<input type='text' class='form-control' id='userName' name='userName' required>" +
                "</div>" +
                "<div class='mb-3'>" +
                "<label for='password' class='form-label'>Contraseña</label>" +
                "<input type='password' class='form-control' id='password' name='password' required>" +
                "</div>" +
                "<button type='submit' class='btn btn-primary w-100'>Entrar</button>" +
                "</form>" +
                "</div>" +
                "</body></html>";
        byte[] resp = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }

    private static void procesarLogin(HttpExchange exchange) throws IOException {
        System.out.println("[LOGIN] procesarLogin llamado");
        System.out.println("[LOGIN] Método: " + exchange.getRequestMethod());
        if ("POST".equals(exchange.getRequestMethod())) {
            System.out.println("[LOGIN] Entrando a bloque POST");
            String body = leerInputStream(exchange.getRequestBody());
            System.out.println("[LOGIN] Body recibido: " + body);
            Map<String, String> params = parsearFormulario(body);
            String userName = params.getOrDefault("userName", "");
            String password = params.getOrDefault("password", "");
            System.out.println("[LOGIN] userName: " + userName + ", password: " + password);

            // Leer usuarios.json
            String jsonPath = "data/usuarios.json";
            java.nio.file.Path path = java.nio.file.Paths.get(jsonPath);
            String json = new String(java.nio.file.Files.readAllBytes(path),
                    java.nio.charset.StandardCharsets.UTF_8);
            List<Map<String, Object>> usuarios = new ArrayList<>();
            try {
                usuarios = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, List.class);
            } catch (Exception e) {
                System.out.println("[LOGIN] Error leyendo usuarios.json: " + e.getMessage());
                exchange.getResponseHeaders().set("Location", "/");
                exchange.sendResponseHeaders(302, -1);
                System.out.println("[LOGIN] Redirigiendo a / por error de lectura de usuarios.json");
                return;
            }
            boolean encontrado = false;
            String rol = "";
            String passwordHash = sha256(password);
            System.out.println("[LOGIN] passwordHash: " + passwordHash);
            for (Map<String, Object> usuario : usuarios) {
                Object passHashObj = usuario.get("passwordHash");
                String passStored = passHashObj != null ? String.valueOf(passHashObj) : "";
                System.out.println("[LOGIN] Comparando usuario: " + usuario.get("userName") + ", rol: "
                        + usuario.get("rol") + ", passHash: " + passStored);
                if (userName.equals(usuario.get("userName")) && passwordHash.equals(passStored)) {
                    rol = String.valueOf(usuario.get("rol"));
                    encontrado = true;
                    // Guardar el id del usuario autenticado
                    Object idObj = usuario.get("idUsuario");
                    int userId = -1;
                    if (idObj != null) {
                        try {
                            userId = Integer.parseInt(String.valueOf(idObj));
                        } catch (NumberFormatException e) {
                            userId = -1;
                        }
                    }
                    // Guardar el userId en el contexto para el redirect
                    usuario.put("_autenticadoId", userId);
                    break;
                }
            }
            if (encontrado) {
                System.out.println("[LOGIN] Usuario encontrado, rol: " + rol);
                // Login exitoso, redirigir al dashboard según rol
                String rolLower = rol.trim().toLowerCase();
                System.out.println("[LOGIN] Redirigiendo a dashboard con rol: " + rolLower);
                // Buscar el id del usuario autenticado
                int userId = -1;
                for (Map<String, Object> usuario : usuarios) {
                    if (usuario.containsKey("_autenticadoId")) {
                        Object idObj = usuario.get("_autenticadoId");
                        if (idObj != null) {
                            try {
                                userId = Integer.parseInt(String.valueOf(idObj));
                            } catch (NumberFormatException e) {
                                userId = -1;
                            }
                        }
                        break;
                    }
                }
                // Crear objeto Usuario y sesión
                com.ferrocarriles.models.Usuario usuarioSesion;
                if (rolLower.equals("admin")) {
                    usuarioSesion = new com.ferrocarriles.models.Administrador();
                } else if (rolLower.equals("operador")) {
                    usuarioSesion = new com.ferrocarriles.models.Operador();
                } else if (rolLower.equals("pasajero")) {
                    usuarioSesion = new com.ferrocarriles.models.Pasajero();
                } else {
                    usuarioSesion = null;
                }
                if (usuarioSesion != null) {
                    usuarioSesion.setIdUsuario(userId);
                    usuarioSesion.setUserName(userName);
                    usuarioSesion.setRol(rol);
                    String sessionId = com.ferrocarriles.servidor.SessionManager.crearSesion(usuarioSesion);
                    exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId + "; Path=/; HttpOnly");
                }
                if (rolLower.equals("admin")) {
                    redirect(exchange, "/dashboard?rol=admin");
                    System.out.println("[LOGIN] Redirección ejecutada a /dashboard?rol=admin");
                    return;
                } else if (rolLower.equals("operador")) {
                    redirect(exchange, "/dashboard-operador");
                    System.out.println("[LOGIN] Redirección ejecutada a /dashboard-operador");
                    return;
                } else if (rolLower.equals("pasajero")) {
                    redirect(exchange, "/dashboard?rol=pasajero&userId=" + userId);
                    System.out.println("[LOGIN] Redirección ejecutada a /dashboard?rol=pasajero&userId=" + userId);
                    return;
                } else {
                    // Rol desconocido
                    System.out.println("[LOGIN] Rol desconocido, enviando respuesta 403");
                    enviarRespuesta(exchange, "<h2>Rol desconocido</h2>", 403);
                    return;
                }
            } else {
                System.out.println("[LOGIN] Usuario o contraseña incorrectos");
                // Login fallido
                enviarRespuesta(exchange, "<h2>Usuario o contraseña incorrectos</h2>", 401);
            }
        }

    }

    // Método para enviar respuesta HTTP
    private static void enviarRespuesta(HttpExchange exchange, String html, int statusCode) throws IOException {
        byte[] resp = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }

    // Utilidad para leer el cuerpo de la petición
    public static String leerInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return new String(buffer.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
    }

    // Utilidad para parsear formulario x-www-form-urlencoded
    public static Map<String, String> parsearFormulario(String body) {
        Map<String, String> params = new HashMap<>();
        String[] pares = body.split("&");
        for (String par : pares) {
            String[] keyValue = par.split("=");
            if (keyValue.length == 2) {
                try {
                    params.put(java.net.URLDecoder.decode(keyValue[0], "UTF-8"),
                            java.net.URLDecoder.decode(keyValue[1], "UTF-8"));
                } catch (Exception e) {
                }
            }
        }
        return params;
    }

    // Métodos de guardado para los handlers
    public static void guardarEstacionesEnJSON() {
        gestorEstaciones.guardarEnJSON();
    }

    public static void guardarRutasEnJSON() {
        gestorRutas.guardarEnJSON();
    }

    public static void guardarTrenesEnJSON() {
        gestorTrenes.guardarEnJSON();
    }

    public static void guardarVagonesEnJSON() {
        gestorVagones.guardarPasajerosEnJSON();
        gestorVagones.guardarEquipajeEnJSON();
    }

    private static void mostrarDashboard(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String rol = "";
        int userId = -1;
        System.out.println("[DASHBOARD] mostrarDashboard llamado");
        System.out.println("[DASHBOARD] Query: " + query);
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("rol=")) {
                    rol = param.substring(4);
                }
                if (param.startsWith("userId=")) {
                    try {
                        userId = Integer.parseInt(param.substring(7));
                    } catch (NumberFormatException e) {
                        userId = -1;
                    }
                }
            }
        }
        System.out.println("[DASHBOARD] Rol detectado: " + rol);
        if ("admin".equalsIgnoreCase(rol)) {
            System.out.println("[DASHBOARD] Mostrando dashboard admin");
            DashboardAdminHandler dah = new DashboardAdminHandler(gestorEstaciones, gestorTrenes, gestorVagones,
                    gestorRutas);
            dah.handle(exchange);
            return;
        } else {
            System.out.println("[DASHBOARD] Solo está disponible el dashboard de administrador.");
            String html = "<html><body><h2>Dashboard solo disponible para el rol administrador.</h2></body></html>";
            byte[] resp = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, resp.length);
            OutputStream os = exchange.getResponseBody();
            os.write(resp);
            os.close();
        }
    }

    // Handler para dashboard de operador
    private static void mostrarDashboardOperador(HttpExchange exchange) throws IOException {
        String html = "<html><body><h2>Dashboard de operador no disponible.</h2></body></html>";
        byte[] resp = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }

    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }

    private static void mostrarEstaciones(HttpExchange exchange) throws IOException {
        // Implementar: mostrar estaciones
    }

    private static void mostrarTrenes(HttpExchange exchange) throws IOException {
        // Implementar: mostrar trenes
    }

    private static void mostrarVagones(HttpExchange exchange) throws IOException {
        // Implementar: mostrar vagones
    }

    private static void mostrarRutas(HttpExchange exchange) throws IOException {
    }
    // Implementar: mostrar rutas

    private static void agregarEstacion(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String nombre = params.getOrDefault("nombre", "").trim();
        String ciudad = params.getOrDefault("ciudad", "").trim();
        if (nombre.isEmpty() || ciudad.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        // Generar nuevo ID
        List<com.ferrocarriles.models.Estacion> estaciones = gestorEstaciones.obtenerTodas();
        int nuevoId = estaciones.stream().mapToInt(com.ferrocarriles.models.Estacion::getIdEstacion).max().orElse(0)
                + 1;
        com.ferrocarriles.models.Estacion nueva = new com.ferrocarriles.models.Estacion();
        nueva.setIdEstacion(nuevoId);
        nueva.setNombre(nombre);
        nueva.setMunicipio(ciudad);
        gestorEstaciones.agregar(nueva);
        gestorEstaciones.guardarEnJSON();
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void editarEstacion(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idStr = params.getOrDefault("idEstacion", "");
        String nombre = params.getOrDefault("nombre", "").trim();
        String ciudad = params.getOrDefault("ciudad", "").trim();
        if (idStr.isEmpty() || nombre.isEmpty() || ciudad.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int id = Integer.parseInt(idStr);
        com.ferrocarriles.models.Estacion estacion = gestorEstaciones.buscarPorId(id);
        if (estacion != null) {
            estacion.setNombre(nombre);
            estacion.setMunicipio(ciudad);
            gestorEstaciones.actualizar(id, estacion);
            gestorEstaciones.guardarEnJSON();
        }
        redirect(exchange, "/dashboard?rol=admin");
    }

    private static void borrarEstacion(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        String idStr = params.getOrDefault("idEstacion", "");
        if (idStr.isEmpty()) {
            redirect(exchange, "/dashboard?rol=admin");
            return;
        }
        int id = Integer.parseInt(idStr);
        gestorEstaciones.eliminar(id);
        gestorEstaciones.guardarEnJSON();
        redirect(exchange, "/dashboard?rol=admin");
    }

    // Utilidad para obtener el hash SHA-256 de un texto
    private static String sha256(String base) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private static void calcularEstimado(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String, String> params = parseFormData(exchange);
        int estacionOrigen = Integer.parseInt(params.getOrDefault("estacionOrigen", "-1"));
        int estacionDestino = Integer.parseInt(params.getOrDefault("estacionDestino", "-1"));
        String tipoAsiento = params.getOrDefault("tipoAsiento", "Turista");
        String tipoEquipaje = params.getOrDefault("equipaje", "Ligero");
        int pesoEquipaje = Integer.parseInt(params.getOrDefault("pesoEquipaje", "0"));
        int cantidad = Integer.parseInt(params.getOrDefault("cantidad", "1"));
        int userId = -1;
        String referer = exchange.getRequestHeaders().getFirst("Referer");
        if (referer != null && referer.contains("userId=")) {
            String[] partes = referer.split("userId=");
            if (partes.length > 1) {
                try {
                    userId = Integer.parseInt(partes[1].split("&")[0]);
                } catch (Exception e) {
                    userId = -1;
                }
            }
        }
        int distanciaTotal = gestorRutas.obtenerDistancia(estacionOrigen, estacionDestino);
        double precioBasePorKm = 100.0;
        double precioPorKg = 10.0;
        double factorClase = tipoAsiento.equals("Premium") ? 2.0 : tipoAsiento.equals("Ejecutivo") ? 1.5 : 1.0;
        double factorEquipaje = tipoEquipaje.equals("Pesado") ? 2.0 : tipoEquipaje.equals("Mediano") ? 1.5 : 1.0;
        double precio = distanciaTotal * precioBasePorKm * factorClase + pesoEquipaje * precioPorKg * factorEquipaje;
        precio *= cantidad;
        String resultadoEstimado = "<div class='alert alert-info mt-3'><strong>Precio estimado:</strong> "
                + String.format("%.2f", precio) + " $ para " + cantidad + " boleto(s).<br>Distancia total: "
                + distanciaTotal + " km</div>";
        // Solo disponible para admin
        String html = "<html><body><h2>Estimación solo disponible para el rol administrador.</h2></body></html>";
        byte[] resp = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, resp.length);
        OutputStream os = exchange.getResponseBody();
        os.write(resp);
        os.close();
    }

}
