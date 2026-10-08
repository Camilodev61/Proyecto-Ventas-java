package com.poli.ventas.process;

import com.poli.ventas.io.ProductoFileReader;
import com.poli.ventas.io.ReportWriter;
import com.poli.ventas.io.VendedorFileReader;
import com.poli.ventas.model.Producto;
import com.poli.ventas.model.Vendedor;
import com.poli.ventas.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Segunda clase con {@code main} del proyecto: orquesta la lectura de vendedores,
 * productos y archivos de ventas, calcula los totales y genera los reportes finales.
 * Nombrada en minúscula ({@code main}) porque así lo exige el enunciado del proyecto.
 */
public class main {

    private static final String CARPETA_DATOS = "data";
    private static final String RUTA_VENDEDORES = CARPETA_DATOS + "/vendedores.txt";
    private static final String RUTA_PRODUCTOS = CARPETA_DATOS + "/productos.txt";
    private static final String RUTA_REPORTE_VENDEDORES = CARPETA_DATOS + "/reporte_vendedores.csv";
    private static final String RUTA_REPORTE_PRODUCTOS = CARPETA_DATOS + "/reporte_productos.csv";

    /**
     * Orquesta el flujo completo de procesamiento: lee vendedores, productos y
     * archivos de ventas desde {@code data/}, calcula los totales por vendedor y
     * por producto, y escribe los reportes CSV correspondientes. Cualquier error
     * durante el proceso se reporta por consola sin detener la JVM abruptamente.
     *
     * @param args argumentos de línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        try {
            FileUtil.crearCarpetaSiNoExiste(CARPETA_DATOS);

            List<Vendedor> vendedores = VendedorFileReader.leer(RUTA_VENDEDORES);
            Map<String, Producto> productos = ProductoFileReader.leer(RUTA_PRODUCTOS);
            List<File> archivosVentas = FileUtil.listarArchivosVentas(CARPETA_DATOS);

            generarReporteVendedores(vendedores, productos, archivosVentas);
            generarReporteProductos(productos, archivosVentas);

            System.out.println("Procesamiento completado exitosamente. Reportes generados en '" + CARPETA_DATOS + "'.");
        } catch (Exception e) {
            handleProcessError(e);
        }
    }

    /**
     * Calcula el total recaudado por cada vendedor a partir de sus archivos de
     * ventas y escribe el reporte {@code reporte_vendedores.csv} ordenado de mayor
     * a menor total. Los totales sin un vendedor conocido en {@code vendedores.txt}
     * se descartan del reporte.
     *
     * @param vendedores     vendedores registrados, usados para completar nombre y documento en cada fila.
     * @param productos      mapa de productos disponibles, con el id de producto como llave, usado para calcular el total.
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @throws Exception si algún archivo de ventas no puede leerse o el reporte no puede escribirse.
     */
    private static void generarReporteVendedores(List<Vendedor> vendedores,
                                                  Map<String, Producto> productos,
                                                  List<File> archivosVentas) throws Exception {
        Map<String, Vendedor> vendedoresPorDocumento = new HashMap<>();
        for (Vendedor vendedor : vendedores) {
            vendedoresPorDocumento.put(String.valueOf(vendedor.getNumeroDocumento()), vendedor);
        }

        Map<String, Double> totales = VentaProcessor.calcularTotalPorVendedor(vendedores, productos, archivosVentas);
        List<Map.Entry<String, Double>> totalesOrdenados = VentaProcessor.ordenarVendedoresPorTotal(totales);

        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<String, Double> entrada : totalesOrdenados) {
            String numeroDocumento = entrada.getKey();
            Vendedor vendedor = vendedoresPorDocumento.get(numeroDocumento);
            if (vendedor == null) {
                continue;
            }
            filas.add(new String[]{
                    vendedor.getTipoDocumento(),
                    numeroDocumento,
                    vendedor.getNombres(),
                    vendedor.getApellidos(),
                    String.format(Locale.US, "%.2f", entrada.getValue())
            });
        }

        ReportWriter.escribirReporteVendedores(filas, RUTA_REPORTE_VENDEDORES);
    }

    /**
     * Calcula la cantidad total vendida de cada producto a partir de los archivos
     * de ventas y escribe el reporte {@code reporte_productos.csv} ordenado de
     * mayor a menor cantidad vendida. Las líneas con productos inexistentes en
     * {@code productos.txt} ya se descartan al leer; el uso del id como nombre es solo
     * una salvaguarda.
     *
     * @param productos      mapa de productos disponibles, con el id de producto como llave.
     * @param archivosVentas archivos de ventas a procesar (uno o varios por vendedor).
     * @throws Exception si algún archivo de ventas no puede leerse o el reporte no puede escribirse.
     */
    private static void generarReporteProductos(Map<String, Producto> productos,
                                                 List<File> archivosVentas) throws Exception {
        Map<String, Integer> cantidades = VentaProcessor.calcularCantidadPorProducto(archivosVentas, productos);
        List<Map.Entry<String, Integer>> cantidadesOrdenadas = VentaProcessor.ordenarProductosPorCantidad(cantidades);

        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : cantidadesOrdenadas) {
            String idProducto = entrada.getKey();
            Producto producto = productos.get(idProducto);
            String nombre = producto != null ? producto.getNombre() : idProducto;
            filas.add(new String[]{idProducto, nombre, String.valueOf(entrada.getValue())});
        }

        ReportWriter.escribirReporteProductos(filas, RUTA_REPORTE_PRODUCTOS);
    }

    /**
     * Maneja de forma controlada cualquier error ocurrido durante el procesamiento de archivos,
     * con un mensaje específico: archivo de entrada faltante (sugiere ejecutar
     * {@code GenerateInfoFiles}), error de lectura/escritura, o error inesperado.
     *
     * @param e la excepción capturada.
     */
    private static void handleProcessError(Exception e) {
        if (e instanceof FileNotFoundException) {
            System.err.println("Error: no se encontró un archivo de entrada (" + e.getMessage()
                    + "). Ejecute primero GenerateInfoFiles para crear los datos en '" + CARPETA_DATOS + "'.");
        } else if (e instanceof IOException) {
            System.err.println("Error de lectura/escritura procesando las ventas: " + e.getMessage());
        } else {
            System.err.println("Error inesperado procesando las ventas (" + e.getClass().getSimpleName()
                    + "): " + e.getMessage());
        }
    }
}
