package com.poli.ventas.util;

import com.poli.ventas.model.Producto;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validaciones de coherencia de los datos leídos de los archivos planos. Las
 * líneas inválidas se descartan y se registran con {@link #logFormatoErroneo},
 * sin detener el programa.
 */
public class ValidationUtil {

    /** Avisos ya mostrados, para no repetirlos cuando un archivo se lee más de una vez. */
    private static final Set<String> AVISOS_REGISTRADOS = new HashSet<>();

    /**
     * Constructor privado: esta clase es utilitaria y no debe instanciarse.
     */
    private ValidationUtil() {
    }

    /**
     * Verifica que un id de producto exista en el catálogo de productos.
     *
     * @param id        id de producto a verificar (ej. {@code "P3"}).
     * @param productos catálogo de productos con el id como llave; si es {@code null} no se valida existencia.
     * @return {@code true} si el catálogo es {@code null} o contiene el id.
     */
    public static boolean idProductoExiste(String id, Map<String, Producto> productos) {
        return productos == null || productos.containsKey(id);
    }

    /**
     * Verifica que una cantidad vendida sea positiva.
     *
     * @param cantidad cantidad de unidades vendidas.
     * @return {@code true} si la cantidad es mayor que cero.
     */
    public static boolean cantidadValida(int cantidad) {
        return cantidad > 0;
    }

    /**
     * Verifica que un precio unitario sea positivo y finito.
     *
     * @param precio precio por unidad de un producto.
     * @return {@code true} si el precio es mayor que cero y no es NaN ni infinito.
     */
    public static boolean precioValido(double precio) {
        return precio > 0 && !Double.isNaN(precio) && !Double.isInfinite(precio);
    }

    /**
     * Registra por la salida de error una línea o archivo con formato incorrecto.
     *
     * @param archivo nombre del archivo donde se encontró el problema.
     * @param detalle descripción del problema (número de línea, contenido y motivo).
     */
    public static void logFormatoErroneo(String archivo, String detalle) {
        String mensaje = "[AVISO] " + archivo + ": " + detalle + " (se omite)";
        if (AVISOS_REGISTRADOS.add(mensaje)) {
            System.err.println(mensaje);
        }
    }
}
