# Spec del Proyecto — Generación y Clasificación de Datos (Java, IntelliJ)

## Arquitectura propuesta: Arquitectura en Capas (Layered Architecture)

Para este proyecto propongo una **arquitectura en capas simple**, organizada por responsabilidad, en lugar de patrones más complejos como MVC, Hexagonal/Clean Architecture o microservicios. Las capas son:

```
┌─────────────────────────────────────────────┐
│  Capa de Entrada/Orquestación (process/,     │
│  generator/)  → las dos clases con main      │
├─────────────────────────────────────────────┤
│  Capa de Lógica de Negocio (process/)        │
│  → VentaProcessor: cálculos y ordenamientos  │
├─────────────────────────────────────────────┤
│  Capa de Acceso a Datos (io/)                │
│  → lectura y escritura de archivos planos    │
├─────────────────────────────────────────────┤
│  Capa de Modelo (model/)                     │
│  → Vendedor, Producto, DetalleVenta          │
├─────────────────────────────────────────────┤
│  Capa Transversal (util/)                    │
│  → validaciones, utilidades de archivos      │
└─────────────────────────────────────────────┘
```

### ¿Por qué esta arquitectura y no otra?

1. **El problema es naturalmente secuencial y por lotes (batch), no interactivo.**
   No hay UI, no hay peticiones concurrentes, no hay estado que persista entre ejecuciones más allá de archivos. Un patrón como MVC (pensado para separar vista/controlador/modelo en apps interactivas) no aporta nada aquí porque no existe "vista". Una arquitectura en capas refleja exactamente el flujo real del programa: **leer → procesar → escribir**.

2. **Separación de responsabilidades sin sobre-ingeniería.**
   Cada capa tiene una única razón para cambiar (principio de responsabilidad única):
   - Si cambia el *formato* de los archivos → solo tocas `io/`.
   - Si cambia la *lógica de cálculo* (ej. cómo se define "el mejor vendedor") → solo tocas `process/`.
   - Si cambia la forma de *generar datos de prueba* → solo tocas `generator/`.
   - El `model/` no depende de nada más, así que es estable y reutilizable en las dos clases `main`.

3. **Cumple el requisito de "dos main independientes" de forma limpia.**
   El enunciado exige que `GenerateInfoFiles` y `main` sean programas separados que no dependen el uno del otro para compilar. Con capas, ambos `main` simplemente orquestan capas inferiores (`generator+model` uno, `io+process+model` el otro) sin necesidad de que una clase `main` llame a la otra.

4. **Facilita las pruebas y el manejo de errores por capa.**
   Como cada capa está desacoplada, puedes probar `VentaProcessor` con datos ficticios sin tocar archivos reales, o probar `ValidationUtil` de forma aislada — algo que el enunciado pide indirectamente en el punto "extra" de detectar formatos erróneos.

5. **Es el estándar de facto para proyectos Java de este tamaño.**
   Para un programa de una sola ejecución sin base de datos ni red, una arquitectura hexagonal o basada en interfaces/inyección de dependencias (Spring, etc.) sería sobre-ingeniería: agrega complejidad (interfaces, contenedores de IoC) que no se justifica para ~10 clases y una ejecución de consola. La arquitectura en capas da la organización necesaria con la mínima fricción, algo muy valorado en un curso de "Conceptos Fundamentales de Programación".

6. **Escala bien hacia los "extras" del enunciado sin refactorizar.**
   - Múltiples archivos por vendedor → solo cambia `FileUtil` (capa transversal), el resto no se entera.
   - Serialización → se agrega como una variante de `io/` (ej. `VendedorSerializer`), sin tocar `model/` ni `process/`.
   - Validación de datos incoherentes → vive en `util/ValidationUtil` y se invoca desde `io/` al momento de leer, sin ensuciar la lógica de negocio en `process/`.

### Regla de dependencia (importante para la sustentación)

Las flechas de dependencia solo deben ir **hacia abajo**:
`main / GenerateInfoFiles → process/generator → io → model`

`model/` nunca debe importar clases de `io/` o `process/` (evita acoplamiento circular). Esto es justamente lo que un jurado/profesor evaluará como "buenas prácticas de diseño" además del código en sí.

---

## 0. Configuración inicial en IntelliJ

- Crea un proyecto **Java simple** (o Maven, opcional pero recomendado para que cualquiera lo compile sin depender del IDE).
- JDK: usa **Java 8** como pide el enunciado (Project Structure → SDK → 1.8).
- Estructura de carpetas sugerida:

```
proyecto-ventas/
├── data/                      # aquí se generan/leen los archivos planos
├── src/
│   └── com/poli/ventas/
│       ├── model/
│       │   ├── Vendedor.java
│       │   ├── Producto.java
│       │   └── DetalleVenta.java
│       ├── generator/
│       │   ├── GenerateInfoFiles.java   <-- primera clase con main
│       │   └── RandomDataProvider.java
│       ├── io/
│       │   ├── VendedorFileReader.java
│       │   ├── ProductoFileReader.java
│       │   ├── VentaFileReader.java
│       │   └── ReportWriter.java
│       ├── process/
│       │   ├── main.java                <-- segunda clase con main
│       │   └── VentaProcessor.java
│       └── util/
│           ├── FileUtil.java
│           └── ValidationUtil.java
├── conslusion.txt              # se agrega en la entrega 3 (nombre tal cual pide el PDF)
├── README.md
└── .gitignore
```

