package com.poli.ventas.model;

/**
 * Representa una línea de venta de un producto dentro del archivo de un vendedor.
 */
public class DetalleVenta {

    private String idProducto;
    private int cantidadVendida;

    public DetalleVenta(String idProducto, int cantidadVendida) {
        this.idProducto = idProducto;
        this.cantidadVendida = cantidadVendida;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "idProducto='" + idProducto + '\'' +
                ", cantidadVendida=" + cantidadVendida +
                '}';
    }
}
