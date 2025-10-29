# 🔍 REPORTE DE ORGANIZACIÓN - PROBLEMAS DETECTADOS

## ❌ PROBLEMAS CRÍTICOS

### 1. **DUPLICACIÓN DE CÓDIGO**

#### 📂 **Estructura Duplicada:**

```
ferrocarriles/
├── src/
│   ├── com/ferrocarriles/          ← CARPETA PRINCIPAL (USADA)
│   │   ├── estructuras/
│   │   ├── gestor/
│   │   ├── models/
│   │   └── servidor/
│   │
│   └── main/java/com/ferrocarriles/ ← CARPETA DUPLICADA (NO USADA)
│       ├── estructuras/
│       ├── gestor/
│       ├── models/
│       ├── graph/
│       ├── controller/
│       └── FerrocarrilesApplication.java
```

#### 📄 **Archivos Duplicados:**

**Models (13 archivos × 2 = 26 archivos):**

- ✅ `src/com/ferrocarriles/models/` (USADOS en compilación)
- ❌ `src/main/java/com/ferrocarriles/models/` (DUPLICADOS, no usados)
  - Administrador.java
  - Asiento.java
  - Boleto.java
  - Equipaje.java
  - Estacion.java
  - Operador.java
  - Pasajero.java
  - PersonaContacto.java
  - Ruta.java
  - Salida.java
  - Tren.java
  - Usuario.java
  - Vagon.java

**Estructuras de Datos (6 archivos × 2 = 12 archivos):**

- ✅ `src/com/ferrocarriles/estructuras/` (USADOS)
- ❌ `src/main/java/com/ferrocarriles/estructuras/` (DUPLICADOS)
  - ArbolAVL.java
  - Cola.java
  - Grafo.java
  - ListaEnlazadaDoble.java
  - Pila.java
  - TablaHash.java

**ServidorHTTP.java (2 copias):**

- ✅ `src/com/ferrocarriles/servidor/ServidorHTTP.java` (USADO)
- ❌ `ServidorHTTP.java` (en raíz, DUPLICADO)

**GestorEstaciones.java (2 copias):**

- ✅ `src/com/ferrocarriles/gestor/GestorEstaciones.java` (USADO)
- ❌ `src/main/java/com/ferrocarriles/gestor/GestorEstaciones.java` (DUPLICADO)

### 2. **ARCHIVOS EN LUGARES INCORRECTOS**

```
ferrocarriles/
├── ServidorHTTP.java          ← ❌ NO debería estar en raíz
├── src/main/                  ← ❌ Carpeta completa NO se usa
│   ├── java/
│   ├── resources/             ← ⚠️ application.properties, banner.txt
│   └── webapp/                ← ⚠️ HTML files aquí también
└── web/                       ← ⚠️ Otro directorio con HTML
    ├── dashboard-admin.html
    └── css/
```

### 3. **CARPETAS INNECESARIAS**

- `src/main/` - Estructura de Maven/Spring Boot NO USADA
- `target/` - Carpeta de build de Maven NO USADA
- `src/main/webapp/` - HTML files duplicados

---

## ✅ ESTRUCTURA CORRECTA (LA QUE SE USA)

