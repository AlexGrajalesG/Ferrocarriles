package com.ferrocarriles.estructuras;

import java.util.*;

/**
 * Grafo no dirigido con pesos
 * Ideal para: Rutas entre estaciones, redes
 * 
 * Implementa algoritmo de Dijkstra para ruta ms corta
 * Complejidad Dijkstra: O((V + E) log V) con heap
 * 
 * @param <T> Tipo de dato del vrtice
 */
public class Grafo<T> {

    /**
     * Clase interna para representar un vrt ice
     */
    private class Vertice {
        T dato;
        Map<T, Integer> adyacentes; // destino -> peso

        Vertice(T dato) {
            this.dato = dato;
            this.adyacentes = new HashMap<>();
        }
    }

    /**
     * Clase para resultado de Dijkstra
     */
    public static class RutaDijkstra<T> {
        private List<T> camino;
        private int distancia;
        private boolean existe;

        public RutaDijkstra(List<T> camino, int distancia, boolean existe) {
            this.camino = camino;
            this.distancia = distancia;
            this.existe = existe;
        }

        public List<T> getCamino() {
            return camino;
        }

        public int getDistancia() {
            return distancia;
        }

        public boolean existe() {
            return existe;
        }

        @Override
        public String toString() {
            if (!existe) {
                return "No existe ruta";
            }
            return String.format("Ruta: %s, Distancia: %d km", camino, distancia);
        }
    }

    private Map<T, Vertice> vertices;

    public Grafo() {
        this.vertices = new HashMap<>();
    }

    /**
     * Agregar vrtice al grafo
     */
    public void agregarVertice(T dato) {
        if (!vertices.containsKey(dato)) {
            vertices.put(dato, new Vertice(dato));
        }
    }

    /**
     * Agregar arista con peso (ruta entre estaciones)
     */
    public void agregarArista(T origen, T destino, int peso) {
        // Asegurar que ambos vrtices existen
        agregarVertice(origen);
        agregarVertice(destino);

        // Grafo no dirigido: agregar en ambas direcciones
        vertices.get(origen).adyacentes.put(destino, peso);
        vertices.get(destino).adyacentes.put(origen, peso);
    }

    /**
     * Obtener vecinos de un vrtice
     */
    public Map<T, Integer> obtenerVecinos(T dato) {
        Vertice vertice = vertices.get(dato);
        if (vertice == null) {
            return new HashMap<>();
        }
        return new HashMap<>(vertice.adyacentes);
    }

    /**
     * Algoritmo de Dijkstra - Ruta ms corta
     * Complejidad: O((V + E) log V)
     */
    public RutaDijkstra<T> dijkstra(T origen, T destino) {
        if (!vertices.containsKey(origen) || !vertices.containsKey(destino)) {
            return new RutaDijkstra<>(new ArrayList<>(), Integer.MAX_VALUE, false);
        }

        // Distancias desde el origen
        Map<T, Integer> distancias = new HashMap<>();
        // Nodos anteriores para reconstruir camino
        Map<T, T> anteriores = new HashMap<>();
        // Nodos visitados
        Set<T> visitados = new HashSet<>();
        // Cola de prioridad para elegir el nodo con menor distancia
        PriorityQueue<NodoDijkstra> cola = new PriorityQueue<>();

        // Inicializar distancias
        for (T vertice : vertices.keySet()) {
            distancias.put(vertice, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);

        cola.offer(new NodoDijkstra(origen, 0));

        while (!cola.isEmpty()) {
            NodoDijkstra actual = cola.poll();
            T verticeActual = actual.vertice;

            // Si ya fue visitado, continuar
            if (visitados.contains(verticeActual)) {
                continue;
            }

            visitados.add(verticeActual);

            // Si llegamos al destino, podemos terminar
            if (verticeActual.equals(destino)) {
                break;
            }

            // Explorar vecinos
            Vertice v = vertices.get(verticeActual);
            for (Map.Entry<T, Integer> vecino : v.adyacentes.entrySet()) {
                T vecinoId = vecino.getKey();
                int peso = vecino.getValue();

                if (!visitados.contains(vecinoId)) {
                    int nuevaDistancia = distancias.get(verticeActual) + peso;

                    if (nuevaDistancia < distancias.get(vecinoId)) {
                        distancias.put(vecinoId, nuevaDistancia);
                        anteriores.put(vecinoId, verticeActual);
                        cola.offer(new NodoDijkstra(vecinoId, nuevaDistancia));
                    }
                }
            }
        }

        // Reconstruir camino
        List<T> camino = new ArrayList<>();
        T actual = destino;

        while (actual != null) {
            camino.add(0, actual);
            actual = anteriores.get(actual);
        }

        int distanciaFinal = distancias.get(destino);
        boolean existe = distanciaFinal != Integer.MAX_VALUE;

        return new RutaDijkstra<>(camino, distanciaFinal, existe);
    }

    /**
     * Clase interna para nodos en cola de prioridad de Dijkstra
     */
    private class NodoDijkstra implements Comparable<NodoDijkstra> {
        T vertice;
        int distancia;

        NodoDijkstra(T vertice, int distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDijkstra otro) {
            return Integer.compare(this.distancia, otro.distancia);
        }
    }

    /**
     * BFS - Bsqueda en anchura
     * Complejidad: O(V + E)
     */
    public List<T> bfs(T inicio) {
        if (!vertices.containsKey(inicio)) {
            return new ArrayList<>();
        }

        List<T> orden = new ArrayList<>();
        Set<T> visitados = new HashSet<>();
        Queue<T> cola = new LinkedList<>();

        cola.offer(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            T actual = cola.poll();
            orden.add(actual);

            Vertice v = vertices.get(actual);
            for (T vecino : v.adyacentes.keySet()) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.offer(vecino);
                }
            }
        }

        return orden;
    }

    /**
     * DFS - Bsqueda en profundidad
     * Complejidad: O(V + E)
     */
    public List<T> dfs(T inicio) {
        if (!vertices.containsKey(inicio)) {
            return new ArrayList<>();
        }

        List<T> orden = new ArrayList<>();
        Set<T> visitados = new HashSet<>();
        dfsRecursivo(inicio, visitados, orden);
        return orden;
    }

    private void dfsRecursivo(T vertice, Set<T> visitados, List<T> orden) {
        visitados.add(vertice);
        orden.add(vertice);

        Vertice v = vertices.get(vertice);
        for (T vecino : v.adyacentes.keySet()) {
            if (!visitados.contains(vecino)) {
                dfsRecursivo(vecino, visitados, orden);
            }
        }
    }

    /**
     * Obtener nmero de vrtices
     */
    public int numeroVertices() {
        return vertices.size();
    }

    /**
     * Verificar si el grafo est vaco
     */
    public boolean estaVacio() {
        return vertices.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo[vertices=").append(numeroVertices()).append("]\n");

        for (Vertice v : vertices.values()) {
            sb.append(v.dato).append(" -> ");
            sb.append(v.adyacentes);
            sb.append("\n");
        }

        return sb.toString();
    }
}
