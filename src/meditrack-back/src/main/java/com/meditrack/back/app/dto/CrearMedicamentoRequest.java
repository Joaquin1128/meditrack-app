package com.meditrack.back.app.dto;

public class CrearMedicamentoRequest {

    // --- Identificación ---
    private String gtin;
    private String nombre;
    private String monodroga;
    private String laboratorio;
    private String presentacion;

    // --- Características ---
    private Boolean cadenaFrio;
    private Double temperaturaMinima;
    private Double temperaturaMaxima;
    private Boolean esFragil;
    private Boolean esControlado;

    // --- Logística ---
    private Double volumen;

    // --- A discutir con el equipo ---
    private Integer cantidad;
    private String unidadMedida;

    // --- Adicionales ---
    private String descripcion;
    private String detallesAdicionales;
    private String imagenUrl;

    public void validate() {
        if (isBlank(gtin))
            throw new IllegalArgumentException("El GTIN es obligatorio");
        if (!gtin.matches("^\\d{13,14}$"))
            throw new IllegalArgumentException("El GTIN debe tener 13 o 14 dígitos numéricos");

        if (isBlank(nombre))
            throw new IllegalArgumentException("El nombre comercial es obligatorio");

        if (isBlank(monodroga))
            throw new IllegalArgumentException("La monodroga es obligatoria");

        if (isBlank(laboratorio))
            throw new IllegalArgumentException("El laboratorio es obligatorio");

        if (isBlank(presentacion))
            throw new IllegalArgumentException("La presentación es obligatoria");

        if (Boolean.TRUE.equals(cadenaFrio)) {
            if (temperaturaMinima == null)
                throw new IllegalArgumentException(
                        "La temperatura mínima es obligatoria cuando requiere cadena de frío");
            if (temperaturaMaxima == null)
                throw new IllegalArgumentException(
                        "La temperatura máxima es obligatoria cuando requiere cadena de frío");
            if (temperaturaMinima >= temperaturaMaxima)
                throw new IllegalArgumentException("La temperatura mínima debe ser menor a la temperatura máxima");
        }

        if (volumen != null && volumen < 0)
            throw new IllegalArgumentException("El volumen no puede ser negativo");

        if (cantidad != null && cantidad < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
    }

    // --- Helper ---

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // --- Getters y Setters ---

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMonodroga() {
        return monodroga;
    }

    public void setMonodroga(String monodroga) {
        this.monodroga = monodroga;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public Boolean getCadenaFrio() {
        return cadenaFrio;
    }

    public void setCadenaFrio(Boolean cadenaFrio) {
        this.cadenaFrio = cadenaFrio;
    }

    public Double getTemperaturaMinima() {
        return temperaturaMinima;
    }

    public void setTemperaturaMinima(Double temperaturaMinima) {
        this.temperaturaMinima = temperaturaMinima;
    }

    public Double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(Double temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    public Boolean getEsFragil() {
        return esFragil;
    }

    public void setEsFragil(Boolean esFragil) {
        this.esFragil = esFragil;
    }

    public Boolean getEsControlado() {
        return esControlado;
    }

    public void setEsControlado(Boolean esControlado) {
        this.esControlado = esControlado;
    }

    public Double getVolumen() {
        return volumen;
    }

    public void setVolumen(Double volumen) {
        this.volumen = volumen;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDetallesAdicionales() {
        return detallesAdicionales;
    }

    public void setDetallesAdicionales(String detallesAdicionales) {
        this.detallesAdicionales = detallesAdicionales;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

}