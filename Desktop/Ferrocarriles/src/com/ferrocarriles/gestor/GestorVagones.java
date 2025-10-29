package com.ferrocarriles.gestor;

import com.ferrocarriles.models.VagonPasajeros;
import com.ferrocarriles.models.VagonEquipaje;
import com.ferrocarriles.models.Asiento;
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

public class GestorVagones {
    private ArbolAVL<Integer, VagonPasajeros> arbolPasajeros;
    private ArbolAVL<Integer, VagonEquipaje> arbolEquipaje;
    private ListaEnlazadaDoble<VagonPasajeros> listaPasajeros;
    private ListaEnlazadaDoble<VagonEquipaje> listaEquipaje;

    public GestorVagones() {
        this.arbolPasajeros = new ArbolAVL<>();
        this.arbolEquipaje = new ArbolAVL<>();
        this.listaPasajeros = new ListaEnlazadaDoble<>();
        this.listaEquipaje = new ListaEnlazadaDoble<>();
    }

    // Crear un vagón de pasajeros y generar los 40 asientos con la distribución
    // correcta
    public VagonPasajeros crearVagonPasajeros(int idVagon) {
        VagonPasajeros vagon = new VagonPasajeros(idVagon);
        agregarVagonPasajeros(vagon);
        guardarAsientosDeVagon(vagon);
        return vagon;
    }

    // Crear un vagón de equipaje
    public VagonEquipaje crearVagonEquipaje(int idVagon) {
        VagonEquipaje vagon = new VagonEquipaje(idVagon);
        agregarVagonEquipaje(vagon);
        return vagon;
    }

    // ...existing code...

