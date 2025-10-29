package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Tren;
import com.ferrocarriles.models.Salida;
import com.ferrocarriles.models.VagonPasajeros;
import com.ferrocarriles.models.VagonEquipaje;
import com.ferrocarriles.gestor.GestorVagones;
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
import java.util.List;

public class GestorTrenes {
    private ArbolAVL<Integer, Tren> arbolPorId;
    private ListaEnlazadaDoble<Tren> listaTrenes;

    public GestorTrenes() {
        this.arbolPorId = new ArbolAVL<>();
        this.listaTrenes = new ListaEnlazadaDoble<>();
    }

    // Crear tren y agregarle vagones según reglas de negocio
    public Tren crearTrenConVagones(int idTren, String nombre, String tipo, int capacidadCarga, double kilometraje,
            int cantidadVagonesPasajeros, GestorVagones gestorVagones) {
        int maxVagones = tipo.equalsIgnoreCase("Transiberiano") ? 30 : 34;
        if (cantidadVagonesPasajeros < 1 || cantidadVagonesPasajeros > maxVagones) {
            throw new IllegalArgumentException("Cantidad de vagones de pasajeros fuera de rango para tipo " + tipo);
        }
        Tren tren = new Tren(idTren, nombre, tipo, capacidadCarga, kilometraje, true, 0);
        int idVagon = 1;
        int idVagonEquipaje = 1000;
        // Agregar vagones de pasajeros
        for (int i = 0; i < cantidadVagonesPasajeros; i++) {
            VagonPasajeros vagonP = gestorVagones.crearVagonPasajeros(idVagon++);
            tren.agregarVagonPasajeros(vagonP.getIdVagon());
            // Por cada 2 vagones de pasajeros, agregar 1 de equipaje
            if ((i + 1) % 2 == 0) {
                VagonEquipaje vagonE = gestorVagones.crearVagonEquipaje(idVagonEquipaje++);
                tren.agregarVagonEquipaje(vagonE.getIdVagon());
            }
        }
        // Si es Carabela, asegurar al menos 1 vagón de equipaje
        if (tipo.equalsIgnoreCase("Carabela") && cantidadVagonesPasajeros < 2) {
            VagonEquipaje vagonE = gestorVagones.crearVagonEquipaje(idVagonEquipaje++);
            tren.agregarVagonEquipaje(vagonE.getIdVagon());
        }
        agregar(tren);
        guardarEnJSON();
        return tren;
    }

    public boolean darDeBajaTren(int idTren, GestorSalidas gestorSalidas) {
        Tren tren = arbolPorId.buscar(idTren);
        if (tren == null) {
            System.out.println(" Tren no encontrado: " + idTren);
            return false;
        }
        // Validar que no tenga salidas activas
        ListaEnlazadaDoble<Salida> salidas = gestorSalidas.buscarPorTren(idTren);
        boolean tieneViajesActivos = false;
        ListaEnlazadaDoble.Nodo actual = salidas.getCabeza();
        while (actual != null) {
            Salida salida = (Salida) actual.getDato();
            if (salida.isDisponible()) {
                tieneViajesActivos = true;
                break;
            }
            actual = actual.getSiguiente();
        }
        if (tieneViajesActivos) {
            System.out.println(" No se puede dar de baja el tren " + idTren + ": tiene viajes activos.");
            return false;
        }
        tren.setEstado(false);
        arbolPorId.insertar(idTren, tren);
        System.out.println(" Tren dado de baja: " + idTren);
        return true;
    }

    public boolean agregar(Tren tren) {
        if (arbolPorId.buscar(tren.getIdTren()) != null) {
            System.out.println(" Tren ya existe con ID: " + tren.getIdTren());
            return false;
        }
        arbolPorId.insertar(tren.getIdTren(), tren);
        listaTrenes.agregarAlFinal(tren);
        System.out.println(" Tren agregado: " + tren.getNombre());
        return true;
    }

    public Tren buscarPorId(int id) {
        return arbolPorId.buscar(id);
    }

    public boolean actualizar(int id, Tren trenActualizado) {
        Tren actual = arbolPorId.buscar(id);
        if (actual == null) {
            System.out.println(" Tren no encontrado: " + id);
            return false;
        }
        arbolPorId.insertar(id, trenActualizado);
        ListaEnlazadaDoble.Nodo nodo = listaTrenes.getCabeza();
        while (nodo != null) {
            Tren trenNodo = (Tren) nodo.getDato();
            if (trenNodo.getIdTren() == id) {
                nodo.setDato(trenActualizado);
                break;
            }
            nodo = nodo.getSiguiente();
        }
        System.out.println(" Tren actualizado: " + id);
        return true;
    }

    public boolean eliminar(int id) {
        Tren tren = arbolPorId.buscar(id);
        if (tren == null) {
            System.out.println(" Tren no encontrado: " + id);
            return false;
        }
        ListaEnlazadaDoble.Nodo actual = listaTrenes.getCabeza();
        while (actual != null) {
            if (((Tren) actual.getDato()).getIdTren() == id) {
                listaTrenes.eliminar(actual);
                break;
            }
            actual = actual.getSiguiente();
        }
        System.out.println(" Tren eliminado: " + id);
        return true;
    }

    public ListaEnlazadaDoble<Tren> obtenerTodos() {
        return listaTrenes;
    }

    public ListaEnlazadaDoble<Tren> obtenerActivos() {
        ListaEnlazadaDoble<Tren> activos = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaTrenes.getCabeza();
        while (actual != null) {
            if (((Tren) actual.getDato()).isEstado()) {
                activos.agregarAlFinal((Tren) actual.getDato());
            }
            actual = actual.getSiguiente();
        }
        return activos;
    }

    public int contarTotal() {
        return listaTrenes.getTamao();
    }

    public int contarActivos() {
        int contador = 0;
        ListaEnlazadaDoble.Nodo actual = listaTrenes.getCabeza();
        while (actual != null) {
            if (((Tren) actual.getDato()).isEstado()) {
                contador++;
            }
            actual = actual.getSiguiente();
        }
        return contador;
    }

    public void limpiar() {
        arbolPorId = new ArbolAVL<>();
        listaTrenes = new ListaEnlazadaDoble<>();
    }

    // Persistencia JSON
    private static final String RUTA_JSON = "data/trenes.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void guardarEnJSON() {
        try {
            List<Tren> trenes = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaTrenes.getCabeza();
            while (actual != null) {
                trenes.add((Tren) actual.getDato());
                actual = actual.getSiguiente();
            }
            String json = gson.toJson(trenes);
            Path archivo = Paths.get(RUTA_JSON);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));
            System.out.println(" Guardados " + trenes.size() + " trenes en " + RUTA_JSON);
        } catch (IOException e) {
            System.out.println(" Error al guardar trenes: " + e.getMessage());
        }
    }

    public void cargarDesdeJSON() {
        try {
            Path archivo = Paths.get(RUTA_JSON);
            if (!Files.exists(archivo)) {
                System.out.println("  Archivo " + RUTA_JSON + " no existe. Iniciando con lista vacía.");
                return;
            }
            String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
            List<Tren> trenes = gson.fromJson(json, new TypeToken<List<Tren>>() {
            }.getType());
            if (trenes != null) {
                limpiar();
                for (Tren t : trenes) {
                    agregar(t);
                }
                System.out.println(" Cargados " + trenes.size() + " trenes desde " + RUTA_JSON);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar trenes: " + e.getMessage());
        }
    }
}