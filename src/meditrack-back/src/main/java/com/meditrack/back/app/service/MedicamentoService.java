package com.meditrack.back.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.meditrack.back.app.dto.ActualizarMedicamentoRequest;
import com.meditrack.back.app.dto.CrearMedicamentoRequest;
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

    public Medicamento crear(CrearMedicamentoRequest dto, String usuario) {
        if (medicamentoRepository.existsByGtin(dto.getGtin()))
            throw new IllegalArgumentException("Ya existe un medicamento con el GTIN: " + dto.getGtin());
        if (medicamentoRepository.existsByNombre(dto.getNombre()))
            throw new IllegalArgumentException("Ya existe un medicamento con el nombre: " + dto.getNombre());

        Medicamento nuevo = new Medicamento();
        mapCrearToMedicamento(dto, nuevo);

        return medicamentoRepository.save(nuevo);
    }

    public Medicamento actualizar(String id, ActualizarMedicamentoRequest dto, String usuario) {
        Medicamento medicamento = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        if (dto.tieneGtin() && !dto.getGtin().equals(medicamento.getGtin())) {
            if (medicamentoRepository.existsByGtin(dto.getGtin()))
                throw new IllegalArgumentException("Ya existe un medicamento con el GTIN: " + dto.getGtin());
        }
        if (dto.tieneNombre() && !dto.getNombre().equals(medicamento.getNombre())) {
            if (medicamentoRepository.existsByNombre(dto.getNombre()))
                throw new IllegalArgumentException("Ya existe un medicamento con el nombre: " + dto.getNombre());
        }

        mapActualizarToMedicamento(dto, medicamento);

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

    // --- Mapeos ---

    private void mapCrearToMedicamento(CrearMedicamentoRequest dto, Medicamento m) {
        m.setGtin(dto.getGtin());
        m.setNombre(dto.getNombre());
        m.setMonodroga(dto.getMonodroga());
        m.setLaboratorio(dto.getLaboratorio());
        m.setPresentacion(dto.getPresentacion());
        m.setDescripcion(dto.getDescripcion());
        m.setDetallesAdicionales(dto.getDetallesAdicionales());
        m.setCadenaFrio(Boolean.TRUE.equals(dto.getCadenaFrio()));
        m.setTemperaturaMinima(dto.getTemperaturaMinima());
        m.setTemperaturaMaxima(dto.getTemperaturaMaxima());
        m.setEsFragil(Boolean.TRUE.equals(dto.getEsFragil()));
        m.setEsControlado(Boolean.TRUE.equals(dto.getEsControlado()));
        m.setVolumen(dto.getVolumen());
        if (dto.getCantidad() != null)
            m.setCantidad(dto.getCantidad());
        m.setUnidadMedida(dto.getUnidadMedida());
        if (dto.getImagenUrl() != null)
            m.setImagenUrl(dto.getImagenUrl());
    }

    private void mapActualizarToMedicamento(ActualizarMedicamentoRequest dto, Medicamento m) {
        if (dto.tieneGtin())
            m.setGtin(dto.getGtin());
        if (dto.tieneNombre())
            m.setNombre(dto.getNombre());
        if (dto.tieneMonodroga())
            m.setMonodroga(dto.getMonodroga());
        if (dto.tieneLaboratorio())
            m.setLaboratorio(dto.getLaboratorio());
        if (dto.tienePresentacion())
            m.setPresentacion(dto.getPresentacion());
        if (dto.tieneDescripcion())
            m.setDescripcion(dto.getDescripcion());
        if (dto.tieneDetallesAdicionales())
            m.setDetallesAdicionales(dto.getDetallesAdicionales());
        if (dto.tieneCadenaFrio())
            m.setCadenaFrio(dto.getCadenaFrio());
        if (dto.tieneTemperaturaMinima())
            m.setTemperaturaMinima(dto.getTemperaturaMinima());
        if (dto.tieneTemperaturaMaxima())
            m.setTemperaturaMaxima(dto.getTemperaturaMaxima());
        if (dto.tieneEsFragil())
            m.setEsFragil(dto.getEsFragil());
        if (dto.tieneEsControlado())
            m.setEsControlado(dto.getEsControlado());
        if (dto.tieneVolumen())
            m.setVolumen(dto.getVolumen());
        if (dto.tieneCantidad())
            m.setCantidad(dto.getCantidad());
        if (dto.tieneUnidadMedida())
            m.setUnidadMedida(dto.getUnidadMedida());
        if (dto.tieneImagenUrl())
            m.setImagenUrl(dto.getImagenUrl());

        if (dto.tieneCadenaFrio() && !dto.getCadenaFrio()) {
            m.setTemperaturaMinima(null);
            m.setTemperaturaMaxima(null);
        }
    }

}