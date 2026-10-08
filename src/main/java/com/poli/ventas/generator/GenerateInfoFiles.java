package com.poli.ventas.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada de la etapa de generación de datos de prueba.
 * Crea, dentro de la carpeta {@code data/}, el archivo de vendedores,
 * el archivo de productos y un archivo de ventas por cada vendedor.
 */
public class GenerateInfoFiles {

    private static final String CARPETA_DATOS = "data";

    /** Cantidad de productos generados en la última llamada a {@link #createProductsFile(int)}. */
    private static int cantidadProductosGenerados = 0;

    /** Indica si ocurrió algún error durante la generación, para no anunciar un éxito falso. */
    private static boolean huboErrores = false;

    /**
     * Orquesta la generación de los archivos de prueba: crea la carpeta {@code data/}
     * si no existe, genera el archivo de productos, el archivo de vendedores y, por
     * cada vendedor generado, un archivo de ventas asociado a su número de documento.
     * Además genera un segundo archivo de ventas ({@code ventas_<id>_2.txt}) para el
     * primer vendedor, para ejercitar el soporte de varios archivos por vendedor.
     * Los errores se reportan por consola sin detener la JVM abruptamente; al final
     * se informa éxito solo si no hubo ningún error.
     *
     * @param args argumentos de línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        int cantidadVendedores = 5;
        int cantidadProductos = 10;
        int ventasPorVendedor = 8;

        try {
            crearCarpetaDatos();

            createProductsFile(cantidadProductos);
            createSalesManInfoFile(cantidadVendedores);

            List<Long> documentos = leerDocumentosVendedores();
            for (long idVendedor : documentos) {
                createSalesMenFile(ventasPorVendedor, RandomDataProvider.randomNombre(), idVendedor);
            }

            if (!documentos.isEmpty()) {
                long idConVariosArchivos = documentos.get(0);
                escribirArchivoVentas(ventasPorVendedor, idConVariosArchivos, "ventas_" + idConVariosArchivos + "_2.txt");
            }

            if (huboErrores) {
                System.err.println("La generación terminó con errores; revise los mensajes anteriores.");
            } else {
                System.out.println("Generación de archivos completada exitosamente en la carpeta '" + CARPETA_DATOS + "'.");
            }
        } catch (Exception e) {
            handleGenerationError(e);
        }
    }

    /**
     * Genera el archivo {@code data/ventas_<id>.txt} de un vendedor, con un encabezado
     * {@code tipoDocumento;id} y {@code randomSalesCount} líneas {@code idProducto;cantidad}
     * con producto y cantidad aleatorios.
     *
     * @param randomSalesCount cantidad de líneas de venta a generar en el archivo.
     * @param name             nombre del vendedor; se conserva por la firma exigida por el enunciado
     *                         pero no se usa ni se escribe en el archivo.
     * @param id               número de documento del vendedor, escrito en el encabezado y usado en el nombre del archivo.
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) {
        escribirArchivoVentas(randomSalesCount, id, "ventas_" + id + ".txt");
    }

    /**
     * Escribe un archivo de ventas en {@code data/} con el nombre indicado. Permite generar
     * varios archivos de ventas para un mismo vendedor (ej. {@code ventas_<id>_2.txt}).
     * Los ids de producto se generan en el rango {@code P1..P<cantidadProductosGenerados>}
     * para que siempre existan en {@code productos.txt}; si aún no se generaron productos,
     * se usa solo {@code P1}.
     *
     * @param randomSalesCount cantidad de líneas de venta a generar en el archivo.
     * @param id               número de documento del vendedor, escrito en el encabezado.
     * @param nombreArchivo    nombre del archivo a crear dentro de {@code data/}.
     */
    private static void escribirArchivoVentas(int randomSalesCount, long id, String nombreArchivo) {
        File archivo = new File(CARPETA_DATOS, nombreArchivo);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(RandomDataProvider.randomTipoDocumento() + ";" + id);
            writer.newLine();

            int maxIdProducto = Math.max(cantidadProductosGenerados, 1);
            for (int i = 0; i < randomSalesCount; i++) {
                String idProducto = "P" + (1 + (int) (Math.random() * maxIdProducto));
                int cantidad = RandomDataProvider.randomCantidad();
                writer.write(idProducto + ";" + cantidad);
                writer.newLine();
            }
        } catch (IOException e) {
            handleGenerationError(e);
        }
    }

    /**
     * Genera el archivo {@code data/productos.txt} con la cantidad de productos indicada,
     * en formato {@code id;nombre;precio}. Si la escritura termina bien, recuerda la cantidad
     * para que las ventas generadas después solo referencien productos existentes.
     *
     * @param productsCount cantidad de productos a generar, con ids consecutivos {@code P1..Pn}.
     */
    public static void createProductsFile(int productsCount) {
        File archivo = new File(CARPETA_DATOS, "productos.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (int i = 1; i <= productsCount; i++) {
                String id = "P" + i;
                String nombre = "Producto" + i;
                double precio = RandomDataProvider.randomPrecio();
                writer.write(id + ";" + nombre + ";" + precio);
                writer.newLine();
            }
            cantidadProductosGenerados = productsCount;
        } catch (IOException e) {
            handleGenerationError(e);
        }
    }

    /**
     * Genera el archivo {@code data/vendedores.txt} con la cantidad de vendedores indicada,
     * en formato {@code TipoDoc;NumDoc;Nombres;Apellidos}.
     *
     * @param salesmanCount cantidad de vendedores a generar.
     */
    public static void createSalesManInfoFile(int salesmanCount) {
        File archivo = new File(CARPETA_DATOS, "vendedores.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            for (int i = 0; i < salesmanCount; i++) {
                String tipoDocumento = RandomDataProvider.randomTipoDocumento();
                long numeroDocumento = RandomDataProvider.randomDocumento();
                String nombres = RandomDataProvider.randomNombre();
                String apellidos = RandomDataProvider.randomApellido();

                writer.write(tipoDocumento + ";" + numeroDocumento + ";" + nombres + ";" + apellidos);
                writer.newLine();
            }
        } catch (IOException e) {
            handleGenerationError(e);
        }
    }

    /**
     * Lee el archivo {@code data/vendedores.txt} recién generado y extrae los números de documento,
     * usados por {@link #main(String[])} para nombrar los archivos de ventas correspondientes.
     *
     * @return lista de números de documento de los vendedores generados.
     * @throws IOException si el archivo de vendedores no puede leerse.
     */
    private static List<Long> leerDocumentosVendedores() throws IOException {
        List<Long> documentos = new ArrayList<>();
        File archivo = new File(CARPETA_DATOS, "vendedores.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    documentos.add(Long.parseLong(partes[1]));
                }
            }
        }
        return documentos;
    }

    /**
     * Crea la carpeta {@code data/} (y sus carpetas padre, si hacen falta) cuando
     * todavía no existe.
     */
    private static void crearCarpetaDatos() {
        File carpeta = new File(CARPETA_DATOS);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    /**
     * Maneja de forma controlada cualquier error ocurrido durante la generación de archivos:
     * marca {@code huboErrores} y muestra por la salida de error un mensaje según el tipo
     * de excepción (E/S o inesperada).
     *
     * @param e la excepción capturada.
     */
    private static void handleGenerationError(Exception e) {
        huboErrores = true;
        if (e instanceof IOException) {
            System.err.println("Error de escritura/lectura generando los archivos en '" + CARPETA_DATOS
                    + "' (¿faltan permisos o espacio?): " + e.getMessage());
        } else {
            System.err.println("Error inesperado generando los archivos de prueba ("
                    + e.getClass().getSimpleName() + "): " + e.getMessage());
        }
    }
}
