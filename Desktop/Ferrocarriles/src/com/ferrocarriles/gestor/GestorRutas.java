package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Ruta;
import com.ferrocarriles.estructuras.Grafo;
import com.ferrocarriles.estructuras.ListaEnlazadaDoble;
import com.ferrocarriles.estructuras.TablaHash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * GESTOR DE RUTAS
 * 
 * Estructuras usadas:
 * - Grafo: Rutas entre estaciones + Dijkstra para ruta nptima
 * - TablaHash: Busqueda por ID O(1)
 * - Lista: Orden de registro
 */
public class GestorRutas {
    // Stub para compatibilidad con ServidorHTTP y otros
    public ListaEnlazadaDoble<Integer> calcularRutaOptima(int origen, int destino) {
        ListaEnlazadaDoble<Integer> resultado = new ListaEnlazadaDoble<>();
        List<Integer> camino = obtenerCaminoOptimo(origen, destino);
        for (Integer est : camino) {
            resultado.agregarAlFinal(est);
        }
        return resultado;
    }

    private Grafo<Integer> grafoRutas; // Grafo de estaciones
    private TablaHash<Integer, Ruta> tablaPorId;
    private ListaEnlazadaDoble<Ruta> listaRutas;

    public GestorRutas() {
        this.grafoRutas = new Grafo<>();
        this.tablaPorId = new TablaHash<>(100);
        this.listaRutas = new ListaEnlazadaDoble<>();
    }

    /**
     * AGREGAR ruta
     */
    public boolean agregar(Ruta ruta) {
        if (tablaPorId.buscar(ruta.getIdRuta()) != null) {
            System.out.println(" Ruta ya existe: " + ruta.getIdRuta());
            return false;
        }

        // Agregar a estructuras
        tablaPorId.insertar(ruta.getIdRuta(), ruta);
        listaRutas.agregarAlFinal(ruta);

        // Agregar al grafo
        grafoRutas.agregarArista(
                ruta.getEstacionOrigenId(),
                ruta.getEstacionDestinoId(),
                (int) ruta.getDistancia());

        System.out.println(" Ruta agregada: " + ruta.getNombre());
        return true;
    }

    /**
     * BUSCAR por ID
     */
    public Ruta buscarPorId(int id) {
        return tablaPorId.buscar(id);
    }

    /**
     * CALCULAR ruta óptima (Dijkstra) y devolver lista de IDs de estaciones
     */
    public List<Integer> obtenerCaminoOptimo(int estacionOrigen, int estacionDestino) {
        System.out.println("Calculando camino óptimo de estación " + estacionOrigen + " a " + estacionDestino);
        Grafo.RutaDijkstra<Integer> resultado = grafoRutas.dijkstra(estacionOrigen, estacionDestino);
        if (resultado == null || !resultado.existe()) {
            return new ArrayList<>();
        }
        return resultado.getCamino();
    }

    /**
     * OBTENER distancia entre estaciones
     */
    public int obtenerDistancia(int origen, int destino) {
        ListaEnlazadaDoble<Integer> ruta = calcularRutaOptima(origen, destino);

        if (ruta == null || ruta.getTamao() == 0) {
            return -1; // No hay ruta
        }

        // Sumar distancias
        int distanciaTotal = 0;
        ListaEnlazadaDoble.Nodo actual = ruta.getCabeza();
        Integer estacionActual = (Integer) actual.getDato();
        actual = actual.getSiguiente();

        while (actual != null) {
            Integer siguienteEstacion = (Integer) actual.getDato();

            // Buscar ruta entre estacionActual y siguienteEstacion
            ListaEnlazadaDoble.Nodo nodoRuta = listaRutas.getCabeza();
            while (nodoRuta != null) {
                Ruta r = (Ruta) nodoRuta.getDato();
                if (r.getEstacionOrigenId() == estacionActual && r.getEstacionDestinoId() == siguienteEstacion) {
                    distanciaTotal += (int) r.getDistancia();
                    break;
                }
                nodoRuta = nodoRuta.getSiguiente();
            }

            estacionActual = siguienteEstacion;
            actual = actual.getSiguiente();
        }

        return distanciaTotal;
    }

