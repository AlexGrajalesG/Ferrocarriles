package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Boleto;
import com.ferrocarriles.estructuras.Cola;
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
 * GESTOR DE BOLETOS
 * 
 * Estructuras usadas:
 * - Cola: Fila de venta (primero en comprar, primero en procesar)
 * - TablaHash: Busqueda rapida por ID O(1)
 * - Lista: Historial completo
 */
public class GestorBoletos {

    private Cola<Boleto> colaVenta;
    private TablaHash<Integer, Boleto> tablaPorId;
    private ListaEnlazadaDoble<Boleto> listaBoletos;

    public GestorBoletos() {
        this.colaVenta = new Cola<>();
        this.tablaPorId = new TablaHash<>(200);
        this.listaBoletos = new ListaEnlazadaDoble<>();
    }

    /**
     * VENDER boleto (encolar para procesamiento)
     */
    public boolean vender(Boleto boleto) {
        if (tablaPorId.buscar(boleto.getIdBoleto()) != null) {
            System.out.println(" Boleto ya existe: " + boleto.getIdBoleto());
            return false;
        }
        // Calcular precio antes de encolar
        boleto.setPrecio(boleto.calcularPrecio());
        colaVenta.encolar(boleto);
        System.out.println(
                " Boleto encolado para procesamiento: " + boleto.getIdBoleto() + " | Precio: $" + boleto.getPrecio());
        return true;
    }

    /**
     * CONFIRMAR boleto (procesar de la cola)
     */
    public Boleto confirmar() {
        if (colaVenta.estaVacia()) {
            System.out.println("  No hay boletos pendientes de confirmar");
            return null;
        }
        Boleto boleto = colaVenta.desencolar();
        // Calcular precio antes de guardar
        boleto.setPrecio(boleto.calcularPrecio());
        tablaPorId.insertar(boleto.getIdBoleto(), boleto);
        listaBoletos.agregarAlFinal(boleto);
        System.out.println(" Boleto confirmado: " + boleto.getIdBoleto() + " | Precio: $" + boleto.getPrecio());
        return boleto;
    }

    /**
     * BUSCAR por ID
     */
    public Boleto buscarPorId(int id) {
        return tablaPorId.buscar(id);
    }

    /**
     * BUSCAR por pasajero
     */
    public ListaEnlazadaDoble<Boleto> buscarPorPasajero(int pasajeroId) {
        ListaEnlazadaDoble<Boleto> boletos = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaBoletos.getCabeza();

        while (actual != null) {
            if (((Boleto) actual.getDato()).getPasajeroId() == pasajeroId) {
                boletos.agregarAlFinal((Boleto) actual.getDato());
            }
            actual = actual.getSiguiente();
        }

        return boletos;
    }

    /**
     * CANCELAR boleto
     */
    public boolean cancelar(int id) {
        Boleto boleto = tablaPorId.buscar(id);

        if (boleto == null) {
            System.out.println(" Boleto no encontrado: " + id);
            return false;
        }

        boleto.cancelar();
        return true;
    }

    /**
     * OBTENER todos
     */
    public ListaEnlazadaDoble<Boleto> obtenerTodos() {
        return listaBoletos;
    }

    /**
     * OBTENER pendientes de confirmar
     */
    public int contarPendientes() {
        return colaVenta.getTamao();
    }

    /**
     * ESTADISTICAS
     */
    public int contarTotal() {
        return listaBoletos.getTamao();
    }

    public int contarActivos() {
        int contador = 0;
        ListaEnlazadaDoble.Nodo actual = listaBoletos.getCabeza();

        while (actual != null) {
            if (((Boleto) actual.getDato()).isEstado()) {
                contador++;
            }
            actual = actual.getSiguiente();
        }

        return contador;
    }

    public double calcularVentaTotal() {
        double total = 0;
        ListaEnlazadaDoble.Nodo actual = listaBoletos.getCabeza();

        while (actual != null) {
            if (((Boleto) actual.getDato()).isEstado()) {
                total += ((Boleto) actual.getDato()).getPrecio();
            }
            actual = actual.getSiguiente();
        }

        return total;
    }

    public void limpiar() {
        colaVenta = new Cola<>();
        tablaPorId = new TablaHash<>(200);
        listaBoletos = new ListaEnlazadaDoble<>();
    }

    // ============================================
    // PERSISTENCIA JSON
    // ============================================

    private static final String RUTA_JSON = "data/boletos.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void guardarEnJSON() {
        try {
            List<Boleto> boletos = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaBoletos.getCabeza();

            while (actual != null) {
                boletos.add((Boleto) actual.getDato());
                actual = actual.getSiguiente();
            }

            String json = gson.toJson(boletos);
            Path archivo = Paths.get(RUTA_JSON);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));

            System.out.println(" Guardados " + boletos.size() + " boletos en " + RUTA_JSON);
        } catch (IOException e) {
            System.out.println(" Error al guardar boletos: " + e.getMessage());
        }
    }

    public void cargarDesdeJSON() {
        try {
            Path archivo = Paths.get(RUTA_JSON);

            if (!Files.exists(archivo)) {
                System.out.println("  Archivo " + RUTA_JSON + " no existe. Iniciando con lista vacna.");
                return;
            }

            String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
            List<Boleto> boletos = gson.fromJson(json, new TypeToken<List<Boleto>>() {
            }.getType());

            if (boletos != null) {
                // Solo limpiar tablas, NO la cola (los pendientes no se guardan)
                tablaPorId = new TablaHash<>(200);
                listaBoletos = new ListaEnlazadaDoble<>();

                for (Boleto b : boletos) {
                    tablaPorId.insertar(b.getIdBoleto(), b);
                    listaBoletos.agregarAlFinal(b);
                }
                System.out.println(" Cargados " + boletos.size() + " boletos desde " + RUTA_JSON);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar boletos: " + e.getMessage());
        }
    }
}
