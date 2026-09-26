# Pendientes tras la Entrega 2

Lo que ya funciona: lectura de vendedores/productos/ventas, cálculo de totales por
vendedor y de cantidades por producto, y los dos reportes CSV ordenados de forma
descendente (`reporte_vendedores.csv`, `reporte_productos.csv`).

Falta para la Entrega 3:

1. **Manejo de errores de formato**: `util/ValidationUtil` todavía no existe. Hoy,
   una línea de venta con un id de producto inexistente se ignora silenciosamente
   en `VentaProcessor`, pero no se registra ningún log ni se validan cantidades o
   precios negativos.
2. **Al menos un "extra" del enunciado**: aún no se implementó ninguno de los
   puntos (a) múltiples archivos de ventas por vendedor con sufijo (ya soportado
   por `FileUtil`, falta probarlo explícitamente), (b) serialización con
   `Serializable`, o (c) validación con descarte de líneas inválidas.
3. **`conslusion.txt`**: pendiente de escribir con lo aprendido, aplicaciones
   profesionales y dificultades.
4. **Revisión final de Javadoc** en todas las clases nuevas de `io/` y `process/`.
