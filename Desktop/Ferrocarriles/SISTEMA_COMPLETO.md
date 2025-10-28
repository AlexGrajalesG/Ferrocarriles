# 🚂 SISTEMA FERROVIARIO - GESTIÓN COMPLETA

## ✅ SISTEMA COMPLETADO

### 📦 **GESTORES CREADOS (7 módulos)**

Cada gestor maneja una entidad con estructuras de datos específicas:

| Gestor          | Archivo               | Estructuras             | Funciones Principales              |
| --------------- | --------------------- | ----------------------- | ---------------------------------- |
| **👥 Usuarios** | `GestorUsuarios.java` | TablaHash, Lista        | Login, Registro, Buscar            |
| **🚂 Trenes**   | `GestorTrenes.java`   | ArbolAVL, Lista         | CRUD, Buscar activos               |
| **🚃 Vagones**  | `GestorVagones.java`  | ArbolAVL, Lista         | CRUD, Buscar disponibles           |
| **🎫 Boletos**  | `GestorBoletos.java`  | Cola, TablaHash, Lista  | Vender, Confirmar, Buscar          |
| **🧳 Equipaje** | `GestorEquipaje.java` | Pila, TablaHash, Lista  | Registrar, Revisar, Validar        |
| **🛤️ Rutas**    | `GestorRutas.java`    | Grafo, TablaHash, Lista | CRUD, Dijkstra, Calcular distancia |
| **🕐 Salidas**  | `GestorSalidas.java`  | ArbolAVL, Lista         | CRUD, Buscar próximas              |

---

## 🎯 **ROLES Y PERMISOS**

### **🔴 ADMINISTRADOR**

- ✅ Gestionar Trenes (crear, editar, eliminar)
- ✅ Gestionar Vagones por Tren
- ✅ Gestionar Rutas (crear, editar, eliminar, publicar)
- ✅ Gestionar Estaciones
- ✅ Gestionar Empleados (Operadores)
- ✅ Administrar Horarios (Salidas)

### **🟡 OPERADOR**

- ✅ Confirmar Boletos (procesar cola de venta)
- ✅ Buscar Boletos por ID
- ✅ Consultar Horarios
- ✅ Gestionar Equipaje (revisar pila)
- ✅ Completar Viaje

### **🟢 PASAJERO**

- ✅ Comprar Boletos
- ✅ Ver Mis Boletos
- ✅ Agregar Equipaje
- ✅ Ver Ruta (visualización de grafo)

---

## 📊 **ESTRUCTURAS DE DATOS USADAS**

### **Cada módulo usa estructuras específicas:**

1. **TablaHash** (O(1) búsqueda)

   - Usuarios por userName
   - Boletos por ID
   - Equipaje por ID
   - Rutas por ID

2. **ArbolAVL** (O(log n) búsqueda ordenada)

   - Trenes por ID
   - Vagones por ID
   - Salidas por ID

3. **ListaEnlazadaDoble** (O(n) recorrido)

   - Todos los módulos: mantener orden de inserción
   - Historial completo

4. **Cola** (FIFO - First In, First Out)

   - Boletos: fila de venta

5. **Pila** (LIFO - Last In, First Out)

   - Equipaje: último en llegar, primero en revisar

6. **Grafo** (Dijkstra para ruta óptima)
   - Rutas: red de estaciones conectadas

---

## 💾 **ARCHIVOS JSON**

Cada entidad se guarda en su propio archivo:

```
data/
├── usuarios.json       👥 Admins, Operadores, Pasajeros
├── trenes.json         🚂 Trenes registrados
├── vagones.json        🚃 Vagones por tren
├── boletos.json        🎫 Boletos vendidos
├── equipaje.json       🧳 Equipaje registrado
├── rutas.json          🛤️  Rutas entre estaciones
├── salidas.json        🕐 Horarios de salida
├── estaciones.json     📍 Estaciones
└── asientos.json       💺 Asientos por vagón
```

---

## 🔄 **FLUJOS PRINCIPALES**

