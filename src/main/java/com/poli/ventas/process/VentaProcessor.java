package com.poli.ventas.process;

import com.poli.ventas.io.VentaFileReader;
import com.poli.ventas.model.DetalleVenta;
import com.poli.ventas.model.Producto;
import com.poli.ventas.model.Vendedor;

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
     * de cada línea de venta en sus archivos de ventas.
     *
     * @param vendedores     lista de vendedores registrados (usada solo para validar que existan).
     * @param productos      mapa de productos disponibles, con el id de producto como llave.
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @return mapa con el número de documento del vendedor (como texto) como llave y el total recaudado como valor.
     * @throws IOException si algún archivo de ventas no puede leerse.
     */
    public static Map<String, Double> calcularTotalPorVendedor(List<Vendedor> vendedores,
                                                                 Map<String, Producto> productos,
                                                                 List<File> archivosVentas) throws IOException {
        Map<String, Double> totales = new LinkedHashMap<>();
        for (File archivoVenta : archivosVentas) {
            String[] encabezado = VentaFileReader.extraerEncabezado(archivoVenta);
            String numeroDocumento = encabezado[1];

            double totalArchivo = 0.0;
            for (DetalleVenta detalle : VentaFileReader.leer(archivoVenta)) {
                Producto producto = productos.get(detalle.getIdProducto());
                if (producto == null) {
                    continue;
                }
                totalArchivo += producto.getPrecioPorUnidad() * detalle.getCantidadVendida();
            }

            totales.merge(numeroDocumento, totalArchivo, Double::sum);
        }
        return totales;
    }

    /**
     * Calcula la cantidad total vendida de cada producto, sumando las cantidades de
     * todas las líneas de venta de todos los archivos.
     *
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @return mapa con el id de producto como llave y la cantidad total vendida como valor.
     * @throws IOException si algún archivo de ventas no puede leerse.
     */
    public static Map<String, Integer> calcularCantidadPorProducto(List<File> archivosVentas) throws IOException {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (File archivoVenta : archivosVentas) {
            for (DetalleVenta detalle : VentaFileReader.leer(archivoVenta)) {
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
