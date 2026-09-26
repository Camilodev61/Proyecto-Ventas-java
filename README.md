# Proyecto Ventas — Generación y Clasificación de Datos

Proyecto en Java (JDK 8) para la materia de Conceptos Fundamentales de Programación.
Genera archivos planos de prueba (vendedores, productos, ventas) y luego los procesa
para producir reportes de vendedores y productos ordenados.

Arquitectura en capas: `model` → `io` → `process` / `generator`. Ver el detalle en
[Spec del Proyecto Generación.md](Spec%20del%20Proyecto%20Generaci%C3%B3n.md).

## Estado actual — Entrega 1 y 2

Implementada la etapa de **generación de datos de prueba**:

- `model/`: `Vendedor`, `Producto`, `DetalleVenta`.
- `generator/RandomDataProvider`: nombres, apellidos, documentos y precios aleatorios.
- `generator/GenerateInfoFiles` (clase con `main`): genera en `data/`
  - `vendedores.txt`
  - `productos.txt`
  - `ventas_<numeroDocumento>.txt` (uno por vendedor)

Implementada la etapa de **lectura y procesamiento de datos**:

- `io/`: `VendedorFileReader`, `ProductoFileReader` y `VentaFileReader` leen los archivos
  planos generados en la Entrega 1; `ReportWriter` escribe los reportes CSV.
- `process/VentaProcessor`: calcula totales por vendedor (`calcularTotalPorVendedor`) y
  cantidades por producto (`calcularCantidadPorProducto`), y los ordena de forma
  descendente (`ordenarVendedoresPorTotal`, `ordenarProductosPorCantidad`).
- `process/main` (segunda clase con `main`): orquesta la lectura, el cálculo y genera en
  `data/`:
  - `reporte_vendedores.csv`
  - `reporte_productos.csv`

Pendiente para la Entrega 3: ver [PENDIENTES.md](PENDIENTES.md) (manejo de errores de
formato con `util/ValidationUtil`, al menos un "extra" del enunciado, `conslusion.txt`
y revisión final de Javadoc).

## Requisitos

- JDK 8 (o superior; el `pom.xml` está configurado para el bytecode de la versión 8).
- Maven (opcional, pero recomendado para compilar desde línea de comandos).

## Cómo correr el proyecto

### Desde IntelliJ

1. Abrir la carpeta del proyecto como proyecto Maven.
2. Configurar el SDK del proyecto en Java 8 (`Project Structure → SDK`).
3. Ejecutar primero `com.poli.ventas.generator.GenerateInfoFiles` (genera los archivos en `data/`).
4. Luego ejecutar `com.poli.ventas.process.main` para leer esos archivos y generar los reportes.

### Desde línea de comandos (con Maven)

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.poli.ventas.generator.GenerateInfoFiles"
mvn exec:java -Dexec.mainClass="com.poli.ventas.process.main"
```

## Estructura del proyecto

```
src/main/java/com/poli/ventas/
├── model/       # Vendedor, Producto, DetalleVenta
├── generator/   # RandomDataProvider, GenerateInfoFiles (main, Entrega 1)
├── io/          # VendedorFileReader, ProductoFileReader, VentaFileReader, ReportWriter (Entrega 2)
├── process/     # VentaProcessor, main (Entrega 2)
└── util/        # FileUtil (ValidationUtil pendiente, ver PENDIENTES.md)
data/            # archivos generados/leídos (no versionados, ver .gitignore)
```

Ver [PENDIENTES.md](PENDIENTES.md) para el detalle de lo que falta antes de la Entrega 3.