### **1️⃣ LOGIN**

```
Usuario → ingresar userName/password
→ GestorUsuarios.login()
→ TablaHash busca O(1)
→ Redirigir a dashboard según rol
```

### **2️⃣ VENTA DE BOLETO (Operador)**

```
Pasajero solicita boleto
→ GestorBoletos.vender()
→ Encola en Cola
→ Operador confirma
→ GestorBoletos.confirmar()
→ Desencola y guarda en TablaHash + Lista
→ JSON actualizado
```

### **3️⃣ REGISTRO EQUIPAJE (Pasajero/Operador)**

```
Pasajero trae equipaje
→ GestorEquipaje.registrar()
→ Valida peso
→ Apila en Pila
→ Operador revisa
→ GestorEquipaje.revisar()
→ Desapila y guarda
→ JSON actualizado
```

### **4️⃣ CALCULAR RUTA ÓPTIMA (Pasajero)**

```
Pasajero elige origen/destino
→ GestorRutas.calcularRutaOptima()
→ Ejecuta Dijkstra en Grafo
→ Retorna lista de estaciones
→ Calcula distancia total
→ Muestra en dashboard
```

---

## 🚀 **PRÓXIMOS PASOS**

### **FASE 2: PERSISTENCIA JSON** ✅ SIGUIENTE

Crear módulo de carga/guardado para cada gestor

### **FASE 3: SERVIDOR HTTP**

Integrar todos los gestores en ServidorHTTP.java

### **FASE 4: DASHBOARDS**

Generar HTML dinámico para cada rol

---

## 🛠️ **CÓMO COMPILAR**

```batch
cd c:\Users\Usuario\Desktop\ferrocarriles
.\compilar.bat
```

Compila:

1. Models (Estacion, Usuario, Tren, etc.)
2. Estructuras (ArbolAVL, Cola, Pila, etc.)
3. Gestores (todos los 7)
4. Servidor HTTP

---

## 📝 **ESTADO ACTUAL**

✅ **COMPLETADO:**

- [x] 7 Gestores creados
- [x] Todas las estructuras de datos integradas
- [x] Modelos de datos (Usuario, Tren, Boleto, etc.)
- [x] Lógica de negocio implementada

⏳ **PENDIENTE:**

- [ ] Módulo de persistencia JSON
- [ ] ServidorHTTP integrado
- [ ] Dashboards HTML
- [ ] Sistema de sesión simple

---

## 📞 **CONTACTO**

Sistema desarrollado para demostrar:

- ✅ Estructuras de datos personalizadas
- ✅ POO (Programación Orientada a Objetos)
- ✅ Arquitectura MVC
- ✅ Sin frameworks externos (solo Java + Gson)

---

---

# 📚 MARCO CONCEPTUAL DETALLADO

Este apartado describe la arquitectura, el diseño y las decisiones técnicas del sistema, alineando cada aspecto con el marco conceptual propuesto y justificando las elecciones de estructuras y tecnologías.

---

## 1. Arquitectura General

- **Backend:** Java, orientado a objetos, modularizado en paquetes por dominio (modelos, gestor, servidor, estructuras).
- **Frontend:** HTML/CSS, formularios interactivos, visualización de datos y acciones de usuario.
- **Persistencia:** Archivos JSON por entidad, gestionados con la librería Gson.

---

## 2. Estructuras de Datos y Modelado

### 2.1. HashMap (TablaHash)

- Usado para indexar usuarios por email y boletos por ID.
- Permite búsquedas y autenticación en O(1).
- Ejemplo: `TablaHash<Integer, Boleto>` para acceso rápido a boletos.

### 2.2. Listas

- Colecciones ordenadas para trenes, estaciones, rutas, boletos y vagones.
- Facilitan iteraciones, serialización y operaciones dinámicas.
- Ejemplo: `ListaEnlazadaDoble<Ruta>` para rutas.

### 2.3. Colas

