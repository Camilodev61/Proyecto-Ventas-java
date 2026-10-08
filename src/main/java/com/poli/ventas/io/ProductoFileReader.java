package com.poli.ventas.io;

import com.poli.ventas.model.Producto;
import com.poli.ventas.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lee el archivo plano de productos generado por {@code GenerateInfoFiles}.
 */
public class ProductoFileReader {

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private ProductoFileReader() {
    }

    /**
     * Lee un archivo de productos con líneas en formato {@code id;nombre;precio}. Las líneas
     * vacías se ignoran; las de formato incorrecto, precio no numérico o no positivo se
     * registran y se descartan. Si un id se repite, prevalece la última línea.
     *
     * @param rutaArchivo ruta del archivo de productos (ej. {@code data/productos.txt}).
     * @return mapa de productos válidos, con el id de producto como llave y en orden de archivo.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static Map<String, Producto> leer(String rutaArchivo) throws IOException {
        Map<String, Producto> productos = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String nombreArchivo = new File(rutaArchivo).getName();
            int numeroLinea = 0;
            String linea;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                if (partes.length < 3) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con formato incorrecto: '" + linea + "'");
                    continue;
                }
                String id = partes[0].trim();
                String nombre = partes[1].trim();
                double precio;
                try {
                    precio = Double.parseDouble(partes[2].trim());
                } catch (NumberFormatException e) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con precio no numérico: '" + linea + "'");
                    continue;
                }
                if (!ValidationUtil.precioValido(precio)) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con precio no positivo: '" + linea + "'");
                    continue;
                }
                productos.put(id, new Producto(id, nombre, precio));
            }
        }
        return productos;
    }
}
