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

    /**
     * Orquesta la generación de los archivos de prueba: crea la carpeta {@code data/}
     * si no existe, genera el archivo de productos, el archivo de vendedores y, por
     * cada vendedor generado, un archivo de ventas asociado a su número de documento.
     * Cualquier error durante la generación se reporta por consola sin detener la JVM
     * abruptamente.
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

            for (long idVendedor : leerDocumentosVendedores()) {
                createSalesMenFile(ventasPorVendedor, RandomDataProvider.randomNombre(), idVendedor);
            }

            System.out.println("Generación de archivos completada exitosamente en la carpeta '" + CARPETA_DATOS + "'.");
        } catch (Exception e) {
            handleGenerationError(e);
        }
    }

    /**
     * Genera el archivo de ventas de un vendedor con un número aleatorio de líneas de venta.
     *
     * @param randomSalesCount cantidad de líneas de venta a generar en el archivo.
     * @param name             nombre usado únicamente para dejar traza en el nombre del archivo (no se persiste en el contenido).
     * @param id               número de documento del vendedor, usado para nombrar el archivo {@code ventas_<id>.txt}.
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) {
        File archivo = new File(CARPETA_DATOS, "ventas_" + id + ".txt");
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
     * Genera el archivo {@code data/productos.txt} con la cantidad de productos indicada.
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
     * Genera el archivo {@code data/vendedores.txt} con la cantidad de vendedores indicada.
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
     * Maneja de forma controlada cualquier error ocurrido durante la generación de archivos.
     *
     * @param e la excepción capturada.
     */
    private static void handleGenerationError(Exception e) {
        System.err.println("Error generando los archivos de prueba: " + e.getMessage());
    }
}
