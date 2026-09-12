# Proyecto Ventas — Generación y Clasificación de Datos

Proyecto en Java (JDK 8) para la materia de Conceptos Fundamentales de Programación.
Genera archivos planos de prueba (vendedores, productos, ventas) y luego los procesa
para producir reportes de vendedores y productos ordenados.

Arquitectura en capas: `model` → `io` → `process` / `generator`. Ver el detalle en
[Spec del Proyecto Generación.md](Spec%20del%20Proyecto%20Generaci%C3%B3n.md).

## Estado actual — Entrega 1

Implementada la etapa de **generación de datos de prueba**:

- `model/`: `Vendedor`, `Producto`, `DetalleVenta`.
- `generator/RandomDataProvider`: nombres, apellidos, documentos y precios aleatorios.
- `generator/GenerateInfoFiles` (clase con `main`): genera en `data/`
  - `vendedores.txt`
  - `productos.txt`
  - `ventas_<numeroDocumento>.txt` (uno por vendedor)

Pendiente (próximas entregas): lectura de archivos (`io/`), procesamiento y reportes
(`process/`), validaciones (`util/ValidationUtil`) y la clase `main` de la etapa 2.

## Requisitos

- JDK 8 (o superior; el `pom.xml` está configurado para el bytecode de la versión 8).
- Maven (opcional, pero recomendado para compilar desde línea de comandos).

## Cómo correr el proyecto

### Desde IntelliJ

1. Abrir la carpeta del proyecto como proyecto Maven.
2. Configurar el SDK del proyecto en Java 8 (`Project Structure → SDK`).
3. Ejecutar primero `com.poli.ventas.generator.GenerateInfoFiles` (genera los archivos en `data/`).
4. Más adelante, ejecutar `com.poli.ventas.process.main` (etapa 2) para leer esos archivos y generar los reportes.

### Desde línea de comandos (con Maven)

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.poli.ventas.generator.GenerateInfoFiles"
```

## Estructura del proyecto

```
src/main/java/com/poli/ventas/
├── model/       # Vendedor, Producto, DetalleVenta
├── generator/   # RandomDataProvider, GenerateInfoFiles (main, Entrega 1)
├── io/          # lectura/escritura de archivos planos (Entrega 2)
├── process/     # VentaProcessor, main (Entrega 2 y 3)
└── util/        # FileUtil, ValidationUtil
data/            # archivos generados/leídos (no versionados, ver .gitignore)
```
