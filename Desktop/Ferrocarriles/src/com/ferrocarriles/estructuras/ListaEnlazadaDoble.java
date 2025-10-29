package com.ferrocarriles.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 * Lista Enlazada Doble - Estructura de datos fundamental
 * Complejidad:
 * - Insertar al inicio/final: O(1)
 * - Eliminar nodo con referencia: O(1)
 * - Buscar: O(n)
 * 
 * @param <T> Tipo de dato a almacenar
 */
public class ListaEnlazadaDoble<T> implements Iterable<T> {

    /**
     * Nodo de la lista enlazada doble
     */
    public class Nodo {
        T dato;
        Nodo siguiente;
        Nodo anterior;

        public Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
            this.anterior = null;
        }

        public T getDato() {
            return dato;
        }

        public void setDato(T dato) {
            this.dato = dato;
        }

        public Nodo getSiguiente() {
            return siguiente;
        }

        public Nodo getAnterior() {
            return anterior;
        }
    }

    private Nodo cabeza;
    private Nodo cola;
    private int tamao;

    public ListaEnlazadaDoble() {
        this.cabeza = null;
        this.cola = null;
        this.tamao = 0;
    }

    /**
     * Agregar elemento al final - O(1)
     */
    public Nodo agregarAlFinal(T dato) {
        Nodo nuevoNodo = new Nodo(dato);

        if (cabeza == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            nuevoNodo.anterior = cola;
            cola.siguiente = nuevoNodo;
            cola = nuevoNodo;
        }

        tamao++;
        return nuevoNodo;
    }

    /**
     * Agregar elemento al inicio - O(1)
     */
    public Nodo agregarAlInicio(T dato) {
        Nodo nuevoNodo = new Nodo(dato);

        if (cabeza == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            nuevoNodo.siguiente = cabeza;
            cabeza.anterior = nuevoNodo;
            cabeza = nuevoNodo;
        }

        tamao++;
        return nuevoNodo;
    }

    /**
     * Eliminar nodo especfico - O(1) si tienes la referencia
     */
    public T eliminar(Nodo nodo) {
        if (nodo == null)
            return null;

        if (nodo.anterior != null) {
            nodo.anterior.siguiente = nodo.siguiente;
        } else {
            cabeza = nodo.siguiente;
        }

        if (nodo.siguiente != null) {
            nodo.siguiente.anterior = nodo.anterior;
        } else {
            cola = nodo.anterior;
        }

        tamao--;
        return nodo.dato;
    }

    /**
     * Obtener elemento en posicin especfica - O(n)
     */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamao) {
            throw new IndexOutOfBoundsException("ndice fuera de rango: " + indice);
        }

        Nodo actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }

        return actual.dato;
    }

    /**
     * Verificar si la lista est vaca
     */
    public boolean estaVacia() {
        return tamao == 0;
    }

    /**
     * Obtener tamao de la lista
     */
    public int getTamao() {
        return tamao;
    }

    /**
     * Obtener primer nodo (cabeza)
     */
    public Nodo getCabeza() {
        return cabeza;
    }

    /**
     * Obtener ltimo nodo (cola)
     */
    public Nodo getCola() {
        return cola;
    }

    /**
     * Limpiar la lista
     */
    public void limpiar() {
        cabeza = null;
        cola = null;
        tamao = 0;
    }

    /**
     * Iterador para recorrer hacia adelante
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    /**
     * Iterador para recorrer hacia atrs
     */
    public Iterator<T> iteradorReverso() {
        return new Iterator<T>() {
            private Nodo actual = cola;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.anterior;
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo actual = cabeza;

        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(" <-> ");
            }
            actual = actual.siguiente;
        }

        sb.append("]");
        return sb.toString();
    }
}