> Nota: el enunciado exige literalmente dos clases con `main`, llamadas **`GenerateInfoFiles`** y **`main`**. Java permite una clase llamada `main` (no es palabra reservada), solo cuidado con la convención de nombres en mayúscula inicial — aquí se prioriza el requisito del PDF sobre la convención.

---

## 1. Clases del modelo (`model/`)

### `Vendedor.java`
- Atributos: `tipoDocumento`, `numeroDocumento`, `nombres`, `apellidos`
- Métodos: getters/setters, `toString()`

### `Producto.java`
- Atributos: `id`, `nombre`, `precioPorUnidad`
- Métodos: getters/setters, `toString()`

### `DetalleVenta.java`
- Atributos: `idProducto`, `cantidadVendida`
- Métodos: getters/setters, `toString()`

---

## 2. Generador de archivos (`generator/`) — Entrega 1

### `RandomDataProvider.java`
Utilidad de datos pseudoaleatorios (nombres/apellidos reales, ids, precios).
- `static String randomNombre()`
- `static String randomApellido()`
- `static long randomDocumento()`
- `static String randomTipoDocumento()` → ("CC", "CE", "TI")
- `static double randomPrecio()`
- `static int randomCantidad()`

### `GenerateInfoFiles.java` (clase con `main`)
- `public static void main(String[] args)`
- `public static void createSalesMenFile(int randomSalesCount, String name, long id)`
  → genera `data/ventas_<id>.txt` con formato `TipoDoc;NumDoc` + líneas `idProducto;cantidad`
- `public static void createProductsFile(int productsCount)`
  → genera `data/productos.txt`
- `public static void createSalesManInfoFile(int salesmanCount)`
  → genera `data/vendedores.txt`
- `private static void handleGenerationError(Exception e)` → mensaje de error controlado

---

## 3. Lectura de archivos (`io/`) — Entrega 2

### `VendedorFileReader.java`
- `static List<Vendedor> leer(String rutaArchivo)`

### `ProductoFileReader.java`
- `static Map<String, Producto> leer(String rutaArchivo)` (clave = idProducto)

### `VentaFileReader.java`
- `static List<DetalleVenta> leer(File archivoVentaVendedor)`
- `static String[] extraerEncabezado(File archivo)` → tipoDoc y numDoc del vendedor

### `ReportWriter.java`
- `static void escribirReporteVendedores(List<String[]> filas, String rutaSalida)`
- `static void escribirReporteProductos(List<String[]> filas, String rutaSalida)`

---

## 4. Procesamiento (`process/`) — Entrega 2 y 3

### `VentaProcessor.java`
- `static Map<String, Double> calcularTotalPorVendedor(List<Vendedor> vendedores, Map<String, Producto> productos, List<File> archivosVentas)`
- `static Map<String, Integer> calcularCantidadPorProducto(List<File> archivosVentas)`
- `static List<Map.Entry<String, Double>> ordenarVendedoresPorTotal(Map<String, Double> totales)` (desc)
- `static List<Map.Entry<String, Integer>> ordenarProductosPorCantidad(Map<String, Integer> cantidades)` (desc)

### `main.java` (clase con `main`)
- `public static void main(String[] args)`
- Orquesta: leer vendedores → leer productos → leer todas las ventas de `data/` → procesar → generar los 2 reportes CSV.
- `private static void handleProcessError(Exception e)`

---

## 5. Utilidades (`util/`) — usadas transversalmente

### `FileUtil.java`
- `static List<File> listarArchivosVentas(String carpeta)` (filtra por patrón de nombre, ej. `ventas_*.txt`)
- `static void crearCarpetaSiNoExiste(String ruta)`

### `ValidationUtil.java` (para el "extra" c)
- `static boolean idProductoExiste(String id, Map<String, Producto> productos)`
- `static boolean cantidadValida(int cantidad)`
- `static boolean precioValido(double precio)`
- `static void logFormatoErroneo(String archivo, String detalle)`

---

## 6. Plan por entregas

### 📌 Entrega 1 — Semana 3
**Objetivo:** solo la generación de datos de prueba.

Pasos:
1. Crear el proyecto en IntelliJ con la estructura de carpetas de arriba.
2. Implementar `model/` completo (Vendedor, Producto, DetalleVenta).
3. Implementar `RandomDataProvider` con listas reales de nombres/apellidos (puedes usar un array estático de 20-30 nombres colombianos comunes).
4. Implementar `GenerateInfoFiles` con los 3 métodos pedidos exactamente con esas firmas:
   - `createSalesMenFile(int randomSalesCount, String name, long id)`
   - `createProductsFile(int productsCount)`
   - `createSalesManInfoFile(int salesmanCount)`
