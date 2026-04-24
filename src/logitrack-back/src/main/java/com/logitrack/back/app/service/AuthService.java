package com.logitrack.back.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logitrack.back.app.model.Role;
import com.logitrack.back.app.model.Sesion;
import com.logitrack.back.app.model.Usuario;

@Service
public class AuthService {

    private final List<Usuario> usuarios = List.of(
        new Usuario("supervisor", "1234", Role.SUPERVISOR),
        new Usuario("operador", "1234", Role.OPERADOR)
    );

    private final Map<String, Sesion> sesiones = new HashMap<>();

    public Sesion login(String username, String password) {
        Usuario usuario = usuarios.stream()
            .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        String token = UUID.randomUUID().toString();
        Sesion sesion = new Sesion(token, usuario.getUsername(), usuario.getRole());
        sesiones.put(token, sesion);

        return sesion;
    }

    public void logout(String token) {
        sesiones.remove(token);
    }

    public Sesion validar(String token) {
        Sesion sesion = sesiones.get(token);
        if (sesion == null) {
            throw new RuntimeException("Token inválido o expirado");
        }

        return sesion;
    }

}