    // Guardar los asientos de un vagón de pasajeros en asientos.json
    private void guardarAsientosDeVagon(VagonPasajeros vagon) {
        try {
            List<Asiento> asientos = vagon.getAsientos();
            Path archivo = Paths.get("data/asientos.json");
            List<Asiento> todos = new ArrayList<>();
            if (Files.exists(archivo)) {
                String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
                todos = new Gson().fromJson(json, new com.google.gson.reflect.TypeToken<List<Asiento>>() {
                }.getType());
            }
            int ultimoId = todos.stream().mapToInt(a -> a.getIdAsiento()).max().orElse(0);
            for (int i = 0; i < asientos.size(); i++) {
                Asiento a = asientos.get(i);
                a.setIdVagon(vagon.getIdVagon());
                a.setIdAsiento(ultimoId + i + 1);
                a.setNumeroAsiento("A" + (i + 1));
            }
            todos.addAll(asientos);
            String jsonFinal = new GsonBuilder().setPrettyPrinting().create().toJson(todos);
            Files.write(archivo, jsonFinal.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Error guardando asientos: " + e.getMessage());
        }
    }

    // Métodos públicos para uso externo
    public ListaEnlazadaDoble<VagonPasajeros> obtenerTodosPasajeros() {
        return listaPasajeros;
    }

    public ListaEnlazadaDoble<VagonEquipaje> obtenerTodosEquipaje() {
        return listaEquipaje;
    }

    public void agregarVagonPasajeros(VagonPasajeros vagon) {
        arbolPasajeros.insertar(vagon.getIdVagon(), vagon);
        listaPasajeros.agregarAlFinal(vagon);
    }

    public void agregarVagonEquipaje(VagonEquipaje vagon) {
        arbolEquipaje.insertar(vagon.getIdVagon(), vagon);
        listaEquipaje.agregarAlFinal(vagon);
    }

    public boolean eliminarVagonPasajeros(int id) {
        if (arbolPasajeros.buscar(id) == null)
            return false;
        ListaEnlazadaDoble.Nodo actual = listaPasajeros.getCabeza();
        while (actual != null) {
            if (((VagonPasajeros) actual.getDato()).getIdVagon() == id) {
                listaPasajeros.eliminar(actual);
                break;
            }
            actual = actual.getSiguiente();
        }
        arbolPasajeros.eliminar(id);
        return true;
    }

    public boolean eliminarVagonEquipaje(int id) {
        if (arbolEquipaje.buscar(id) == null)
            return false;
        ListaEnlazadaDoble.Nodo actual = listaEquipaje.getCabeza();
        while (actual != null) {
            if (((VagonEquipaje) actual.getDato()).getIdVagon() == id) {
                listaEquipaje.eliminar(actual);
                break;
            }
            actual = actual.getSiguiente();
        }
        arbolEquipaje.eliminar(id);
        return true;
    }

    public ListaEnlazadaDoble<VagonPasajeros> obtenerDisponiblesPasajeros() {
        ListaEnlazadaDoble<VagonPasajeros> disponibles = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaPasajeros.getCabeza();
        while (actual != null) {
            if (((VagonPasajeros) actual.getDato()).isDisponibilidad()) {
                disponibles.agregarAlFinal((VagonPasajeros) actual.getDato());
            }
            actual = actual.getSiguiente();
        }
        return disponibles;
    }

    public ListaEnlazadaDoble<VagonEquipaje> obtenerDisponiblesEquipaje() {
        ListaEnlazadaDoble<VagonEquipaje> disponibles = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaEquipaje.getCabeza();
        while (actual != null) {
            if (((VagonEquipaje) actual.getDato()).isDisponibilidad()) {
                disponibles.agregarAlFinal((VagonEquipaje) actual.getDato());
            }
            actual = actual.getSiguiente();
        }
        return disponibles;
    }

    public int contarTotal() {
        return listaPasajeros.getTamao() + listaEquipaje.getTamao();
    }

    public void limpiar() {
        arbolPasajeros = new ArbolAVL<>();
        arbolEquipaje = new ArbolAVL<>();
        listaPasajeros = new ListaEnlazadaDoble<>();
        listaEquipaje = new ListaEnlazadaDoble<>();
    }

    // Persistencia JSON
    private static final String RUTA_JSON_PASAJEROS = "data/vagones_pasajeros.json";
    private static final String RUTA_JSON_EQUIPAJE = "data/vagones_equipaje.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void guardarPasajerosEnJSON() {
        try {
            List<VagonPasajeros> vagones = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaPasajeros.getCabeza();
            while (actual != null) {
                vagones.add((VagonPasajeros) actual.getDato());
                actual = actual.getSiguiente();
            }
            String json = gson.toJson(vagones);
            Path archivo = Paths.get(RUTA_JSON_PASAJEROS);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));
            System.out.println(" Guardados " + vagones.size() + " vagones pasajeros en " + RUTA_JSON_PASAJEROS);
        } catch (IOException e) {
            System.out.println(" Error al guardar vagones pasajeros: " + e.getMessage());
        }
    }

    public void guardarEquipajeEnJSON() {
        try {
            List<VagonEquipaje> vagones = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaEquipaje.getCabeza();
            while (actual != null) {
                vagones.add((VagonEquipaje) actual.getDato());
                actual = actual.getSiguiente();
            }
            String json = gson.toJson(vagones);
            Path archivo = Paths.get(RUTA_JSON_EQUIPAJE);
            Files.createDirectories(archivo.getParent());
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));
            System.out.println(" Guardados " + vagones.size() + " vagones equipaje en " + RUTA_JSON_EQUIPAJE);
        } catch (IOException e) {
            System.out.println(" Error al guardar vagones equipaje: " + e.getMessage());
        }
    }

    public void cargarPasajerosDesdeJSON() {
        try {
            Path archivo = Paths.get(RUTA_JSON_PASAJEROS);
            if (!Files.exists(archivo)) {
                System.out.println("  Archivo " + RUTA_JSON_PASAJEROS + " no existe. Iniciando con lista vacía.");
                return;
            }
            String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
            List<VagonPasajeros> vagones = gson.fromJson(json, new TypeToken<List<VagonPasajeros>>() {
            }.getType());
            if (vagones != null) {
                listaPasajeros = new ListaEnlazadaDoble<>();
                for (VagonPasajeros v : vagones) {
                    agregarVagonPasajeros(v);
                }
                System.out.println(" Cargados " + vagones.size() + " vagones pasajeros desde " + RUTA_JSON_PASAJEROS);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar vagones pasajeros: " + e.getMessage());
        }
    }

    public void cargarEquipajeDesdeJSON() {
        try {
            Path archivo = Paths.get(RUTA_JSON_EQUIPAJE);
            if (!Files.exists(archivo)) {
                System.out.println("  Archivo " + RUTA_JSON_EQUIPAJE + " no existe. Iniciando con lista vacía.");
                return;
            }
            String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);
            List<VagonEquipaje> vagones = gson.fromJson(json, new TypeToken<List<VagonEquipaje>>() {
            }.getType());
            if (vagones != null) {
                listaEquipaje = new ListaEnlazadaDoble<>();
                for (VagonEquipaje v : vagones) {
                    agregarVagonEquipaje(v);
                }
                System.out.println(" Cargados " + vagones.size() + " vagones equipaje desde " + RUTA_JSON_EQUIPAJE);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar vagones equipaje: " + e.getMessage());
        }
    }
}
