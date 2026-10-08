package com.poli.ventas.io;

import com.poli.ventas.model.DetalleVenta;
import com.poli.ventas.model.Producto;
import com.poli.ventas.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lee el archivo de ventas de un vendedor generado por {@code GenerateInfoFiles}.
 * El archivo tiene un encabezado con el documento del vendedor y luego una línea
 * por cada producto vendido.
 */
public class VentaFileReader {

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private VentaFileReader() {
    }

    /**
     * Lee las líneas de detalle de venta de un archivo de ventas, ignorando el encabezado,
     * sin validar la existencia del producto. Equivale a {@link #leer(File, Map)} con catálogo
     * {@code null}; igualmente descarta líneas con formato incorrecto o cantidad inválida.
     *
     * @param archivoVentaVendedor archivo de ventas de un vendedor (ej. {@code data/ventas_123.txt}).
     * @return lista de detalles de venta (id de producto y cantidad) leídos del archivo.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static List<DetalleVenta> leer(File archivoVentaVendedor) throws IOException {
        return leer(archivoVentaVendedor, null);
    }

    /**
     * Lee las líneas de detalle de venta descartando y registrando las inválidas:
     * formato incorrecto, cantidad no numérica o no positiva, e id de producto
     * inexistente en el catálogo.
     *
     * @param archivoVentaVendedor archivo de ventas de un vendedor (ej. {@code data/ventas_123.txt}).
     * @param productos            catálogo de productos con el id como llave; si es {@code null} no se valida el id.
     * @return lista de detalles de venta válidos leídos del archivo.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static List<DetalleVenta> leer(File archivoVentaVendedor, Map<String, Producto> productos) throws IOException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String nombreArchivo = archivoVentaVendedor.getName();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoVentaVendedor))) {
            reader.readLine();
            int numeroLinea = 1;
            String linea;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                if (partes.length < 2) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con formato incorrecto: '" + linea + "'");
                    continue;
                }
                String idProducto = partes[0].trim();
                int cantidad;
                try {
                    cantidad = Integer.parseInt(partes[1].trim());
                } catch (NumberFormatException e) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con cantidad no numérica: '" + linea + "'");
                    continue;
                }
                if (!ValidationUtil.cantidadValida(cantidad)) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con cantidad no positiva: '" + linea + "'");
                    continue;
                }
                if (!ValidationUtil.idProductoExiste(idProducto, productos)) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con producto inexistente '" + idProducto + "'");
                    continue;
                }
                detalles.add(new DetalleVenta(idProducto, cantidad));
            }
        }
        return detalles;
    }

    /**
     * Extrae el encabezado (tipo y número de documento del vendedor) de un archivo de ventas.
     *
     * @param archivo archivo de ventas de un vendedor.
     * @return arreglo {@code [tipoDocumento, numeroDocumento]} leído de la primera línea del archivo.
     * @throws IOException si el archivo no existe, no puede leerse, o no tiene encabezado válido.
     */
    public static String[] extraerEncabezado(File archivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String primeraLinea = reader.readLine();
            if (primeraLinea == null) {
                throw new IOException("El archivo " + archivo.getName() + " está vacío, no tiene encabezado.");
            }
            String[] partes = primeraLinea.split(";");
            if (partes.length < 2) {
                throw new IOException("Encabezado inválido en " + archivo.getName() + ": " + primeraLinea);
            }
            return new String[]{partes[0], partes[1]};
        }
    }
}
