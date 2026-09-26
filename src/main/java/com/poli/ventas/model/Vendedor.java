package com.poli.ventas.model;

/**
 * Representa un vendedor identificado por su documento.
 */
public class Vendedor {

    private String tipoDocumento;
    private long numeroDocumento;
    private String nombres;
    private String apellidos;

    /**
     * Crea un vendedor con todos sus datos de identificación.
     *
     * @param tipoDocumento   tipo de documento del vendedor (ej. {@code "CC"}, {@code "CE"}, {@code "TI"}).
     * @param numeroDocumento número de documento del vendedor, usado como identificador único.
     * @param nombres         nombres del vendedor.
     * @param apellidos       apellidos del vendedor.
     */
    public Vendedor(String tipoDocumento, long numeroDocumento, String nombres, String apellidos) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    /**
     * @return el tipo de documento del vendedor.
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento nuevo tipo de documento del vendedor.
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * @return el número de documento del vendedor.
     */
    public long getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * @param numeroDocumento nuevo número de documento del vendedor.
     */
    public void setNumeroDocumento(long numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    /**
     * @return los nombres del vendedor.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * @param nombres nuevos nombres del vendedor.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * @return los apellidos del vendedor.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos nuevos apellidos del vendedor.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return representación en texto del vendedor, útil para depuración y logs.
     */
    @Override
    public String toString() {
        return "Vendedor{" +
                "tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento=" + numeroDocumento +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                '}';
    }
}
