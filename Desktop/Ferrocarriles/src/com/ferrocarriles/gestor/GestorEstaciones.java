package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Estacion;
import com.ferrocarriles.estructuras.*;
import java.util.*;

public class GestorEstaciones {
    private static final String RUTA_JSON = "data/estaciones.json";
    private com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();

    private ListaEnlazadaDoble<Estacion> listaEstaciones;
    private ArbolAVL<Integer, Estacion> arbolPorId;
    private TablaHash<String, Estacion> tablaPorNombre;
    private Grafo<Integer> grafoRutas;
    private Pila<Operacion> pilaDeshacer;
    private Pila<Operacion> pilaRehacer;
    private Cola<Operacion> colaOperaciones;

    public GestorEstaciones() {
        this.listaEstaciones = new ListaEnlazadaDoble<>();
        this.arbolPorId = new ArbolAVL<>();
        this.tablaPorNombre = new TablaHash<>();
        this.grafoRutas = new Grafo<>();
        this.pilaDeshacer = new Pila<>(50);
        this.pilaRehacer = new Pila<>(50);
        this.colaOperaciones = new Cola<>();
    }

    public void cargarDesdeJSON() {
        try {
            java.nio.file.Path archivo = java.nio.file.Paths.get(RUTA_JSON);
            if (!java.nio.file.Files.exists(archivo)) {
                System.out.println("  Archivo " + RUTA_JSON + " no existe. Iniciando con lista vacía.");
                return;
            }
            String json = new String(java.nio.file.Files.readAllBytes(archivo),
                    java.nio.charset.StandardCharsets.UTF_8);
            List<Estacion> estaciones = gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<Estacion>>() {
            }.getType());
            if (estaciones != null) {
                this.listaEstaciones = new ListaEnlazadaDoble<>();
                this.arbolPorId = new ArbolAVL<>();
                this.tablaPorNombre = new TablaHash<>();
                this.grafoRutas = new Grafo<>();
                for (Estacion e : estaciones) {
                    agregar(e);
                }
                System.out.println(" Cargadas " + estaciones.size() + " estaciones desde " + RUTA_JSON);
            }
        } catch (Exception e) {
            System.out.println(" Error al cargar estaciones: " + e.getMessage());
        }
    }

    public void guardarEnJSON() {
        try {
            List<Estacion> estaciones = new ArrayList<>();
            for (Estacion e : listaEstaciones) {
                estaciones.add(e);
            }
            String json = gson.toJson(estaciones);
            java.nio.file.Path archivo = java.nio.file.Paths.get(RUTA_JSON);
            java.nio.file.Files.createDirectories(archivo.getParent());
            java.nio.file.Files.write(archivo, json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            System.out.println(" Guardadas " + estaciones.size() + " estaciones en " + RUTA_JSON);
        } catch (Exception e) {
            System.out.println(" Error al guardar estaciones: " + e.getMessage());
        }
    }

    public Estacion agregar(Estacion estacion) {
        if (estacion == null) {
            throw new IllegalArgumentException("La estación no puede ser null");
        }
        if (tablaPorNombre.contiene(estacion.getNombre().toLowerCase())) {
            throw new IllegalStateException("Ya existe una estación con el nombre: " + estacion.getNombre());
        }
        listaEstaciones.agregarAlFinal(estacion);
        arbolPorId.insertar(estacion.getIdEstacion(), estacion);
        tablaPorNombre.insertar(estacion.getNombre().toLowerCase(), estacion);
        grafoRutas.agregarVertice(estacion.getIdEstacion());
        Operacion operacion = new Operacion(Operacion.Tipo.AGREGAR, null, estacion);
        pilaDeshacer.push(operacion);
        pilaRehacer.limpiar();
        System.out.println(" Estación agregada: " + estacion.getNombre());
        return estacion;
    }

    public Estacion buscarPorId(int id) {
        return arbolPorId.buscar(id);
    }

    public Estacion buscarPorNombre(String nombre) {
        if (nombre == null)
            return null;
        return tablaPorNombre.buscar(nombre.toLowerCase());
    }

    public Estacion actualizar(int id, Estacion nuevosDatos) {
        Estacion estacionVieja = buscarPorId(id);
        if (estacionVieja == null) {
            throw new NoSuchElementException("No se encontró estación con ID: " + id);
        }
        Estacion otraConMismoNombre = buscarPorNombre(nuevosDatos.getNombre());
        if (otraConMismoNombre != null && otraConMismoNombre.getIdEstacion() != id) {
            throw new IllegalStateException("Ya existe otra estación con el nombre: " + nuevosDatos.getNombre());
        }
        Estacion copiaVieja = new Estacion(estacionVieja);
        arbolPorId.insertar(id, nuevosDatos);
        if (!estacionVieja.getNombre().equalsIgnoreCase(nuevosDatos.getNombre())) {
            tablaPorNombre.eliminar(estacionVieja.getNombre().toLowerCase());
            tablaPorNombre.insertar(nuevosDatos.getNombre().toLowerCase(), nuevosDatos);
        } else {
            tablaPorNombre.insertar(nuevosDatos.getNombre().toLowerCase(), nuevosDatos);
        }
        Operacion operacion = new Operacion(Operacion.Tipo.ACTUALIZAR, copiaVieja, nuevosDatos);
        pilaDeshacer.push(operacion);
        pilaRehacer.limpiar();
        System.out.println(" Estación actualizada: " + nuevosDatos.getNombre());
        return nuevosDatos;
    }

    public boolean eliminar(int id) {
        Estacion estacion = buscarPorId(id);
        if (estacion == null) {
            return false;
        }
        tablaPorNombre.eliminar(estacion.getNombre().toLowerCase());
        Operacion operacion = new Operacion(Operacion.Tipo.ELIMINAR, estacion, null);
        pilaDeshacer.push(operacion);
        pilaRehacer.limpiar();
        System.out.println(" Estación eliminada: " + estacion.getNombre());
        return true;
    }

    public List<Estacion> obtenerTodas() {
        List<Estacion> lista = new ArrayList<>();
        for (Estacion e : listaEstaciones) {
            lista.add(e);
        }
        return lista;
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEstaciones", listaEstaciones.getTamao());
        stats.put("operacionesDeshacer", pilaDeshacer.getTamao());
        stats.put("operacionesRehacer", pilaRehacer.getTamao());
        stats.put("operacionesPendientes", colaOperaciones.getTamao());
        stats.put("alturaArbolAVL", arbolPorId.getAltura());
        stats.put("verticesGrafo", grafoRutas.numeroVertices());
        stats.put("estadisticasHash", tablaPorNombre.obtenerEstadisticas());
        return stats;
    }

    public void imprimirEstadisticas() {
        System.out.println("\n ========================================");
        System.out.println(" ESTADÍSTICAS DEL GESTOR DE ESTACIONES");
        System.out.println(" ========================================");
        Map<String, Object> stats = obtenerEstadisticas();
        System.out.println(" Total estaciones: " + stats.get("totalEstaciones"));
        System.out.println("Operaciones deshacer: " + stats.get("operacionesDeshacer"));
        System.out.println(" Operaciones rehacer: " + stats.get("operacionesRehacer"));
        System.out.println(" Operaciones pendientes: " + stats.get("operacionesPendientes"));
        System.out.println(" Altura árbol AVL: " + stats.get("alturaArbolAVL"));
        System.out.println(" vértices en grafo: " + stats.get("verticesGrafo"));
        System.out.println("# " + stats.get("estadisticasHash"));
        System.out.println("========================================\n");
    }

    public static class Operacion {
        public enum Tipo {
            AGREGAR, ACTUALIZAR, ELIMINAR
        }

        private Tipo tipo;
        private Estacion estacionVieja;
        private Estacion estacionNueva;
        private long timestamp;

        public Operacion(Tipo tipo, Estacion estacionVieja, Estacion estacionNueva) {
            this.tipo = tipo;
            this.estacionVieja = estacionVieja;
            this.estacionNueva = estacionNueva;
            this.timestamp = System.currentTimeMillis();
        }

        public Tipo getTipo() {
            return tipo;
        }

        public Estacion getEstacionVieja() {
            return estacionVieja;
        }

        public Estacion getEstacionNueva() {
            return estacionNueva;
        }

        @Override
        public String toString() {
            return String.format("%s - %s", tipo,
                    estacionNueva != null ? estacionNueva.getNombre() : estacionVieja.getNombre());
        }
    }
}
