package com.poli.ventas.generator;

import java.util.Random;

/**
 * Provee datos pseudoaleatorios (nombres, documentos, precios) usados para
 * generar los archivos de prueba del proyecto.
 */
public class RandomDataProvider {

    private static final String[] NOMBRES = {
            "Andres", "Camila", "Juan", "Maria", "Carlos", "Laura", "Santiago", "Valentina",
            "Felipe", "Daniela", "David", "Paula", "Miguel", "Natalia", "Diego", "Sofia",
            "Julian", "Manuela", "Sebastian", "Isabella", "Alejandro", "Mariana", "Nicolas",
            "Gabriela", "Cristian", "Luisa", "Jorge", "Catalina"
    };

    private static final String[] APELLIDOS = {
            "Gomez", "Rodriguez", "Martinez", "Lopez", "Garcia", "Perez", "Sanchez", "Ramirez",
            "Torres", "Diaz", "Vargas", "Castro", "Ruiz", "Alvarez", "Romero", "Suarez",
            "Rojas", "Moreno", "Munoz", "Herrera", "Jimenez", "Ortiz", "Gutierrez", "Chavez",
            "Reyes", "Cardenas", "Guzman", "Pena"
    };

    private static final String[] TIPOS_DOCUMENTO = {"CC", "CE", "TI"};

    private static final Random RANDOM = new Random();

    private RandomDataProvider() {
    }

    /**
     * @return un nombre de pila tomado de una lista predefinida.
     */
    public static String randomNombre() {
        return NOMBRES[RANDOM.nextInt(NOMBRES.length)];
    }

    /**
     * @return un apellido tomado de una lista predefinida.
     */
    public static String randomApellido() {
        return APELLIDOS[RANDOM.nextInt(APELLIDOS.length)];
    }

    /**
     * @return un número de documento aleatorio entre 1.000.000 y 1.100.000.000.
     */
    public static long randomDocumento() {
        return 1_000_000L + (long) (RANDOM.nextDouble() * 1_100_000_000L);
    }

    /**
     * @return un tipo de documento aleatorio ("CC", "CE" o "TI").
     */
    public static String randomTipoDocumento() {
        return TIPOS_DOCUMENTO[RANDOM.nextInt(TIPOS_DOCUMENTO.length)];
    }

    /**
     * @return un precio unitario aleatorio entre 1.000 y 500.000, con 2 decimales.
     */
    public static double randomPrecio() {
        double precio = 1000 + RANDOM.nextDouble() * 499_000;
        return Math.round(precio * 100.0) / 100.0;
    }

    /**
     * @return una cantidad vendida aleatoria entre 1 y 20 unidades.
     */
    public static int randomCantidad() {
        return 1 + RANDOM.nextInt(20);
    }
}
