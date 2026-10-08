package com.poli.ventas.process;

import com.poli.ventas.io.VentaFileReader;
import com.poli.ventas.model.DetalleVenta;
import com.poli.ventas.model.Producto;
import com.poli.ventas.model.Vendedor;
import com.poli.ventas.util.ValidationUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contiene la lógica de negocio para calcular y ordenar totales de venta por
 * vendedor y por producto, a partir de los archivos de ventas ya leídos.
 */
public class VentaProcessor {

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private VentaProcessor() {
    }

    /**
     * Calcula el total recaudado por cada vendedor, sumando cantidad * precio unitario
     * de cada línea de venta válida en sus archivos de ventas. Los totales de varios
     * archivos del mismo vendedor se acumulan. Un archivo con encabezado ausente o inválido
     * se registra y se omite; las líneas inválidas también se descartan.
     *
     * @param vendedores     vendedores registrados; no se usa en el cálculo y se conserva por compatibilidad de la firma.
     * @param productos      mapa de productos disponibles, con el id de producto como llave, para obtener precios.
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @return mapa con el número de documento del vendedor (como texto) como llave y el total recaudado como valor.
     * @throws IOException si el cuerpo de algún archivo de ventas no puede leerse.
     */
    public static Map<String, Double> calcularTotalPorVendedor(List<Vendedor> vendedores,
                                                                 Map<String, Producto> productos,
                                                                 List<File> archivosVentas) throws IOException {
        Map<String, Double> totales = new LinkedHashMap<>();
        for (File archivoVenta : archivosVentas) {
            String numeroDocumento;
            try {
                numeroDocumento = VentaFileReader.extraerEncabezado(archivoVenta)[1];
            } catch (IOException e) {
                ValidationUtil.logFormatoErroneo(archivoVenta.getName(), e.getMessage());
                continue;
            }

            double totalArchivo = 0.0;
            for (DetalleVenta detalle : VentaFileReader.leer(archivoVenta, productos)) {
                Producto producto = productos.get(detalle.getIdProducto());
                totalArchivo += producto.getPrecioPorUnidad() * detalle.getCantidadVendida();
            }

            totales.merge(numeroDocumento, totalArchivo, Double::sum);
        }
        return totales;
    }

    /**
     * Calcula la cantidad total vendida de cada producto, sumando las cantidades de
     * todas las líneas de venta de todos los archivos, sin validar la existencia de los
     * productos. Equivale a {@link #calcularCantidadPorProducto(List, Map)} con catálogo {@code null}.
     *
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @return mapa con el id de producto como llave y la cantidad total vendida como valor.
     * @throws IOException si algún archivo de ventas no puede leerse.
     */
    public static Map<String, Integer> calcularCantidadPorProducto(List<File> archivosVentas) throws IOException {
        return calcularCantidadPorProducto(archivosVentas, null);
    }

    /**
     * Calcula la cantidad total vendida de cada producto descartando las líneas
     * inválidas (cantidad no positiva, formato incorrecto o producto inexistente).
     *
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @param productos      catálogo de productos con el id como llave; si es {@code null} no se valida el id.
     * @return mapa con el id de producto como llave y la cantidad total vendida como valor.
     * @throws IOException si algún archivo de ventas no puede leerse.
     */
    public static Map<String, Integer> calcularCantidadPorProducto(List<File> archivosVentas,
                                                                     Map<String, Producto> productos) throws IOException {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (File archivoVenta : archivosVentas) {
            for (DetalleVenta detalle : VentaFileReader.leer(archivoVenta, productos)) {
                cantidades.merge(detalle.getIdProducto(), detalle.getCantidadVendida(), Integer::sum);
            }
        }
        return cantidades;
    }

    /**
     * Ordena los totales por vendedor de mayor a menor.
     *
     * @param totales mapa de totales por número de documento, como los produce {@link #calcularTotalPorVendedor}.
     * @return lista de entradas ordenadas de forma descendente por total recaudado.
     */
    public static List<Map.Entry<String, Double>> ordenarVendedoresPorTotal(Map<String, Double> totales) {
        List<Map.Entry<String, Double>> entradas = new ArrayList<>(totales.entrySet());
        entradas.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return entradas;
    }

    /**
     * Ordena las cantidades vendidas por producto de mayor a menor.
     *
     * @param cantidades mapa de cantidades por id de producto, como lo produce {@link #calcularCantidadPorProducto}.
     * @return lista de entradas ordenadas de forma descendente por cantidad vendida.
     */
    public static List<Map.Entry<String, Integer>> ordenarProductosPorCantidad(Map<String, Integer> cantidades) {
        List<Map.Entry<String, Integer>> entradas = new ArrayList<>(cantidades.entrySet());
        entradas.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        return entradas;
    }
}
