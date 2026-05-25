package com.meditrack.back.app.dto;

public class ActualizarMedicamentoRequest {

    // Todos opcionales — solo se actualizan los que vienen presentes

    private String gtin;
    private String nombre;
    private String monodroga;
    private String laboratorio;
    private String presentacion;
    private Boolean cadenaFrio;
    private Double temperaturaMinima;
    private Double temperaturaMaxima;
    private Boolean esFragil;
    private Boolean esControlado;
    private Double volumen;
    private Integer cantidad;
    private String unidadMedida;
    private String descripcion;
    private String detallesAdicionales;
    private String imagenUrl;

    public void validate() {
        if (tieneGtin() && !gtin.matches("^\\d{13,14}$"))
            throw new IllegalArgumentException("El GTIN debe tener 13 o 14 dígitos numéricos");

        if (Boolean.TRUE.equals(cadenaFrio)) {
            if (temperaturaMinima == null)
                throw new IllegalArgumentException(
                        "La temperatura mínima es obligatoria cuando requiere cadena de frío");
            if (temperaturaMaxima == null)
                throw new IllegalArgumentException(
                        "La temperatura máxima es obligatoria cuando requiere cadena de frío");
        }

        if (temperaturaMinima != null && temperaturaMaxima != null) {
            if (temperaturaMinima >= temperaturaMaxima)
                throw new IllegalArgumentException("La temperatura mínima debe ser menor a la temperatura máxima");
        }

        if (volumen != null && volumen < 0)
            throw new IllegalArgumentException("El volumen no puede ser negativo");

        if (cantidad != null && cantidad < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
    }

    public boolean tieneGtin() {
        return gtin != null && !gtin.isBlank();
    }

    public boolean tieneNombre() {
        return nombre != null && !nombre.isBlank();
    }

    public boolean tieneMonodroga() {
        return monodroga != null && !monodroga.isBlank();
    }

    public boolean tieneLaboratorio() {
        return laboratorio != null && !laboratorio.isBlank();
    }

    public boolean tienePresentacion() {
        return presentacion != null && !presentacion.isBlank();
    }

    public boolean tieneCadenaFrio() {
        return cadenaFrio != null;
    }

    public boolean tieneTemperaturaMinima() {
        return temperaturaMinima != null;
    }

    public boolean tieneTemperaturaMaxima() {
        return temperaturaMaxima != null;
    }

    public boolean tieneEsFragil() {
        return esFragil != null;
    }

    public boolean tieneEsControlado() {
        return esControlado != null;
    }

    public boolean tieneVolumen() {
        return volumen != null;
    }

    public boolean tieneCantidad() {
        return cantidad != null;
    }

    public boolean tieneUnidadMedida() {
        return unidadMedida != null && !unidadMedida.isBlank();
    }

    public boolean tieneDescripcion() {
        return descripcion != null && !descripcion.isBlank();
    }

    public boolean tieneDetallesAdicionales() {
        return detallesAdicionales != null && !detallesAdicionales.isBlank();
    }

    public boolean tieneImagenUrl() {
        return imagenUrl != null && !imagenUrl.isBlank();
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