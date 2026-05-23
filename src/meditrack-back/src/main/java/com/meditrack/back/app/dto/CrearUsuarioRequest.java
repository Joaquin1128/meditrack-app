package com.meditrack.back.app.dto;

import jakarta.validation.constraints.*;

public class CrearUsuarioRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]{2,}(\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]{2,})+$",
        message = "El nombre debe contener nombre y apellido, cada uno con al menos 2 letras"
    )
    private String nombre;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
        regexp = "^\\d{7,8}$",
        message = "El DNI debe tener entre 7 y 8 dígitos numéricos"
    )
    private String dni;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
        message = "La contraseña debe contener al menos una letra y un número"
    )
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
        regexp = "^(ADMINISTRADOR|SUPERVISOR|OPERADOR|REPARTIDOR)$",
        message = "El rol debe ser ADMINISTRADOR, SUPERVISOR, OPERADOR o REPARTIDOR"
    )
    private String role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
