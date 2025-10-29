package com.ferrocarriles.estructuras;

import java.util.NoSuchElementException;

/**
 * Cola (Queue) - FIFO (First In, First Out)
 * Ideal para: Procesamiento de tareas, notificaciones
 * 
 * Complejidad:
 * - Encolar: O(1)
 * - Desencolar: O(1)
 * - Frente: O(1)
 * 
 * @param <T> Tipo de dato a almacenar
 */
public class Cola<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamao;

    public Cola() {
        this.frente = null;
        this.fin = null;
        this.tamao = 0;
    }

    /**
     * Agregar elemento al final de la cola - O(1)
     */
    public void encolar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);

        if (estaVacia()) {
            frente = nuevoNodo;
            fin = nuevoNodo;
        } else {
            fin.siguiente = nuevoNodo;
            fin = nuevoNodo;
        }

        tamao++;
    }

    /**
     * Eliminar y retornar elemento del frente - O(1)
     */
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola est vaca");
        }

        T dato = frente.dato;
        frente = frente.siguiente;

        if (frente == null) {
            fin = null;
        }

        tamao--;
        return dato;
    }

    /**
     * Ver elemento del frente sin eliminarlo - O(1)
     */
    public T verFrente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola est vaca");
        }
        return frente.dato;
    }

    /**
     * Verificar si la cola est vaca
     */
    public boolean estaVacia() {
        return tamao == 0;
    }

    /**
     * Obtener tamao de la cola
     */
    public int getTamao() {
        return tamao;
    }

    /**
     * Limpiar la cola
     */
    public void limpiar() {
        frente = null;
        fin = null;
        tamao = 0;
    }

    @Override
    public String toString() {
        if (estaVacia()) {
            return "Cola[]";
        }

        StringBuilder sb = new StringBuilder("Cola[FRENTE -> ");
        Nodo<T> actual = frente;

        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }

        sb.append(" <- FIN]");
        return sb.toString();
    }
}
