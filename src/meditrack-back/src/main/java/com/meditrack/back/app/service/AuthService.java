package com.meditrack.back.app.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.meditrack.back.app.config.JwtUtil;
import com.meditrack.back.app.model.Sesion;
import com.meditrack.back.app.model.Usuario;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    public AuthService(JwtUtil jwtUtil, UsuarioService usuarioService) {
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }
    public Map<String, String> login(String email, String password) {
        Usuario usuario = usuarioService.buscarPorEmail(email)
            .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Credenciales inválidas");
        }
        if (!usuario.isEstadoActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte a un administrador.");
        }
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getNombre(), usuario.getRole());
        return Map.of(
            "token",  token,
            "email",  usuario.getEmail(),
            "nombre", usuario.getNombre(),
            "role",   usuario.getRole().name()
        );
    }

    public Sesion validar(String token) {
        return jwtUtil.validar(token);
    }
    
}