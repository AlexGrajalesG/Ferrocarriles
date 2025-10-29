package com.ferrocarriles.gestor;

import com.ferrocarriles.models.Usuario;
import com.ferrocarriles.models.Administrador;
import com.ferrocarriles.models.Operador;
import com.ferrocarriles.models.Pasajero;
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
 * GESTOR DE USUARIOS
 * 
 * Estructuras usadas:
 * - TablaHash: Busqueda rapida por userName O(1)
 * - Lista: Mantener orden de registro
 */
public class GestorUsuarios {

    private TablaHash<String, Usuario> tablaPorUserName; // userName -> Usuario
    private ListaEnlazadaDoble<Usuario> listaUsuarios; // Orden de insercion

    public GestorUsuarios() {
        this.tablaPorUserName = new TablaHash<>(100);
        this.listaUsuarios = new ListaEnlazadaDoble<>();
    }

    /**
     * LOGIN - Autenticar usuario
     */
    public Usuario login(String userName, String password) {
        Usuario usuario = tablaPorUserName.buscar(userName);

        if (usuario != null && usuario.login(userName, password)) {
            System.out.println(" Login exitoso: " + userName + " (" + usuario.getRol() + ")");
            return usuario;
        }

        System.out.println(" Login fallido: credenciales incorrectas");
        return null;
    }

    /**
     * AGREGAR usuario (registro)
     */
    public boolean agregar(Usuario usuario) {
        // Verificar si ya existe
        if (tablaPorUserName.buscar(usuario.getUserName()) != null) {
            System.out.println(" Usuario ya existe: " + usuario.getUserName());
            return false;
        }

        // Agregar a estructuras
        tablaPorUserName.insertar(usuario.getUserName(), usuario);
        listaUsuarios.agregarAlFinal(usuario);

        System.out.println(" Usuario registrado: " + usuario.getUserName());
        return true;
    }

    /**
     * BUSCAR por userName
     */
    public Usuario buscarPorUserName(String userName) {
        return tablaPorUserName.buscar(userName);
    }

    /**
     * BUSCAR por ID
     */
    public Usuario buscarPorId(int id) {
        ListaEnlazadaDoble.Nodo actual = listaUsuarios.getCabeza();

        while (actual != null) {
            if (((Usuario) actual.getDato()).getIdUsuario() == id) {
                return (Usuario) actual.getDato();
            }
            actual = actual.getSiguiente();
        }

        return null;
    }

    /**
     * ACTUALIZAR usuario
     */
    public boolean actualizar(String userName, Usuario usuarioActualizado) {
        Usuario actual = tablaPorUserName.buscar(userName);

        if (actual == null) {
            System.out.println(" Usuario no encontrado: " + userName);
            return false;
        }

        // Actualizar en hash
        tablaPorUserName.insertar(userName, usuarioActualizado);

        // Actualizar en lista
        ListaEnlazadaDoble.Nodo nodo = listaUsuarios.getCabeza();
        while (nodo != null) {
            if (((Usuario) nodo.getDato()).getUserName().equals(userName)) {
                // nodo.setDato no existe, se actualiza desde hash
                break;
            }
            nodo = nodo.getSiguiente();
        }

        System.out.println(" Usuario actualizado: " + userName);
        return true;
    }

    /**
     * ELIMINAR usuario
     */
    public boolean eliminar(String userName) {
        Usuario usuario = tablaPorUserName.buscar(userName);

        if (usuario == null) {
            System.out.println(" Usuario no encontrado: " + userName);
            return false;
        }

        // Eliminar de hash
        tablaPorUserName.eliminar(userName);

        // Eliminar de lista
        ListaEnlazadaDoble.Nodo actual = listaUsuarios.getCabeza();
        while (actual != null) {
            if (((Usuario) actual.getDato()).getUserName().equals(userName)) {
                listaUsuarios.eliminar(actual);
                break;
            }
            actual = actual.getSiguiente();
        }

        System.out.println(" Usuario eliminado: " + userName);
        return true;
    }

    /**
     * OBTENER todos los usuarios
     */
    public ListaEnlazadaDoble<Usuario> obtenerTodos() {
        return listaUsuarios;
    }

