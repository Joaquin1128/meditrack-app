package com.meditrack.back.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.meditrack.back.app.model.Medicamento;
import com.meditrack.back.app.model.Sesion;
import com.meditrack.back.app.service.AuthService;
import com.meditrack.back.app.service.CloudinaryService;
import com.meditrack.back.app.service.MedicamentoService;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "*")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final AuthService authService;
    private final CloudinaryService cloudinaryService;

    public MedicamentoController(MedicamentoService medicamentoService, AuthService authService,
            CloudinaryService cloudinaryService) {
        this.medicamentoService = medicamentoService;
        this.authService = authService;
        this.cloudinaryService = cloudinaryService;
    }

    // --- Auth helper ---

    private Sesion autenticar(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token requerido");
        }
        return authService.validar(authHeader.substring(7));
    }

    // --- Endpoints ---

    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            autenticar(authHeader);
            return ResponseEntity.ok(medicamentoService.listarTodos());
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
            return ResponseEntity.ok(medicamentoService.obtenerPorId(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/gtin/{gtin}")
    public ResponseEntity<?> obtenerPorGtin(
            @PathVariable String gtin,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            autenticar(authHeader);
            Medicamento med = medicamentoService.obtenerPorGtin(gtin);
            if (!med.isEstadoActivo())
                return ResponseEntity.status(HttpStatus.GONE)
                        .body(Map.of("error", "El medicamento con GTIN " + gtin + " está inactivo"));
            return ResponseEntity.ok(med);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crear(
            @RequestParam String gtin,
            @RequestParam String nombre,
            @RequestParam String monodroga,
            @RequestParam String laboratorio,
            @RequestParam String presentacion,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String detallesAdicionales,
            @RequestParam(defaultValue = "false") boolean cadenaFrio,
            @RequestParam(required = false) Double temperaturaMinima,
            @RequestParam(required = false) Double temperaturaMaxima,
            @RequestParam(defaultValue = "false") boolean esFragil,
            @RequestParam(defaultValue = "false") boolean esControlado,
            @RequestParam(required = false) Double volumen,
            @RequestParam int cantidad,
            @RequestParam(required = false) String unidadMedida,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Sesion sesion = autenticar(authHeader);
            String imageUrl = (imagen != null && !imagen.isEmpty())
                    ? cloudinaryService.subirImagen(imagen)
                    : null;

            Map<String, Object> body = buildMedicamentoBody(
                    gtin, nombre, monodroga, laboratorio, presentacion,
                    descripcion, detallesAdicionales, cadenaFrio,
                    temperaturaMinima, temperaturaMaxima,
                    esFragil, esControlado, volumen,
                    cantidad, unidadMedida, imageUrl);

            Medicamento nuevo = medicamentoService.crear(body, sesion.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @RequestParam String gtin,
            @RequestParam String nombre,
            @RequestParam String monodroga,
            @RequestParam String laboratorio,
            @RequestParam String presentacion,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String detallesAdicionales,
            @RequestParam(defaultValue = "false") boolean cadenaFrio,
            @RequestParam(required = false) Double temperaturaMinima,
            @RequestParam(required = false) Double temperaturaMaxima,
            @RequestParam(defaultValue = "false") boolean esFragil,
            @RequestParam(defaultValue = "false") boolean esControlado,
            @RequestParam(required = false) Double volumen,
            @RequestParam int cantidad,
            @RequestParam(required = false) String unidadMedida,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Sesion sesion = autenticar(authHeader);
            String imageUrl = (imagen != null && !imagen.isEmpty())
                    ? cloudinaryService.subirImagen(imagen)
                    : null;

            Map<String, Object> body = buildMedicamentoBody(
                    gtin, nombre, monodroga, laboratorio, presentacion,
                    descripcion, detallesAdicionales, cadenaFrio,
                    temperaturaMinima, temperaturaMaxima,
                    esFragil, esControlado, volumen,
                    cantidad, unidadMedida, imageUrl);

            Medicamento actualizado = medicamentoService.actualizar(id, body, sesion.getNombre());
            return ResponseEntity.ok(actualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cambiarEstado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Sesion sesion = autenticar(authHeader);
            String motivo = body.get("motivo");
            return ResponseEntity.ok(medicamentoService.cambiarEstado(id, motivo, sesion.getNombre()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    // --- Builder ---

    private Map<String, Object> buildMedicamentoBody(
            String gtin, String nombre, String monodroga, String laboratorio, String presentacion,
            String descripcion, String detallesAdicionales, boolean cadenaFrio,
            Double temperaturaMinima, Double temperaturaMaxima,
            boolean esFragil, boolean esControlado, Double volumen,
            int cantidad, String unidadMedida, String imageUrl) {

        Map<String, Object> body = new HashMap<>();
        body.put("gtin", gtin);
        body.put("nombre", nombre);
        body.put("monodroga", monodroga);
        body.put("laboratorio", laboratorio);
        body.put("presentacion", presentacion);
        body.put("descripcion", descripcion);
        body.put("detallesAdicionales", detallesAdicionales);
        body.put("cadenaFrio", cadenaFrio);
        body.put("temperaturaMinima", temperaturaMinima);
        body.put("temperaturaMaxima", temperaturaMaxima);
        body.put("esFragil", esFragil);
        body.put("esControlado", esControlado);
        body.put("volumen", volumen);
        body.put("cantidad", cantidad);
        body.put("unidadMedida", unidadMedida);
        if (imageUrl != null) body.put("imagenUrl", imageUrl);
        return body;
    }

}
