package com.meditrack.back.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.meditrack.back.app.model.Medicamento;
import com.meditrack.back.app.repository.MedicamentoRepository;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    public Medicamento obtenerPorId(String id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
    }

    public Medicamento obtenerPorGtin(String gtin) {
        return medicamentoRepository.findByGtin(gtin)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado para el GTIN: " + gtin));
    }

    public Medicamento crear(Map<String, Object> datos, String usuario) {
        // RF 6.4.2: validar que el GTIN no exista previamente
        String gtin = (String) datos.get("gtin");

        if (gtin == null || gtin.isBlank())
            throw new IllegalArgumentException("El GTIN es obligatorio");
        if (medicamentoRepository.findByGtin(gtin).isPresent())
            throw new IllegalArgumentException("Ya existe un medicamento con el GTIN: " + gtin);

        Medicamento nuevo = new Medicamento();
        mapDataToMedicamento(datos, nuevo);
        nuevo.validar();

        if (medicamentoRepository.existsByGtin(gtin))
    throw new IllegalArgumentException("Ya existe un medicamento con el GTIN: " + gtin);

        return medicamentoRepository.save(nuevo);
    }

    public Medicamento actualizar(String id, Map<String, Object> body, String usuario) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        String nuevoGtin = (String) body.get("gtin");
        if (nuevoGtin != null && !nuevoGtin.equals(medicamento.getGtin())) {
            if (medicamentoRepository.findByGtin(nuevoGtin).isPresent())
                throw new IllegalArgumentException("Ya existe un medicamento con el GTIN: " + nuevoGtin);
        }

        mapDataToMedicamento(body, medicamento);
        medicamento.validar();

        return medicamentoRepository.save(medicamento);
    }

    public Medicamento cambiarEstado(String id, String motivo, String usuario) {
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("Debe indicar el motivo para cambiar el estado del medicamento");

        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        medicamento.setEstadoActivo(!medicamento.isEstadoActivo());

        return medicamentoRepository.save(medicamento);
    }

    // --- Mapeo de datos ---

    private void mapDataToMedicamento(Map<String, Object> body, Medicamento medicamento) {
        setIfPresent(body, "gtin", v -> medicamento.setGtin((String) v));
        setIfPresent(body, "nombre", v -> medicamento.setNombre((String) v));
        setIfPresent(body, "monodroga", v -> medicamento.setMonodroga((String) v));
        setIfPresent(body, "laboratorio", v -> medicamento.setLaboratorio((String) v));
        setIfPresent(body, "presentacion", v -> medicamento.setPresentacion((String) v));
        setIfPresent(body, "descripcion", v -> medicamento.setDescripcion((String) v));
        setIfPresent(body, "detallesAdicionales", v -> medicamento.setDetallesAdicionales((String) v));
        setIfPresent(body, "unidadMedida", v -> medicamento.setUnidadMedida((String) v));

        // Booleanos
        setIfPresent(body, "cadenaFrio", v -> medicamento.setCadenaFrio(parseBoolean(v)));
        setIfPresent(body, "esFragil", v -> medicamento.setEsFragil(parseBoolean(v)));
        setIfPresent(body, "esControlado", v -> medicamento.setEsControlado(parseBoolean(v)));

        // Numéricos
        setIfPresent(body, "cantidad", v -> medicamento.setCantidad(parseInteger(v)));
        setIfPresent(body, "volumen", v -> medicamento.setVolumen(parseDouble(v)));
        setIfPresent(body, "temperaturaMinima", v -> medicamento.setTemperaturaMinima(parseDouble(v)));
        setIfPresent(body, "temperaturaMaxima", v -> medicamento.setTemperaturaMaxima(parseDouble(v)));

        // Imagen: solo actualizar si viene un valor
        setIfPresent(body, "imagenUrl", v -> {
            String url = (String) v;
            if (!url.isBlank())
                medicamento.setImagenUrl(url);
        });
    }

    // --- Helpers ---

    private void setIfPresent(Map<String, Object> body, String key, java.util.function.Consumer<Object> setter) {
        if (body.containsKey(key) && body.get(key) != null) {
            setter.accept(body.get(key));
        }
    }

    private boolean parseBoolean(Object v) {
        if (v instanceof Boolean b)
            return b;
        return Boolean.parseBoolean(v.toString());
    }

    private int parseInteger(Object v) {
        if (v instanceof Integer i)
            return i;
        return Integer.parseInt(v.toString());
    }

    private Double parseDouble(Object v) {
        if (v instanceof Double d)
            return d;
        if (v instanceof Number n)
            return n.doubleValue();
        return Double.parseDouble(v.toString());
    }

}
