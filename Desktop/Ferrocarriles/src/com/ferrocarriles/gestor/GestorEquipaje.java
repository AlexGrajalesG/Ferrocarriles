package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Equipaje;
import com.ferrocarriles.estructuras.Pila;
import com.ferrocarriles.estructuras.TablaHash;
import com.ferrocarriles.estructuras.ListaEnlazadaDoble;
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
 *  GESTOR DE EQUIPAJE
 * 
 * Estructuras usadas:
 * - Pila: ltimo en llegar, primero en revisar (LIFO)
 * - TablaHash: Bsqueda por ID O(1)
 * - Lista: Historial completo
 */
public class GestorEquipaje {

    private Pila<Equipaje> pilaRevision;
    private TablaHash<Integer, Equipaje> tablaPorId;
    private ListaEnlazadaDoble<Equipaje> listaEquipaje;

    public GestorEquipaje() {
        this.pilaRevision = new Pila<>();
        this.tablaPorId = new TablaHash<>(200);
        this.listaEquipaje = new ListaEnlazadaDoble<>();
    }

    /**
     * REGISTRAR equipaje (apilar para revision)
     */
    public boolean registrar(Equipaje equipaje) {
        if (tablaPorId.buscar(equipaje.getIdEquipaje()) != null) {
            System.out.println(" Equipaje ya existe: " + equipaje.getIdEquipaje());
            return false;
        }

        // Validar peso
        if (!equipaje.validarPeso()) {
            System.out.println(" Equipaje rechazado por exceso de peso");
            return false;
        }

        // Apilar para revision
        pilaRevision.push(equipaje);
        System.out.println(" Equipaje apilado para revisinn: " + equipaje.getIdEquipaje());
        return true;
    }

    /**
     *  REVISAR equipaje (desapilar)
     */
    public Equipaje revisar() {
        if (pilaRevision.estaVacia()) {
            System.out.println("No hay equipaje pendiente de revisar");
            return null;
        }

        Equipaje equipaje = pilaRevision.pop();

        // Agregar a estructuras permanentes
        tablaPorId.insertar(equipaje.getIdEquipaje(), equipaje);
        listaEquipaje.agregarAlFinal(equipaje);

        System.out.println("Equipaje revisado y aprobado: " + equipaje.getIdEquipaje());
        return equipaje;
    }

    /**
     * BUSCAR por ID
     */
    public Equipaje buscarPorId(int id) {
        return tablaPorId.buscar(id);
    }

    /**
     * BUSCAR por pasajero
     */
    public ListaEnlazadaDoble<Equipaje> buscarPorPasajero(int pasajeroId) {
        ListaEnlazadaDoble<Equipaje> equipajes = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaEquipaje.getCabeza();

        while (actual != null) {
            if (((Equipaje) actual.getDato()).getPasajeroId() == pasajeroId) {
                equipajes.agregarAlFinal((Equipaje) actual.getDato());
            }
            actual = actual.getSiguiente();
        }

        return equipajes;
    }

    /**
     * OBTENER todos
     */
    public ListaEnlazadaDoble<Equipaje> obtenerTodos() {
        return listaEquipaje;
    }

    /**
     * OBTENER pendientes de revisinn
     */
    public int contarPendientes() {
        return pilaRevision.getTamao();
    }

    /**
     * ESTADISTICAS
     */
    public int contarTotal() {
        return listaEquipaje.getTamao();
    }

    public double calcularPesoTotal() {
        double peso = 0;
        ListaEnlazadaDoble.Nodo actual = listaEquipaje.getCabeza();

        while (actual != null) {
            peso += ((Equipaje) actual.getDato()).getPeso();
            actual = actual.getSiguiente();
        }

        return peso;
    }

    public void limpiar() {
        pilaRevision = new Pila<>();
        tablaPorId = new TablaHash<>(200);
        listaEquipaje = new ListaEnlazadaDoble<>();
    }

    // ============================================
    // PERSISTENCIA JSON
    // ============================================

    private static final String RUTA_JSON = "data/equipaje.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void guardarEnJSON() {
        try {
            List<Equipaje> equipajes = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaEquipaje.getCabeza();

            while (actual != null) {
                equipajes.add((Equipaje) actual.getDato());
                actual = actual.getSiguiente();
            }

            String json = gson.toJson(equipajes);
            Path archivo = Paths.get(RUTA_JSON);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));

            System.out.println("Guardados " + equipajes.size() + " equipajes en " + RUTA_JSON);
        } catch (IOException e) {
            System.out.println(" Error al guardar equipaje: " + e.getMessage());
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
            List<Equipaje> equipajes = gson.fromJson(json, new TypeToken<List<Equipaje>>() {
            }.getType());

            if (equipajes != null) {
                // Solo limpiar tablas, NO la pila (los pendientes no se guardan)
                tablaPorId = new TablaHash<>(200);
                listaEquipaje = new ListaEnlazadaDoble<>();

                for (Equipaje e : equipajes) {
                    tablaPorId.insertar(e.getIdEquipaje(), e);
                    listaEquipaje.agregarAlFinal(e);
                }
                System.out.println(" Cargados " + equipajes.size() + " equipajes desde " + RUTA_JSON);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar equipaje: " + e.getMessage());
        }
    }
}
