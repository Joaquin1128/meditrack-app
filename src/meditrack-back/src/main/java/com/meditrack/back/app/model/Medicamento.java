package com.meditrack.back.app.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "medicamentos")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Medicamento {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String gtin;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String monodroga;

    @Column(nullable = false)
    private String laboratorio;

    @Column(nullable = false)
    private String presentacion;

    @Column(name = "es_fragil", nullable = false)
    private boolean esFragil = false;

    @Column(name = "es_controlado", nullable = false)
    private boolean esControlado = false;

    @Column(name = "cadena_frio", nullable = false)
    private boolean cadenaFrio = false;

    @Column(name = "temperatura_minima")
    private Double temperaturaMinima;

    @Column(name = "temperatura_maxima")
    private Double temperaturaMaxima;

    @Column(name = "volumen")
    private Double volumen;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private String unidadMedida;

    @Column(name = "detalles_adicionales", length = 2000)
    private String detallesAdicionales;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "estado_activo")
    private boolean estadoActivo = true;

    public Medicamento() {
        this.estadoActivo = true;
    }

    @PrePersist
    public void generarId() {
        if (this.id == null) {
            this.id = "MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public void validar() {
        if (gtin == null || gtin.isBlank())
            throw new IllegalArgumentException("El GTIN es obligatorio");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre comercial es obligatorio");
        if (monodroga == null || monodroga.isBlank())
            throw new IllegalArgumentException("La monodroga es obligatoria");
        if (laboratorio == null || laboratorio.isBlank())
            throw new IllegalArgumentException("El laboratorio es obligatorio");
        if (presentacion == null || presentacion.isBlank())
            throw new IllegalArgumentException("La presentación es obligatoria");

        if (cadenaFrio) {
            if (temperaturaMinima == null || temperaturaMaxima == null)
                throw new IllegalArgumentException(
                        "Si requiere cadena de frío, debe indicar temperatura mínima y máxima");
            if (temperaturaMinima >= temperaturaMaxima)
                throw new IllegalArgumentException("La temperatura mínima debe ser menor a la temperatura máxima");
        } else {
            // Si no requiere frío, limpiar temperaturas por consistencia
            this.temperaturaMinima = null;
            this.temperaturaMaxima = null;
        }
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isEsFragil() {
        return esFragil;
    }

    public void setEsFragil(boolean esFragil) {
        this.esFragil = esFragil;
    }

    public boolean isEsControlado() {
        return esControlado;
    }

    public void setEsControlado(boolean esControlado) {
        this.esControlado = esControlado;
    }

    public boolean isCadenaFrio() {
        return cadenaFrio;
    }

    public void setCadenaFrio(boolean cadenaFrio) {
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

    public Double getVolumen() {
        return volumen;
    }

    public void setVolumen(Double volumen) {
        this.volumen = volumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
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

    public boolean isEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

}
