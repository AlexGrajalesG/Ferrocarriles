/**
     * Crear una salida (viaje) con recorrido óptimo entre estaciones usando gestor de rutas
     */
    
package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Salida;
import com.ferrocarriles.estructuras.ArbolAVL;
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
import java.util.Date;
import java.util.List;

/**
 *  GESTOR DE SALIDAS (Horarios)
 * 
 * Estructuras usadas:
 * - ArbolAVL: Ordenar por fecha/hora O(log n)
 * - Lista: Orden cronolngico
 */
public class GestorSalidas {

    private ArbolAVL<Integer, Salida> arbolPorId;
    private ListaEnlazadaDoble<Salida> listaSalidas;
    public Salida crearSalidaConRecorrido(int idSalida, int trenId, int estacionOrigenId, int estacionDestinoId, Date fechaHoraSalida, GestorRutas gestorRutas, int rutaId) {
        // Calcular recorrido óptimo
        List<Integer> recorrido = gestorRutas.obtenerCaminoOptimo(estacionOrigenId, estacionDestinoId);
        // Crear salida con recorrido
        Salida salida = new Salida(idSalida, fechaHoraSalida, trenId, rutaId, true, recorrido);
        // Guardar salida
        agregar(salida);
        // Mostrar recorrido
        System.out.println("Recorrido óptimo para salida " + idSalida + ": " + recorrido);
        return salida;
    }
    public GestorSalidas() {
        this.arbolPorId = new ArbolAVL<>();
        this.listaSalidas = new ListaEnlazadaDoble<>();
    }

    /**
     *  AGREGAR salida
     */
    public boolean agregar(Salida salida) {
        if (arbolPorId.buscar(salida.getIdSalida()) != null) {
            System.out.println(" Salida ya existe: " + salida.getIdSalida());
            return false;
        }

        arbolPorId.insertar(salida.getIdSalida(), salida);
        listaSalidas.agregarAlFinal(salida);

        System.out.println(" Salida agregada: " + salida.getIdSalida());
        return true;
    }

    /**
     *  BUSCAR por ID
     */
    public Salida buscarPorId(int id) {
        return arbolPorId.buscar(id);
    }

    /**
     *  BUSCAR por tren
     */
    public ListaEnlazadaDoble<Salida> buscarPorTren(int trenId) {
        ListaEnlazadaDoble<Salida> salidas = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaSalidas.getCabeza();

        while (actual != null) {
            if (((Salida) actual.getDato()).getTrenId() == trenId) {
                salidas.agregarAlFinal((Salida) actual.getDato());
            }
            actual = actual.getSiguiente();
        }

        return salidas;
    }

    /**
     *  BUSCAR por ruta
     */
    public ListaEnlazadaDoble<Salida> buscarPorRuta(int rutaId) {
        ListaEnlazadaDoble<Salida> salidas = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaSalidas.getCabeza();

        while (actual != null) {
            if (((Salida) actual.getDato()).getRutaId() == rutaId) {
                salidas.agregarAlFinal((Salida) actual.getDato());
            }
            actual = actual.getSiguiente();
        }

        return salidas;
    }

    /**
     *  BUSCAR prnximas salidas (disponibles)
     */
    public ListaEnlazadaDoble<Salida> buscarProximas() {
        ListaEnlazadaDoble<Salida> proximas = new ListaEnlazadaDoble<>();
        Date ahora = new Date();
        ListaEnlazadaDoble.Nodo actual = listaSalidas.getCabeza();

        while (actual != null) {
            Salida salida = (Salida) actual.getDato();
            if (salida.isDisponible() && salida.getFechaHoraSalida().after(ahora)) {
                proximas.agregarAlFinal(salida);
            }
            actual = actual.getSiguiente();
        }

        return proximas;
    }

    /**
     *  ACTUALIZAR salida
     */
    public boolean actualizar(int id, Salida salidaActualizada) {
        if (arbolPorId.buscar(id) == null) {
            System.out.println(" Salida no encontrada: " + id);
            return false;
        }

        arbolPorId.insertar(id, salidaActualizada);

        ListaEnlazadaDoble.Nodo nodo = listaSalidas.getCabeza();
        while (nodo != null) {
            if (((Salida) nodo.getDato()).getIdSalida() == id) {
                // No hay setDato
                break;
            }
            nodo = nodo.getSiguiente();
        }

        System.out.println(" Salida actualizada: " + id);
        return true;
    }

    /**
     *  ELIMINAR salida
     */
    public boolean eliminar(int id) {
        if (arbolPorId.buscar(id) == null) {
            System.out.println(" Salida no encontrada: " + id);
            return false;
        }

        // arbolPorId no tiene metodo eliminar (solo eliminar de lista)

        ListaEnlazadaDoble.Nodo actual = listaSalidas.getCabeza();
        while (actual != null) {
            if (((Salida) actual.getDato()).getIdSalida() == id) {
                listaSalidas.eliminar(actual);
                break;
            }
            actual = actual.getSiguiente();
        }

        System.out.println(" Salida eliminada: " + id);
        return true;
    }

    /**
     *  OBTENER todas
     */
    public ListaEnlazadaDoble<Salida> obtenerTodas() {
        return listaSalidas;
    }

    /**
     *  ESTADnSTICAS
     */
    public int contarTotal() {
        return listaSalidas.getTamao();
    }

    public int contarDisponibles() {
        int contador = 0;
        Date ahora = new Date();
        ListaEnlazadaDoble.Nodo actual = listaSalidas.getCabeza();

        while (actual != null) {
            if (((Salida) actual.getDato()).isDisponible()
                    && ((Salida) actual.getDato()).getFechaHoraSalida().after(ahora)) {
                contador++;
            }
            actual = actual.getSiguiente();
        }

        return contador;
    }

    public void limpiar() {
        arbolPorId = new ArbolAVL<>();
        listaSalidas = new ListaEnlazadaDoble<>();
    }

    // ============================================
    //  PERSISTENCIA JSON
    // ============================================

    private static final String RUTA_JSON = "data/salidas.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public void guardarEnJSON() {
        try {
            List<Salida> salidas = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaSalidas.getCabeza();

            while (actual != null) {
                salidas.add((Salida) actual.getDato());
                actual = actual.getSiguiente();
            }

            String json = gson.toJson(salidas);
            Path archivo = Paths.get(RUTA_JSON);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));

            System.out.println(" Guardadas " + salidas.size() + " salidas en " + RUTA_JSON);
        } catch (IOException e) {
            System.out.println(" Error al guardar salidas: " + e.getMessage());
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
            List<Salida> salidas = gson.fromJson(json, new TypeToken<List<Salida>>() {
            }.getType());

            if (salidas != null) {
                limpiar();
                for (Salida s : salidas) {
                    agregar(s);
                }
                System.out.println(" Cargadas " + salidas.size() + " salidas desde " + RUTA_JSON);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar salidas: " + e.getMessage());
        }
    }
}
