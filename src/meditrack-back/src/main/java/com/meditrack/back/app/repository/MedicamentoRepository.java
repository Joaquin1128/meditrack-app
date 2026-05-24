package com.meditrack.back.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meditrack.back.app.model.Medicamento;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, String> {

    Optional<Medicamento> findByNombre(String nombre);
    Optional<Medicamento> findByGtin(String gtin);

    List<Medicamento> findByEstadoActivoTrue();

    List<Medicamento> findByCadenaFrioTrue();

    boolean existsByNombre(String nombre);
    boolean existsByGtin(String gtin);

    List<Medicamento> findByMonodroga(String monodroga);

    List<Medicamento> findByLaboratorio(String laboratorio);

}