- Modelan procesos FIFO como la venta/validación de boletos y simulación de flujo en estaciones.
- Ejemplo: `Cola<SolicitudBoleto>` para gestionar solicitudes de compra.

### 2.4. Árboles

- BST/AVL para búsquedas por rango, fechas o apellidos.
- Mejoran eficiencia en consultas secundarias.
- Ejemplo: `ArbolAVL<Usuario>` para búsquedas ordenadas.

---

## 3. Persistencia y Formato

- **Archivos JSON independientes por entidad:**
  - `boletos.json`, `trenes.json`, `estaciones.json`, etc.
  - Facilita depuración, portabilidad y uso de Gson.
- **Formato RFC 8259:**
  - Cumple con la especificación y recomendaciones de interoperabilidad.

---

## 4. Modelo de Rutas y Grafo

- **Grafo dirigido y ponderado:**
  - Vértices: estaciones.
  - Aristas: trayectos con peso igual a la distancia en km.
- **Matriz de adyacencia:**
  - Consultas O(1) para existencia y distancia de trayectos.
  - Serialización directa a JSON.

---

## 5. Algoritmo de Rutas Óptimas

- **Dijkstra:**
  - Calcula la ruta más corta entre estaciones.
  - Utiliza la matriz de adyacencia y heap de prioridad.
  - Justificado por la naturaleza dirigida/ponderada del problema.

---

## 6. Gestión de Boletos y Vagones

- **Listas de vagones por tren:**
  - Cada tren tiene una lista de vagones.
  - Cada vagón registra pasajeros por categoría de boleto.
- **Validación de equipaje:**
  - Verifica el peso y rechaza la compra si excede el límite.
- **Asignación de asientos:**
  - Respeta la capacidad y categoría del boleto.

---

## 7. Validación y Seguridad

- **Control de duplicidad y credenciales:**
  - Previene duplicidad de usuarios y boletos.
  - Valida integridad de datos.
- **Contraseñas con hash:**
  - Almacenamiento seguro y confidencialidad básica.
- **Validaciones internas:**
  - Coherencia y consistencia en todas las operaciones.

---

## 8. Justificación Tecnológica

- **Java backend:**
  - Orientación a objetos, colecciones optimizadas, integración con Gson, modularidad y escalabilidad.
- **HTML/CSS frontend:**
  - Formularios interactivos y visualización clara de datos.

---

## 9. Flujo de Usuario y Operaciones Críticas

- **Login y autenticación:**
  - Validación por email y contraseña (hash).
- **Compra de boletos:**
  - Selección de origen, destino, clase, equipaje y cantidad.
  - Cálculo de ruta óptima y precio real con Dijkstra.
  - Validación de equipaje y capacidad.
- **Visualización de boletos:**
  - Dashboard muestra historial, rutas, precios y detalles.
- **Administración de rutas, trenes y estaciones:**
  - CRUD completo desde el backend y persistencia en JSON.

---

## 10. Diagrama Conceptual (descripción)

- **Usuarios** → [HashMap] → Autenticación y acceso.
- **Boletos** → [HashMap/Listas] → Gestión y visualización.
- **Rutas** → [Grafo/Matriz] → Cálculo de trayectos y planificación.
- **Trenes/Vagones** → [Listas] → Asignación y control de capacidad.
- **Equipaje** → [Validación] → Control de peso y restricciones.

---

## 11. Referencias

- Oracle. (n.d.). Java Collections Framework.
- Bray, T. (2017). The JavaScript Object Notation (JSON) Data Interchange Format (RFC 8259).
- baeldung. (2024). Graph Data Structures in Java.
- Tuychiev, S. (2024). Dijkstra’s Algorithm in Java.

---

## 12. Observaciones y Extensibilidad

- El sistema está preparado para escalar agregando nuevas entidades, reglas de negocio o integraciones externas.
- La modularidad y el uso de estructuras óptimas garantizan mantenibilidad y facilidad de extensión.

---

**Este documento puede ser ajustado y ampliado según los cambios futuros en el sistema o los requisitos académicos.**