    /**
     * OBTENER solo Operadores
     */
    public ListaEnlazadaDoble<Usuario> obtenerOperadores() {
        ListaEnlazadaDoble<Usuario> operadores = new ListaEnlazadaDoble<>();
        ListaEnlazadaDoble.Nodo actual = listaUsuarios.getCabeza();

        while (actual != null) {
            if (actual.getDato() instanceof Operador) {
                operadores.agregarAlFinal((Usuario) actual.getDato());
            }
            actual = actual.getSiguiente();
        }

        return operadores;
    }

    /**
     * ESTADISTICAS
     */
    public int contarTotal() {
        return listaUsuarios.getTamao();
    }

    public int contarPorRol(String rol) {
        int contador = 0;
        ListaEnlazadaDoble.Nodo actual = listaUsuarios.getCabeza();

        while (actual != null) {
            if (((Usuario) actual.getDato()).getRol().equals(rol)) {
                contador++;
            }
            actual = actual.getSiguiente();
        }

        return contador;
    }

    /**
     * LIMPIAR todo
     */
    public void limpiar() {
        tablaPorUserName = new TablaHash<>(100);
        listaUsuarios = new ListaEnlazadaDoble<>();
    }

    // ============================================
    // PERSISTENCIA JSON
    // ============================================

    private static final String RUTA_JSON = "data/usuarios.json";
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * GUARDAR en JSON
     */
    public void guardarEnJSON() {
        try {
            // Convertir lista enlazada a ArrayList para serializar
            List<Usuario> usuarios = new ArrayList<>();
            ListaEnlazadaDoble.Nodo actual = listaUsuarios.getCabeza();

            while (actual != null) {
                usuarios.add((Usuario) actual.getDato());
                actual = actual.getSiguiente();
            }

            // Serializar a JSON
            String json = gson.toJson(usuarios);

            // Crear directorio si no existe
            Path archivo = Paths.get(RUTA_JSON);
            Files.createDirectories(archivo.getParent());

            // Escribir archivo
            Files.write(archivo, json.getBytes(StandardCharsets.UTF_8));

            System.out.println(" Guardados " + usuarios.size() + " usuarios en " + RUTA_JSON);

        } catch (IOException e) {
            System.out.println(" Error al guardar usuarios: " + e.getMessage());
        }
    }

    /**
     * CARGAR desde JSON
     */
    public void cargarDesdeJSON() {
        try {
            Path archivo = Paths.get(RUTA_JSON);

            if (!Files.exists(archivo)) {
                System.out.println("  Archivo " + RUTA_JSON + " no existe. Iniciando con lista vacna.");
                return;
            }

            // Leer archivo
            String json = new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8);

            // Deserializar JSON manualmente para instanciar la subclase correcta
            List<java.util.Map<String, Object>> usuariosRaw = gson.fromJson(json,
                    new TypeToken<List<java.util.Map<String, Object>>>() {
                    }.getType());
            List<Usuario> usuarios = new ArrayList<>();
            for (java.util.Map<String, Object> u : usuariosRaw) {
                String tipo = (String) u.get("tipoUsuario");
                int idUsuario = ((Double) u.get("idUsuario")).intValue();
                String nombre = (String) u.get("nombre");
                String email = (String) u.get("email");
                String passwordHash = (String) u.get("passwordHash");
                String userName = (String) u.get("userName");
                boolean activo = (Boolean) u.get("activo");
                Usuario usuario = null;
                if ("Administrador".equalsIgnoreCase(tipo)) {
                    usuario = new Administrador(idUsuario, nombre, email, passwordHash, userName, activo, null);
                } else if ("Operador".equalsIgnoreCase(tipo)) {
                    usuario = new Operador(idUsuario, nombre, email, passwordHash, userName, activo);
                } else if ("Pasajero".equalsIgnoreCase(tipo)) {
                    usuario = new Pasajero(idUsuario, nombre, email, passwordHash, userName, activo, null);
                }
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
            if (!usuarios.isEmpty()) {
                limpiar();
                for (Usuario u : usuarios) {
                    agregar(u);
                }
                System.out.println(" Cargados " + usuarios.size() + " usuarios desde " + RUTA_JSON);
            }
        } catch (IOException e) {
            System.out.println(" Error al cargar usuarios: " + e.getMessage());
        }
    }
}
