package com.poli.ventas.io;

import com.poli.ventas.model.Vendedor;
import com.poli.ventas.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.File;
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
     * Las líneas vacías se ignoran; las de formato incorrecto o con documento no numérico se
     * registran y se descartan.
     *
     * @param rutaArchivo ruta del archivo de vendedores (ej. {@code data/vendedores.txt}).
     * @return lista de vendedores válidos, en el mismo orden del archivo.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static List<Vendedor> leer(String rutaArchivo) throws IOException {
        List<Vendedor> vendedores = new ArrayList<>();
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
                if (partes.length < 4) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con formato incorrecto: '" + linea + "'");
                    continue;
                }
                long numeroDocumento;
                try {
                    numeroDocumento = Long.parseLong(partes[1].trim());
                } catch (NumberFormatException e) {
                    ValidationUtil.logFormatoErroneo(nombreArchivo, "línea " + numeroLinea + " con documento no numérico: '" + linea + "'");
                    continue;
                }
                vendedores.add(new Vendedor(partes[0].trim(), numeroDocumento, partes[2].trim(), partes[3].trim()));
            }
        }
        return vendedores;
    }
}
