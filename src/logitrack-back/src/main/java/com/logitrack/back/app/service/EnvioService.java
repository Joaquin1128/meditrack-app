package com.logitrack.back.app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.logitrack.back.app.model.Envio;
import com.logitrack.back.app.model.EstadoEnvio;
import com.logitrack.back.app.model.HistorialEstado;

@Service
public class EnvioService {

    private final List<Envio> envios = new ArrayList<>();

    public EnvioService() {
        Envio e1 = new Envio("1", "Juan Pérez", "Ana García", "Av. Siempre Viva 123",
                "Buenos Aires", "Córdoba", "2026-04-01", "", EstadoEnvio.CREADO);
        e1.setPrioridad("ALTA"); 
        e1.agregarHistorial(new HistorialEstado(EstadoEnvio.CREADO, "2026-03-28", "09:00", "sistema"));
        envios.add(e1);

        Envio e2 = new Envio("2", "Empresa XYZ", "Carlos López", "Calle Falsa 456",
                "Rosario", "Mendoza", "2026-04-05", "Frágil", EstadoEnvio.EN_TRANSITO);
        e2.setPrioridad("MEDIA");         
        e2.agregarHistorial(new HistorialEstado(EstadoEnvio.CREADO,     "2026-03-25", "10:00", "admin"));
        e2.agregarHistorial(new HistorialEstado(EstadoEnvio.EN_TRANSITO, "2026-03-26", "08:30", "admin"));
        envios.add(e2);

        Envio e3 = new Envio("3", "Transportes Sur", "María Fernández", "Belgrano 789",
                "Salta", "Buenos Aires", "2026-03-30", "", EstadoEnvio.EN_SUCURSAL);
        e3.setPrioridad("BAJA"); 
        e3.agregarHistorial(new HistorialEstado(EstadoEnvio.CREADO,     "2026-03-22", "14:00", "admin"));
        e3.agregarHistorial(new HistorialEstado(EstadoEnvio.EN_TRANSITO, "2026-03-24", "07:00", "admin"));
        e3.agregarHistorial(new HistorialEstado(EstadoEnvio.EN_SUCURSAL, "2026-03-27", "15:00", "admin"));
        envios.add(e3);
    }

    public List<Envio> listarTodos() {
        return envios;
    }

    public Envio obtenerPorId(String id) {
        return envios.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Envío no encontrado: " + id));
    }

    public Envio crear(Map<String, String> body) {
        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Envio nuevo = new Envio(
            id,
            body.getOrDefault("remitente", ""),
            body.getOrDefault("destinatario", ""),
            body.getOrDefault("direccionEntrega", ""),
            body.getOrDefault("origen", ""),
            body.getOrDefault("destino", ""),
            body.getOrDefault("fechaEstimada", ""),
            body.getOrDefault("observaciones", ""),
            EstadoEnvio.CREADO
        );

        String hoy  = LocalDate.now().toString();
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        nuevo.setPrioridad("MEDIA");
        nuevo.agregarHistorial(new HistorialEstado(EstadoEnvio.CREADO, hoy, hora, "sistema")); 
        envios.add(nuevo);

        return nuevo;
    }

    public Envio actualizar(String id, Map<String, String> body) {
        Envio envio = obtenerPorId(id);
        if (body.containsKey("remitente"))        envio.setRemitente(body.get("remitente"));
        if (body.containsKey("destinatario"))     envio.setDestinatario(body.get("destinatario"));
        if (body.containsKey("direccionEntrega")) envio.setDireccionEntrega(body.get("direccionEntrega"));
        if (body.containsKey("origen"))           envio.setOrigen(body.get("origen"));
        if (body.containsKey("destino"))          envio.setDestino(body.get("destino"));
        if (body.containsKey("fechaEstimada"))    envio.setFechaEstimada(body.get("fechaEstimada"));
        if (body.containsKey("observaciones"))    envio.setObservaciones(body.get("observaciones"));

        return envio;
    }

    public Envio cambiarEstado(String id, EstadoEnvio nuevoEstado, String fecha, String hora, String usuario) {
        Envio envio = obtenerPorId(id);
        EstadoEnvio estadoEsperado = envio.getEstado().siguiente();

        if (!estadoEsperado.equals(nuevoEstado)) {
            throw new IllegalArgumentException(
                "Transición inválida: " + envio.getEstado() + " → " + nuevoEstado +
                ". El siguiente estado válido es: " + estadoEsperado
            );
        }

        envio.setEstado(nuevoEstado);
        envio.agregarHistorial(new HistorialEstado(nuevoEstado, fecha, hora, usuario));

        return envio;
    }
    
}
