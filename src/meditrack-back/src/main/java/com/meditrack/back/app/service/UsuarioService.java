package com.meditrack.back.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.meditrack.back.app.dto.ActualizarUsuarioRequest;
import com.meditrack.back.app.dto.CrearUsuarioRequest;
import com.meditrack.back.app.model.Role;
import com.meditrack.back.app.model.Usuario;
import com.meditrack.back.app.model.HistorialUsuario;
import com.meditrack.back.app.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, NotificacionService notificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public boolean tienePermisoSobreRol(Role rolUsuarioLogueado, Role rolObjetivo) {
        if (rolUsuarioLogueado == Role.ADMINISTRADOR) {
            return rolObjetivo == Role.SUPERVISOR || rolObjetivo == Role.OPERADOR || rolObjetivo == Role.REPARTIDOR;
        }
        if (rolUsuarioLogueado == Role.SUPERVISOR) {
            return rolObjetivo == Role.OPERADOR || rolObjetivo == Role.REPARTIDOR;
        }
        if (rolUsuarioLogueado == Role.OPERADOR) {
            return rolObjetivo == Role.REPARTIDOR;
        }
        return false;
    }

    public Usuario crear(CrearUsuarioRequest datos, Usuario autorDelCambio) {
        Role rolNuevoUsuario = Role.valueOf(datos.getRole());

        if (!tienePermisoSobreRol(autorDelCambio.getRole(), rolNuevoUsuario)) {
            throw new RuntimeException("Tu rol no tiene permisos para crear un " + rolNuevoUsuario);
        }

        if (usuarioRepository.existsByEmail(datos.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (usuarioRepository.existsByDni(datos.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        Usuario nuevo = new Usuario(
            datos.getEmail(),
            datos.getNombre(),
            datos.getDni(),
            passwordEncoder.encode(datos.getPassword()),
            rolNuevoUsuario
        );

        agregarHistorial(nuevo, "Creación", "-", "-", LocalDateTime.now().toString(), autorDelCambio);

        Usuario saved = usuarioRepository.save(nuevo);

        try {
            notificacionService.crearNotificacion(
                saved,
                "Registro Confirmado",
                "Se ha confirmado tu registro en el sistema por el administrador " + autorDelCambio.getNombre() + " (" + autorDelCambio.getRole() + ")."
            );
        } catch (Exception e) {
            System.err.println("Error al enviar notificación de confirmación de registro: " + e.getMessage());
        }

        return saved;
    }

    public Usuario actualizar(String id, ActualizarUsuarioRequest datos, Usuario autorDelCambio) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Role rolObjetivo = datos.tieneRole() ? Role.valueOf(datos.getRole()) : usuario.getRole();

        if (!tienePermisoSobreRol(autorDelCambio.getRole(), rolObjetivo)) {
            throw new RuntimeException("No tienes jerarquía suficiente para modificar o asignar este rol.");
        }

        String fechaModificacion = LocalDateTime.now().toString();

        if (datos.tieneNombre() && !usuario.getNombre().equals(datos.getNombre())) {
            agregarHistorial(usuario, "Nombre", usuario.getNombre(), datos.getNombre(), fechaModificacion, autorDelCambio);
            usuario.setNombre(datos.getNombre());
        }

        if (datos.tieneEmail() && !usuario.getEmail().equals(datos.getEmail())) {
            if (usuarioRepository.existsByEmail(datos.getEmail())) {
                throw new RuntimeException("El email ya está registrado en otra cuenta");
            }
            agregarHistorial(usuario, "Email", usuario.getEmail(), datos.getEmail(), fechaModificacion, autorDelCambio);
            usuario.setEmail(datos.getEmail());
        }

        if (datos.tieneDni() && !usuario.getDni().equals(datos.getDni())) {
            if (usuarioRepository.existsByDni(datos.getDni())) {
                throw new RuntimeException("El DNI ya está registrado en otra cuenta");
            }
            agregarHistorial(usuario, "DNI", usuario.getDni(), datos.getDni(), fechaModificacion, autorDelCambio);
            usuario.setDni(datos.getDni());
        }

        if (datos.tieneRole() && usuario.getRole() != rolObjetivo) {
            agregarHistorial(usuario, "Role", usuario.getRole().toString(), rolObjetivo.toString(), fechaModificacion, autorDelCambio);
            usuario.setRole(rolObjetivo);
        }

        if (datos.tienePassword()) {
            if (!passwordEncoder.matches(datos.getPassword(), usuario.getPassword())) {
                agregarHistorial(usuario, "Password", "[protegida]", "[protegida]", fechaModificacion, autorDelCambio);
                usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
            }
        }

        Usuario saved = usuarioRepository.save(usuario);

        try {
            notificacionService.crearNotificacion(
                saved,
                "Datos de Usuario Modificados",
                "Tus datos personales o rol en la plataforma fueron modificados. Administrador responsable: " + autorDelCambio.getNombre() + "."
            );
        } catch (Exception e) {
            System.err.println("Error al enviar notificación de actualización de usuario: " + e.getMessage());
        }

        return saved;
    }

    public Usuario toggleEstado(String id, Usuario autorDelCambio) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!tienePermisoSobreRol(autorDelCambio.getRole(), usuario.getRole())) {
            throw new RuntimeException("No tienes permiso para desactivar a este usuario.");
        }

        String estadoAnterior = usuario.isEstadoActivo() ? "Activo" : "Inactivo";
        usuario.setEstadoActivo(!usuario.isEstadoActivo());
        String estadoActual = usuario.isEstadoActivo() ? "Activo" : "Inactivo";

        agregarHistorial(usuario, "Estado", estadoAnterior, estadoActual, LocalDateTime.now().toString(), autorDelCambio);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    public String hashearPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verificarPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    private void agregarHistorial(Usuario usuario, String campo, String valorAnterior, String valorNuevo, String fecha, Usuario autorDelCambio) {
        HistorialUsuario h = new HistorialUsuario(campo, valorAnterior, valorNuevo, fecha, autorDelCambio);
        h.setUsuario(usuario);
        usuario.addHistorial(h);
    }
    
}