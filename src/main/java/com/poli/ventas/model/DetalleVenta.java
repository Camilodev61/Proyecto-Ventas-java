package com.poli.ventas.model;

/**
 * Representa una línea de venta de un producto dentro del archivo de un vendedor.
 */
public class DetalleVenta {

    private String idProducto;
    private int cantidadVendida;

    /**
     * Crea una línea de detalle de venta.
     *
     * @param idProducto      identificador del producto vendido (ej. {@code "P1"}).
     * @param cantidadVendida cantidad de unidades vendidas de ese producto.
     */
    public DetalleVenta(String idProducto, int cantidadVendida) {
        this.idProducto = idProducto;
        this.cantidadVendida = cantidadVendida;
    }

    /**
     * @return el identificador del producto vendido.
     */
    public String getIdProducto() {
        return idProducto;
    }

    /**
     * @param idProducto nuevo identificador del producto vendido.
     */
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * @return la cantidad de unidades vendidas.
     */
    public int getCantidadVendida() {
        return cantidadVendida;
    }

    /**
     * @param cantidadVendida nueva cantidad de unidades vendidas.
     */
    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    /**
     * @return representación en texto del detalle de venta, útil para depuración y logs.
     */
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "idProducto='" + idProducto + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                '}';
    }
}
