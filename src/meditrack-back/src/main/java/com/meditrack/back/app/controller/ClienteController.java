package com.meditrack.back.app.controller;

import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.meditrack.back.app.dto.ActualizarClienteRequest;
import com.meditrack.back.app.dto.CrearClienteRequest;
import com.meditrack.back.app.model.Cliente;
import com.meditrack.back.app.model.Sesion;
import com.meditrack.back.app.service.AuthService;
import com.meditrack.back.app.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;
    private final AuthService authService;

    public ClienteController(ClienteService clienteService, AuthService authService) {
        this.clienteService = clienteService;
        this.authService = authService;
    }

    private Sesion autenticar(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token requerido");
        }
        return authService.validar(authHeader.substring(7));
    }

    private Map<String, String> erroresDeValidacion(BindingResult br) {
        return br.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage(),
                        (m1, m2) -> m1
                ));
    }

    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            autenticar(authHeader);
            return ResponseEntity.ok(clienteService.listarTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            autenticar(authHeader);
            return ResponseEntity.ok(clienteService.obtenerPorId(id));
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("no encontrado")
                    ? HttpStatus.NOT_FOUND : HttpStatus.UNAUTHORIZED;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody CrearClienteRequest body,
            BindingResult bindingResult,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errores", erroresDeValidacion(bindingResult)));
            }
            Sesion sesion = autenticar(authHeader);
            Cliente nuevo = clienteService.crear(body, sesion.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @Valid @RequestBody ActualizarClienteRequest body,
            BindingResult bindingResult,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("errores", erroresDeValidacion(bindingResult)));
            }
            Sesion sesion = autenticar(authHeader);
            Cliente actualizado = clienteService.actualizar(id, body, sesion.getEmail());
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cambiarEstado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            autenticar(authHeader);
            return ResponseEntity.ok(clienteService.cambiarEstado(id));
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().contains("no encontrado")
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(Map.of("error", e.getMessage()));
        }
    }
    
}
