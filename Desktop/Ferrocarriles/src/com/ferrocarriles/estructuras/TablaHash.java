package com.ferrocarriles.estructuras;

import java.util.*;

/**
 * Tabla Hash (HashMap personalizado)
 * Bsqueda O(1) promedio
 * 
 * Usa encadenamiento (chaining) para manejar colisiones
 * 
 * Complejidad:
 * - Insertar: O(1) promedio
 * - Buscar: O(1) promedio
 * - Eliminar: O(1) promedio
 * 
 * @param <K> Tipo de clave
 * @param <V> Tipo de valor
 */
public class TablaHash<K, V> {

    /**
     * Entrada de la tabla hash
     */
    private static class Entrada<K, V> {
        K clave;
        V valor;

        Entrada(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private LinkedList<Entrada<K, V>>[] tabla;
    private int tamao;
    private int capacidad;
    private static final double FACTOR_CARGA = 0.75;

    /**
     * Constructor con capacidad inicial
     */
    @SuppressWarnings("unchecked")
    public TablaHash(int capacidadInicial) {
        this.capacidad = capacidadInicial;
        this.tabla = (LinkedList<Entrada<K, V>>[]) new LinkedList[capacidad];
        this.tamao = 0;
    }

    /**
     * Constructor con capacidad por defecto (16)
     */
    public TablaHash() {
        this(16);
    }

    /**
     * Funcin hash
     */
    private int hash(K clave) {
        if (clave == null) {
            return 0;
        }
        return Math.abs(clave.hashCode() % capacidad);
    }

    /**
     * Insertar o actualizar - O(1) promedio
     */
    public void insertar(K clave, V valor) {
        int indice = hash(clave);

        // Crear bucket si no existe
        if (tabla[indice] == null) {
            tabla[indice] = new LinkedList<>();
        }

        // Buscar si la clave ya existe
        for (Entrada<K, V> entrada : tabla[indice]) {
            if (entrada.clave.equals(clave)) {
                // Actualizar valor existente
                entrada.valor = valor;
                return;
            }
        }

        // Agregar nueva entrada
        tabla[indice].add(new Entrada<>(clave, valor));
        tamao++;

        // Verificar factor de carga y redimensionar si es necesario
        if ((double) tamao / capacidad > FACTOR_CARGA) {
            redimensionar();
        }
    }

    /**
     * Buscar valor por clave - O(1) promedio
     */
    public V buscar(K clave) {
        int indice = hash(clave);

        if (tabla[indice] == null) {
            return null;
        }

        for (Entrada<K, V> entrada : tabla[indice]) {
            if (entrada.clave.equals(clave)) {
                return entrada.valor;
            }
        }

        return null;
    }

    /**
     * Eliminar entrada por clave - O(1) promedio
     */
    public boolean eliminar(K clave) {
        int indice = hash(clave);

        if (tabla[indice] == null) {
            return false;
        }

        Iterator<Entrada<K, V>> iterator = tabla[indice].iterator();
        while (iterator.hasNext()) {
            Entrada<K, V> entrada = iterator.next();
            if (entrada.clave.equals(clave)) {
                iterator.remove();
                tamao--;
                return true;
            }
        }

        return false;
    }

    /**
     * Verificar si contiene una clave
     */
    public boolean contiene(K clave) {
        return buscar(clave) != null;
    }

    /**
     * Obtener tamao
     */
    public int getTamao() {
        return tamao;
    }

    /**
     * Verificar si est vaca
     */
    public boolean estaVacia() {
        return tamao == 0;
    }

    /**
     * Obtener todas las claves
     */
    public Set<K> obtenerClaves() {
        Set<K> claves = new HashSet<>();
        for (LinkedList<Entrada<K, V>> bucket : tabla) {
            if (bucket != null) {
                for (Entrada<K, V> entrada : bucket) {
                    claves.add(entrada.clave);
                }
            }
        }
        return claves;
    }

    /**
     * Obtener todos los valores
     */
    public List<V> obtenerValores() {
        List<V> valores = new ArrayList<>();
        for (LinkedList<Entrada<K, V>> bucket : tabla) {
            if (bucket != null) {
                for (Entrada<K, V> entrada : bucket) {
                    valores.add(entrada.valor);
                }
            }
        }
        return valores;
    }

    /**
     * Redimensionar la tabla cuando el factor de carga es alto
     */
    @SuppressWarnings("unchecked")
    private void redimensionar() {
        int nuevaCapacidad = capacidad * 2;
        LinkedList<Entrada<K, V>>[] tablaVieja = tabla;

        // Crear nueva tabla
        tabla = (LinkedList<Entrada<K, V>>[]) new LinkedList[nuevaCapacidad];
        capacidad = nuevaCapacidad;
        tamao = 0;

        // Reinsertar todas las entradas
        for (LinkedList<Entrada<K, V>> bucket : tablaVieja) {
            if (bucket != null) {
                for (Entrada<K, V> entrada : bucket) {
                    insertar(entrada.clave, entrada.valor);
                }
            }
        }
    }

    /**
     * Limpiar la tabla
     */
    @SuppressWarnings("unchecked")
    public void limpiar() {
        tabla = (LinkedList<Entrada<K, V>>[]) new LinkedList[capacidad];
        tamao = 0;
    }

    /**
     * Obtener estadsticas de la tabla
     */
    public String obtenerEstadisticas() {
        int bucketsUsados = 0;
        int colisionesMaximas = 0;

        for (LinkedList<Entrada<K, V>> bucket : tabla) {
            if (bucket != null && !bucket.isEmpty()) {
                bucketsUsados++;
                colisionesMaximas = Math.max(colisionesMaximas, bucket.size());
            }
        }

        return String.format(
                "Estadsticas: tamao=%d, capacidad=%d, bucketsUsados=%d, factorCarga=%.2f, colisionesMax=%d",
                tamao, capacidad, bucketsUsados, (double) tamao / capacidad, colisionesMaximas);
    }

    @Override
    public String toString() {
        return String.format("TablaHash[tamao=%d, capacidad=%d]", tamao, capacidad);
    }
}
