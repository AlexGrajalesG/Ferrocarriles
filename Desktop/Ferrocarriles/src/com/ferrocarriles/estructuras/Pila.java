package com.ferrocarriles.estructuras;

import java.util.EmptyStackException;

/**
 * Pila (Stack) - LIFO (Last In, First Out)
 * Ideal para: Deshacer/Rehacer operaciones
 * 
 * Complejidad:
 * - Push: O(1)
 * - Pop: O(1)
 * - Peek: O(1)
 * 
 * @param <T> Tipo de dato a almacenar
 */
public class Pila<T> {
    
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;
        
        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
    
    private Nodo<T> tope;
    private int tamao;
    private final int capacidadMaxima;
    
    /**
     * Constructor con capacidad mxima
     */
    public Pila(int capacidadMaxima) {
        this.tope = null;
        this.tamao = 0;
        this.capacidadMaxima = capacidadMaxima;
    }
    
    /**
     * Constructor con capacidad por defecto (100)
     */
    public Pila() {
        this(100);
    }
    
    /**
     * Agregar elemento al tope - O(1)
     */
    public void push(T dato) {
        // Si se alcanza la capacidad mxima, eliminar el elemento ms antiguo
        if (tamao >= capacidadMaxima) {
            eliminarBase();
        }
        
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        nuevoNodo.siguiente = tope;
        tope = nuevoNodo;
        tamao++;
    }
    
    /**
     * Eliminar y retornar elemento del tope - O(1)
     */
    public T pop() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        
        T dato = tope.dato;
        tope = tope.siguiente;
        tamao--;
        return dato;
    }
    
    /**
     * Ver elemento del tope sin eliminarlo - O(1)
     */
    public T peek() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        return tope.dato;
    }
    
    /**
     * Verificar si la pila est vaca
     */
    public boolean estaVacia() {
        return tamao == 0;
    }
    
    /**
     * Obtener tamao de la pila
     */
    public int getTamao() {
        return tamao;
    }
    
    /**
     * Limpiar la pila
     */
    public void limpiar() {
        tope = null;
        tamao = 0;
    }
    
    /**
     * Eliminar elemento de la base (para mantener capacidad)
     */
    private void eliminarBase() {
        if (tamao <= 1) {
            limpiar();
            return;
        }
        
        Nodo<T> actual = tope;
        Nodo<T> anterior = null;
        
        // Llegar al penltimo nodo
        while (actual.siguiente != null) {
            anterior = actual;
            actual = actual.siguiente;
        }
        
        if (anterior != null) {
            anterior.siguiente = null;
        }
        tamao--;
    }
    
    @Override
    public String toString() {
        if (estaVacia()) {
            return "Pila[]";
        }
        
        StringBuilder sb = new StringBuilder("Pila[TOPE -> ");
        Nodo<T> actual = tope;
        
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(" -> ");
            }
            actual = actual.siguiente;
        }
        
        sb.append("]");
        return sb.toString();
    }
}
