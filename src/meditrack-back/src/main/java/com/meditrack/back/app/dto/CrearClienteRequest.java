package com.meditrack.back.app.dto;

import jakarta.validation.constraints.*;

public class CrearClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El CUIT es obligatorio")
    @Pattern(regexp = "^30\\d{9}$", message = "El CUIT debe tener 11 dígitos y comenzar con 30")
    private String cuit;

    @NotBlank(message = "El GLN es obligatorio")
    @Pattern(regexp = "^\\d{13}$", message = "El GLN debe tener exactamente 13 dígitos")
    private String gln;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[\\d\\s\\-\\+]{8,}$", message = "El teléfono debe tener al menos 8 dígitos y no puede contener letras")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El tipo de establecimiento es obligatorio")
    @Pattern(regexp = "^(LABORATORIO|DEPOSITO|HOSPITAL|FARMACIA)$", message = "El tipo debe ser LABORATORIO, DEPOSITO, HOSPITAL o FARMACIA")
    private String tipoEstablecimiento;

    private Double latitud;
    private Double longitud;
    private String placeId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getGln() {
        return gln;
    }

    public void setGln(String gln) {
        this.gln = gln;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoEstablecimiento() {
        return tipoEstablecimiento;
    }

    public void setTipoEstablecimiento(String tipoEstablecimiento) {
        this.tipoEstablecimiento = tipoEstablecimiento;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    
}
