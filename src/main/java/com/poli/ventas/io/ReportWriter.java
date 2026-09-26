package com.poli.ventas.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Escribe los reportes CSV finales (vendedores y productos) a partir de filas ya
 * calculadas y ordenadas por {@code VentaProcessor}.
 */
public class ReportWriter {

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private ReportWriter() {
    }

    /**
     * Escribe el reporte de vendedores ordenado por total recaudado.
     *
     * @param filas       filas ya ordenadas, cada una como {@code [tipoDoc, numDoc, nombres, apellidos, total]}.
     * @param rutaSalida  ruta del archivo CSV a generar (ej. {@code data/reporte_vendedores.csv}).
     * @throws IOException si el archivo no puede escribirse.
     */
    public static void escribirReporteVendedores(List<String[]> filas, String rutaSalida) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida))) {
            writer.write("TipoDocumento;NumeroDocumento;Nombres;Apellidos;TotalRecaudado");
            writer.newLine();
            for (String[] fila : filas) {
                writer.write(String.join(";", fila));
                writer.newLine();
            }
        }
    }

    /**
     * Escribe el reporte de productos ordenado por cantidad vendida.
     *
     * @param filas       filas ya ordenadas, cada una como {@code [idProducto, nombre, cantidadVendida]}.
     * @param rutaSalida  ruta del archivo CSV a generar (ej. {@code data/reporte_productos.csv}).
     * @throws IOException si el archivo no puede escribirse.
     */
    public static void escribirReporteProductos(List<String[]> filas, String rutaSalida) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida))) {
            writer.write("IdProducto;Nombre;CantidadVendida");
            writer.newLine();
            for (String[] fila : filas) {
                writer.write(String.join(";", fila));
                writer.newLine();
            }
        }
    }
}
