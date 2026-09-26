package com.poli.ventas.io;

import com.poli.ventas.model.Producto;

import java.io.BufferedReader;
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
     * Lee un archivo de productos con líneas en formato {@code id;nombre;precio}.
     *
     * @param rutaArchivo ruta del archivo de productos (ej. {@code data/productos.txt}).
     * @return mapa de productos leídos, con el id de producto como llave.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static Map<String, Producto> leer(String rutaArchivo) throws IOException {
        Map<String, Producto> productos = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                if (partes.length < 3) {
                    continue;
                }
                String id = partes[0];
                String nombre = partes[1];
                double precio = Double.parseDouble(partes[2]);
                productos.put(id, new Producto(id, nombre, precio));
            }
        }
        return productos;
    }
}