5. En el `main` de `GenerateInfoFiles`, llamar estos métodos con valores de prueba (ej. generar 5 vendedores, 10 productos, y un archivo de ventas por cada vendedor).
6. Mostrar mensaje de éxito/error (try-catch alrededor de la escritura de archivos).
7. Documentar cada método con Javadoc (`/** ... */`), explicando parámetros y qué genera.
8. Probar que los archivos se generan correctamente en `data/`.
9. Subir a GitHub (ver sección 7).

**Entregable:** repo con `GenerateInfoFiles` funcionando y generando los 3 tipos de archivo.

---

### 📌 Entrega 2 — Semana 5
**Objetivo:** versión preliminar del proyecto completo (puede tener partes faltantes).

Pasos:
1. Implementar `io/` completo: los 3 lectores (`VendedorFileReader`, `ProductoFileReader`, `VentaFileReader`).
2. Implementar `FileUtil.listarArchivosVentas()` para detectar automáticamente todos los archivos de ventas en `data/`.
3. Implementar `VentaProcessor` con al menos el cálculo de totales por vendedor (punto 3 del enunciado).
4. Implementar `main.java` que:
   - Lea vendedores, productos y ventas.
   - Calcule totales por vendedor.
   - Genere el archivo de reporte de vendedores ordenado descendente (usando `ReportWriter`).
5. Si el reporte de productos (punto 4) no alcanza a quedar listo, está bien — es lo esperado en esta entrega.
6. Crear un documento (`.txt` o `.md`) indicando qué falta: ej. "Falta el reporte de productos vendidos, falta manejo de errores de formato, falta serialización".
7. Subir todo a GitHub con ese documento adjunto.

**Entregable:** repo con las dos clases `main` ejecutables, reporte de vendedores funcionando, documento de pendientes.

---

### 📌 Entrega 3 y sustentación — Semanas 7 y 8
**Objetivo:** proyecto completo, documentado y probado.

Pasos:
1. Completar `VentaProcessor.calcularCantidadPorProducto()` y el reporte de productos ordenado descendente.
2. Revisar que ambas clases `main` (`GenerateInfoFiles` y `main`) muestren mensaje de éxito o error claro (usa `try-catch` con mensajes específicos, no genéricos).
3. Repasar Javadoc de **todas** las clases y métodos (estándar Java: `@param`, `@return`, descripción de clase).
4. Revisar nombres de variables y espaciado (nada de `a`, `x1`, `temp2`; usar nombres descriptivos como `totalRecaudadoPorVendedor`).
5. Implementar al menos uno de los "extra" (recomendado empezar por el más simple):
   - **(a) Fácil:** permitir varios archivos de ventas por vendedor (tu `FileUtil` ya lo soporta si nombras `ventas_<id>_1.txt`, `ventas_<id>_2.txt`, etc.)
   - **(c) Medio:** usar `ValidationUtil` para descartar líneas con id de producto inexistente, cantidades o precios negativos, y loguear el error sin tumbar el programa.
   - **(b) Más avanzado:** serializar `Vendedor`/`Producto` con `Serializable` y ofrecer una función alterna de lectura desde `.ser`.
6. Crear `conslusion.txt` (así, con ese nombre exacto que trae el PDF) con:
   1. Lo aprendido durante el desarrollo.
   2. Aplicaciones profesionales de lo aprendido.
   3. Dificultades presentadas.
7. Probar de punta a punta: borrar `data/`, correr `GenerateInfoFiles`, luego correr `main`, verificar que ambos reportes CSV salen bien ordenados.
8. Push final a GitHub, revisar que el README explique cómo correr el proyecto.

**Entregable:** repo final, ambos `main` funcionando sin pedir input, `conslusion.txt`, al menos un extra implementado.

---

## 7. Checklist para subir a GitHub

1. `git init` en la carpeta del proyecto.
2. Crear `.gitignore` con al menos:
   ```
   .idea/
   *.iml
   out/
   target/
   data/*.txt
   ```
   (opcional excluir `data/*.txt` para no ensuciar el repo con archivos generados; el programa los recrea al correr `GenerateInfoFiles`).
3. Agregar un `README.md` explicando:
   - Cómo abrir el proyecto en IntelliJ.
   - Cómo compilar/correr `GenerateInfoFiles` primero y `main` después.
   - Qué JDK usar (8).
4. `git add . && git commit -m "Entrega 1: generación de archivos de prueba"` (y commits similares por entrega).
5. Crear el repo en GitHub (público o con el profesor invitado como colaborador) y hacer `git push`.
6. Compartir el hipervínculo del repositorio — **no hay despliegue ni hosting involucrado**, solo el código fuente accesible.

---

## Resumen rápido de firmas clave (para copiar en tus clases)

```java
// GenerateInfoFiles.java
public static void main(String[] args)
public static void createSalesMenFile(int randomSalesCount, String name, long id)
public static void createProductsFile(int productsCount)
public static void createSalesManInfoFile(int salesmanCount)

// main.java
public static void main(String[] args)

// VentaProcessor.java
static Map<String, Double> calcularTotalPorVendedor(...)
static Map<String, Integer> calcularCantidadPorProducto(...)
```