```
ferrocarriles/
├── bin/                           ← Archivos compilados (.class)
│   └── com/ferrocarriles/
│       ├── estructuras/
│       ├── gestor/
│       ├── models/
│       └── servidor/
│
├── data/                          ← Archivos JSON de persistencia
│   ├── boletos.json
│   ├── equipaje.json
│   ├── estaciones.json
│   ├── rutas.json
│   ├── salidas.json
│   ├── trenes.json
│   ├── usuarios.json
│   └── vagones.json
│
├── lib/                           ← Bibliotecas externas
│   └── gson-2.10.1.jar
│
├── src/                           ← CÓDIGO FUENTE
│   └── com/ferrocarriles/
│       ├── estructuras/           ← 6 estructuras de datos
│       │   ├── ArbolAVL.java
│       │   ├── Cola.java
│       │   ├── Grafo.java
│       │   ├── ListaEnlazadaDoble.java
│       │   ├── Pila.java
│       │   └── TablaHash.java
│       │
│       ├── gestor/                ← 8 gestores de negocio
│       │   ├── GestorBoletos.java
│       │   ├── GestorEquipaje.java
│       │   ├── GestorEstaciones.java
│       │   ├── GestorRutas.java
│       │   ├── GestorSalidas.java
│       │   ├── GestorTrenes.java
│       │   ├── GestorUsuarios.java
│       │   └── GestorVagones.java
│       │
│       ├── models/                ← 13 modelos de datos
│       │   ├── Administrador.java
│       │   ├── Asiento.java
│       │   ├── Boleto.java
│       │   ├── Equipaje.java
│       │   ├── Estacion.java
│       │   ├── Operador.java
│       │   ├── Pasajero.java
│       │   ├── PersonaContacto.java
│       │   ├── Ruta.java
│       │   ├── Salida.java
│       │   ├── Tren.java
│       │   ├── Usuario.java
│       │   └── Vagon.java
│       │
│       └── servidor/              ← Servidor HTTP
│           └── ServidorHTTP.java
│
├── web/                           ← Archivos HTML/CSS/JS (si se usan)
│   ├── dashboard-admin.html
│   ├── dashboard-operador.html
│   ├── dashboard-pasajero.html
│   ├── index.html
│   └── css/
│       └── style.css
│
├── compilar.bat                   ← Script de compilación
├── ejecutar.bat                   ← Script de ejecución
└── limpiar.ps1                    ← Script de limpieza
```

---

## 🎯 PLAN DE LIMPIEZA RECOMENDADO

### **Paso 1: ELIMINAR Carpetas/Archivos Duplicados**

```powershell
# Eliminar carpeta main completa (NO se usa)
Remove-Item -Recurse -Force "src\main"

# Eliminar ServidorHTTP.java de la raíz
Remove-Item -Force "ServidorHTTP.java"

# Eliminar carpeta target (Maven, no se usa)
Remove-Item -Recurse -Force "target" -ErrorAction SilentlyContinue
```

### **Paso 2: Organizar Archivos Web**

- Decidir si usar `web/` o `src/main/webapp/`
- Recomendación: **Mantener `web/`** (más simple)
- Eliminar `src/main/webapp/` y `src/main/resources/`

### **Paso 3: Verificar Compilación**

```powershell
.\compilar.bat
```

### **Paso 4: Actualizar .gitignore**

```
# Archivos compilados
bin/
*.class

# Archivos temporales
*.log
*.tmp

# Configuración del IDE
.vscode/
.idea/
*.iml
```

---

## 📊 ESTADÍSTICAS

| Categoría   | Archivos Usados | Archivos Duplicados | Total  |
| ----------- | --------------- | ------------------- | ------ |
| Models      | 13              | 13                  | 26     |
| Estructuras | 6               | 6                   | 12     |
| Gestores    | 8               | 1                   | 9      |
| Servidor    | 1               | 1                   | 2      |
| **TOTAL**   | **28**          | **21**              | **49** |

**Duplicación: 43% del código está duplicado** ⚠️

---

## ✅ ESTRUCTURA FINAL LIMPIA

```
ferrocarriles/
├── bin/                  (generado automáticamente)
├── data/                 (8 archivos JSON)
├── lib/                  (gson-2.10.1.jar)
├── src/
│   └── com/ferrocarriles/
│       ├── estructuras/  (6 archivos)
│       ├── gestor/       (8 archivos)
│       ├── models/       (13 archivos)
│       └── servidor/     (1 archivo)
├── web/                  (HTML/CSS para UI)
├── compilar.bat
├── ejecutar.bat
└── limpiar.ps1
```

**Total archivos fuente:** 28 archivos Java
**Líneas de código:** ~4,500 líneas
**Tamaño:** ~150 KB código fuente

---

## 🚨 ACCIÓN REQUERIDA

1. **Respaldar el proyecto** (por si acaso)
2. **Ejecutar limpieza** con los comandos del Paso 1
3. **Recompilar** para verificar que todo funciona
4. **Confirmar** que el servidor inicia correctamente
