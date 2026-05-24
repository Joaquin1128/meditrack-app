package com.meditrack.back.app.dto;

import jakarta.validation.constraints.*;

public class ActualizarClienteRequest {

    private String nombre;

    @Pattern(regexp = "^$|^30\\d{9}$", message = "El CUIT debe tener 11 dígitos y comenzar con 30")
    private String cuit;

    @Pattern(regexp = "^$|^\\d{13}$", message = "El GLN debe tener exactamente 13 dígitos")
    private String gln;

    @Pattern(regexp = "^$|^[\\d\\s\\-\\+]{8,}$", message = "El teléfono debe tener al menos 8 dígitos y no puede contener letras")
    private String telefono;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    private String direccion;

    @Pattern(regexp = "^$|^(LABORATORIO|DEPOSITO|HOSPITAL|FARMACIA)$", message = "El tipo debe ser LABORATORIO, DEPOSITO, HOSPITAL o FARMACIA")
    private String tipoEstablecimiento;

    private Double latitud;
    private Double longitud;
    private String placeId;

    public boolean tieneNombre() {
        return nombre != null && !nombre.isBlank();
    }

    public boolean tieneCuit() {
        return cuit != null && !cuit.isBlank();
    }

    public boolean tieneGln() {
        return gln != null && !gln.isBlank();
    }

    public boolean tieneTelefono() {
        return telefono != null && !telefono.isBlank();
    }

    public boolean tieneEmail() {
        return email != null && !email.isBlank();
    }

    public boolean tieneDireccion() {
        return direccion != null && !direccion.isBlank();
    }

    public boolean tieneTipoEstablecimiento() {
        return tipoEstablecimiento != null && !tipoEstablecimiento.isBlank();
    }

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
