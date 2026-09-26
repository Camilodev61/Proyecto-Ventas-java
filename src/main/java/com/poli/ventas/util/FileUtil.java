package com.poli.ventas.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades transversales para manejo de archivos y carpetas.
 */
public class FileUtil {

    private static final String PREFIJO_VENTAS = "ventas_";
    private static final String EXTENSION_TXT = ".txt";

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private FileUtil() {
    }

    /**
     * Lista todos los archivos de ventas (patrón {@code ventas_*.txt}) dentro de una carpeta,
     * lo que permite soportar varios archivos de ventas por vendedor.
     *
     * @param carpeta ruta de la carpeta donde están los archivos generados/leídos (ej. {@code data}).
     * @return lista de archivos de ventas encontrados; vacía si la carpeta no existe o no tiene archivos.
     */
    public static List<File> listarArchivosVentas(String carpeta) {
        List<File> archivos = new ArrayList<>();
        File directorio = new File(carpeta);
        File[] encontrados = directorio.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(PREFIJO_VENTAS) && name.endsWith(EXTENSION_TXT);
            }
        });
        if (encontrados != null) {
            for (File archivo : encontrados) {
                archivos.add(archivo);
            }
        }
        return archivos;
    }

    /**
     * Crea la carpeta indicada si todavía no existe, incluyendo carpetas padre necesarias.
     *
     * @param ruta ruta de la carpeta a asegurar.
     */
    public static void crearCarpetaSiNoExiste(String ruta) {
        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
}
