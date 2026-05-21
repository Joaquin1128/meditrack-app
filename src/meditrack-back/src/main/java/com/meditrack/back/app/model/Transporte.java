package com.meditrack.back.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
    name = "transportes",
    uniqueConstraints = @UniqueConstraint(name = "uk_transporte_patente", 
    columnNames = "patente")
)
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transporte")
    private Long id;

    @NotBlank(message = "La patente es obligatoria")
    @Column(nullable = false, length = 20, unique = true)
    private String patente;

    @NotBlank(message = "El tipo de vehículo es obligatorio")
    @Column(name = "tipo_vehiculo", nullable = false, length = 40)
    private String tipoVehiculo;

    @NotNull(message = "La capacidad de carga es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Column(name = "capacidad_kg", nullable = false)
    private Integer capacidadKg;

    @NotNull(message = "El estado operativo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_operativo", nullable = false, length = 20)
    private EstadoOperativo estadoOperativo = EstadoOperativo.ACTIVO;

    public Transporte () {}
    
    // Getters y Setters

    public Long getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }
    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }
    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public Integer getCapacidadKg() {
        return capacidadKg;
    }
    public void setCapacidadKg(Integer capacidadKg) {
        this.capacidadKg = capacidadKg;
    }

    public EstadoOperativo getEstadoOperativo() {
        return estadoOperativo;
    }
    public void setEstadoOperativo(EstadoOperativo estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

}