    /**
     * ACTUALIZAR ruta
     */
    public boolean actualizar(int id, Ruta rutaActualizada) {
        Ruta actual = tablaPorId.buscar(id);

        if (actual == null) {
            System.out.println(" Ruta no encontrada: " + id);
            return false;
        }

        tablaPorId.insertar(id, rutaActualizada);

        ListaEnlazadaDoble.Nodo nodo = listaRutas.getCabeza();
        while (nodo != null) {
            if (((Ruta) nodo.getDato()).getIdRuta() == id) {
                // No hay setDato
                break;
            }
            nodo = nodo.getSiguiente();
        }

        // Actualizar grafo
        grafoRutas.agregarArista(
                rutaActualizada.getEstacionOrigenId(),
                rutaActualizada.getEstacionDestinoId(),
                (int) rutaActualizada.getDistancia());

        System.out.println(" Ruta actualizada: " + id);
        return true;
    }

    /**
     * ELIMINAR ruta
     */
    public boolean eliminar(int id) {
        Ruta ruta = tablaPorId.buscar(id);

        if (ruta == null) {
            System.out.println(" Ruta no encontrada: " + id);
            return false;
        }

        tablaPorId.eliminar(id);

        ListaEnlazadaDoble.Nodo actual = listaRutas.getCabeza();
        while (actual != null) {
            if (((Ruta) actual.getDato()).getIdRuta() == id) {
                listaRutas.eliminar(actual);
                break;
            }
            actual = actual.getSiguiente();
        }

        System.out.println(" Ruta eliminada: " + id);
        return true;
    }

    /**
     * OBTENER todas
     */
    public ListaEnlazadaDoble<Ruta> obtenerTodas() {
        return listaRutas;
    }

    /**
     * OBTENER solo activas
     */
    public ListaEnlazadaDoble<Ruta> obtenerActivas() {
        ListaEnlazadaDoble<Ruta> activas = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaRutas.getCabeza();

        while (actual != null) {
            if (((Ruta) actual.getDato()).isEstado()) {
                activas.agregarAlFinal((Ruta) actual.getDato());
            }
            actual = actual.getSiguiente();
        }

        return activas;
    }

    /**
     * Estadisticas
     */
    public int contarTotal() {
        return listaRutas.getTamao();
    }

    public int contarActivas() {
        int contador = 0;
        ListaEnlazadaDoble.Nodo actual = listaRutas.getCabeza();

        while (actual != null) {
            if (((Ruta) actual.getDato()).isEstado()) {
                contador++;
            }
            actual = actual.getSiguiente();
        }

        return contador;
    }

    public void limpiar() {
        grafoRutas = new Grafo<>();
        tablaPorId = new TablaHash<>(100);
        listaRutas = new ListaEnlazadaDoble<>();
    }

    // ============================================
    // PERSISTENCIA JSON
    // ============================================

    private static final String RUTA_JSON = "data/rutas.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void guardarEnJSON() {
        try {
            List<Ruta> rutas = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaRutas.getCabeza();

            while (actual != null) {
                rutas.add((Ruta) actual.getDato());
                actual = actual.getSiguiente();
            }

            String json = gson.toJson(rutas);
            Path archivo = Paths.get(RUTA_JSON);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));

            System.out.println(" Guardadas " + rutas.size() + " rutas en " + RUTA_JSON);
        } catch (IOException e) {
            System.out.println(" Error al guardar rutas: " + e.getMessage());
        }
    }

    public void cargarDesdeJSON() {
        try {
            Path archivo = Paths.get(RUTA_JSON);

            if (!Files.exists(archivo)) {
                System.out.println(" Archivo " + RUTA_JSON + " no existe. Iniciando con lista vaca.");
                return;
            }

            String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
            List<Ruta> rutas = gson.fromJson(json, new TypeToken<List<Ruta>>() {
            }.getType());

            if (rutas != null) {
                limpiar();
                for (Ruta r : rutas) {
                    agregar(r);
                }
                System.out.println(" Cargadas " + rutas.size() + " rutas desde " + RUTA_JSON);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar rutas: " + e.getMessage());
        }
    }
}
