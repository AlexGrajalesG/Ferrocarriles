package com.ferrocarriles.estructuras;

/**
 * rbol AVL - rbol Binario de Bsqueda Auto-balanceado
 * Garantiza bsqueda O(log n) SIEMPRE
 * 
 * Complejidad GARANTIZADA:
 * - Insertar: O(log n)
 * - Buscar: O(log n)
 * - Eliminar: O(log n)
 * 
 * @param <K> Tipo de clave (debe ser Comparable)
 * @param <V> Tipo de valor
 */
public class ArbolAVL<K extends Comparable<K>, V> {
    // Método para balancear el árbol después de eliminar
    private Nodo balancear(Nodo nodo) {
        if (nodo == null)
            return null;
        int balance = factorBalance(nodo);
        // Caso Izquierda-Izquierda
        if (balance > 1 && factorBalance(nodo.izquierda) >= 0) {
            return rotarDerecha(nodo);
        }
        // Caso Izquierda-Derecha
        if (balance > 1 && factorBalance(nodo.izquierda) < 0) {
            nodo.izquierda = rotarIzquierda(nodo.izquierda);
            return rotarDerecha(nodo);
        }
        // Caso Derecha-Derecha
        if (balance < -1 && factorBalance(nodo.derecha) <= 0) {
            return rotarIzquierda(nodo);
        }
        // Caso Derecha-Izquierda
        if (balance < -1 && factorBalance(nodo.derecha) > 0) {
            nodo.derecha = rotarDerecha(nodo.derecha);
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    // Método eliminar para compatibilidad con GestorVagones
    public void eliminar(K clave) {
        raiz = eliminarRec(raiz, clave);
        tamao--;
    }

    private Nodo eliminarRec(Nodo nodo, K clave) {
        if (nodo == null)
            return null;
        int cmp = clave.compareTo(nodo.clave);
        if (cmp < 0) {
            nodo.izquierda = eliminarRec(nodo.izquierda, clave);
        } else if (cmp > 0) {
            nodo.derecha = eliminarRec(nodo.derecha, clave);
        } else {
            if (nodo.izquierda == null)
                return nodo.derecha;
            if (nodo.derecha == null)
                return nodo.izquierda;
            Nodo min = nodo.derecha;
            while (min.izquierda != null)
                min = min.izquierda;
            nodo.clave = min.clave;
            nodo.valor = min.valor;
            nodo.derecha = eliminarRec(nodo.derecha, min.clave);
        }
        actualizarAltura(nodo);
        return balancear(nodo);
    }

    private class Nodo {
        K clave;
        V valor;
        Nodo izquierda;
        Nodo derecha;
        int altura;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.izquierda = null;
            this.derecha = null;
            this.altura = 1;
        }
    }

    private Nodo raiz;
    private int tamao;

    public ArbolAVL() {
        this.raiz = null;
        this.tamao = 0;
    }

    /**
     * Obtener altura de un nodo
     */
    private int altura(Nodo nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    /**
     * Obtener factor de balance de un nodo
     */
    private int factorBalance(Nodo nodo) {
        return nodo == null ? 0 : altura(nodo.izquierda) - altura(nodo.derecha);
    }

    /**
     * Actualizar altura de un nodo
     */
    private void actualizarAltura(Nodo nodo) {
        if (nodo != null) {
            nodo.altura = 1 + Math.max(altura(nodo.izquierda), altura(nodo.derecha));
        }
    }

    /**
     * Rotacin a la derecha
     */
    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izquierda;
        Nodo T2 = x.derecha;

        // Realizar rotacin
        x.derecha = y;
        y.izquierda = T2;

        // Actualizar alturas
        actualizarAltura(y);
        actualizarAltura(x);

        return x;
    }

    /**
     * Rotacin a la izquierda
     */
    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.derecha;
        Nodo T2 = y.izquierda;

        // Realizar rotacin
        y.izquierda = x;
        x.derecha = T2;

        // Actualizar alturas
        actualizarAltura(x);
        actualizarAltura(y);

        return y;
    }

    /**
     * Insertar clave-valor - O(log n)
     */
    public void insertar(K clave, V valor) {
        raiz = insertarRecursivo(raiz, clave, valor);
    }

    private Nodo insertarRecursivo(Nodo nodo, K clave, V valor) {
        // 1. Insercin BST normal
        if (nodo == null) {
            tamao++;
            return new Nodo(clave, valor);
        }

        int comparacion = clave.compareTo(nodo.clave);

        if (comparacion < 0) {
            nodo.izquierda = insertarRecursivo(nodo.izquierda, clave, valor);
        } else if (comparacion > 0) {
            nodo.derecha = insertarRecursivo(nodo.derecha, clave, valor);
        } else {
            // Clave duplicada, actualizar valor
            nodo.valor = valor;
            return nodo;
        }

        // 2. Actualizar altura
        actualizarAltura(nodo);

        // 3. Obtener factor de balance
        int balance = factorBalance(nodo);

        // 4. Casos de desbalanceo

        // Caso Izquierda-Izquierda
        if (balance > 1 && clave.compareTo(nodo.izquierda.clave) < 0) {
            return rotarDerecha(nodo);
        }

        // Caso Derecha-Derecha
        if (balance < -1 && clave.compareTo(nodo.derecha.clave) > 0) {
            return rotarIzquierda(nodo);
        }

        // Caso Izquierda-Derecha
        if (balance > 1 && clave.compareTo(nodo.izquierda.clave) > 0) {
            nodo.izquierda = rotarIzquierda(nodo.izquierda);
            return rotarDerecha(nodo);
        }

        // Caso Derecha-Izquierda
        if (balance < -1 && clave.compareTo(nodo.derecha.clave) < 0) {
            nodo.derecha = rotarDerecha(nodo.derecha);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    /**
     * Buscar valor por clave - O(log n)
     */
    public V buscar(K clave) {
        return buscarRecursivo(raiz, clave);
    }

    private V buscarRecursivo(Nodo nodo, K clave) {
        if (nodo == null) {
            return null;
        }

        int comparacion = clave.compareTo(nodo.clave);

        if (comparacion == 0) {
            return nodo.valor;
        } else if (comparacion < 0) {
            return buscarRecursivo(nodo.izquierda, clave);
        } else {
            return buscarRecursivo(nodo.derecha, clave);
        }
    }

    /**
     * Verificar si contiene una clave
     */
    public boolean contiene(K clave) {
        return buscar(clave) != null;
    }

    /**
     * Obtener tamao del rbol
     */
    public int getTamao() {
        return tamao;
    }

    /**
     * Verificar si el rbol est vaco
     */
    public boolean estaVacio() {
        return tamao == 0;
    }

    /**
     * Recorrido InOrder (ordenado ascendente)
     */
    public void inOrder(VisitadorNodo<K, V> visitador) {
        inOrderRecursivo(raiz, visitador);
    }

    private void inOrderRecursivo(Nodo nodo, VisitadorNodo<K, V> visitador) {
        if (nodo != null) {
            inOrderRecursivo(nodo.izquierda, visitador);
            visitador.visitar(nodo.clave, nodo.valor);
            inOrderRecursivo(nodo.derecha, visitador);
        }
    }

    /**
     * Interfaz funcional para visitar nodos
     */
    @FunctionalInterface
    public interface VisitadorNodo<K, V> {
        void visitar(K clave, V valor);
    }

    /**
     * Obtener altura del rbol
     */
    public int getAltura() {
        return altura(raiz);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArbolAVL[tamao=").append(tamao)
                .append(", altura=").append(getAltura())
                .append("]");
        return sb.toString();
    }
}
