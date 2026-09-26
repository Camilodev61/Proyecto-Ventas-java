package com.poli.ventas.model;

/**
 * Representa un producto disponible para la venta.
 */
public class Producto {

    private String id;
    private String nombre;
    private double precioPorUnidad;

    /**
     * Crea un producto con su identificador, nombre y precio unitario.
     *
     * @param id              identificador único del producto (ej. {@code "P1"}).
     * @param nombre          nombre descriptivo del producto.
     * @param precioPorUnidad precio de venta de una unidad del producto.
     */
    public Producto(String id, String nombre, double precioPorUnidad) {
        this.id = id;
        this.nombre = nombre;
        this.precioPorUnidad = precioPorUnidad;
    }

    /**
     * @return el identificador único del producto.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id nuevo identificador único del producto.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return el nombre descriptivo del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre descriptivo del producto.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return el precio de venta de una unidad del producto.
     */
    public double getPrecioPorUnidad() {
        return precioPorUnidad;
    }

    /**
     * @param precioPorUnidad nuevo precio de venta de una unidad del producto.
     */
    public void setPrecioPorUnidad(double precioPorUnidad) {
        this.precioPorUnidad = precioPorUnidad;
    }

    /**
     * @return representación en texto del producto, útil para depuración y logs.
     */
    @Override
    public String toString() {
        return "Producto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precioPorUnidad=" + precioPorUnidad +
                '}';
    }
}
