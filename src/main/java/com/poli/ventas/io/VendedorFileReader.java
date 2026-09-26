package com.poli.ventas.io;

import com.poli.ventas.model.Vendedor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee el archivo plano de vendedores generado por {@code GenerateInfoFiles}.
 */
public class VendedorFileReader {

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private VendedorFileReader() {
    }

    /**
     * Lee un archivo de vendedores con líneas en formato {@code TipoDoc;NumDoc;Nombres;Apellidos}.
     *
     * @param rutaArchivo ruta del archivo de vendedores (ej. {@code data/vendedores.txt}).
     * @return lista de vendedores leídos, en el mismo orden del archivo.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static List<Vendedor> leer(String rutaArchivo) throws IOException {
        List<Vendedor> vendedores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(";");
                if (partes.length < 4) {
                    continue;
                }
                String tipoDocumento = partes[0];
                long numeroDocumento = Long.parseLong(partes[1]);
                String nombres = partes[2];
                String apellidos = partes[3];
                vendedores.add(new Vendedor(tipoDocumento, numeroDocumento, nombres, apellidos));
            }
        }
        return vendedores;
    }
}
