package com.meditrack.back.app.service;

import com.meditrack.back.app.model.DashboardKpiDTO;
import com.meditrack.back.app.model.Cliente;
import com.meditrack.back.app.model.Envio;
import com.meditrack.back.app.model.EstadoEnvio;
import com.meditrack.back.app.repository.ClienteRepository;
import com.meditrack.back.app.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;

    public DashboardService(EnvioRepository envioRepository, ClienteRepository clienteRepository) {
        this.envioRepository = envioRepository;
        this.clienteRepository = clienteRepository;
    }

    public DashboardKpiDTO obtenerMetricasDashboard() {
        String hoyStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long entregadosHoy = envioRepository.countByEstadoAndFechaCreacion(EstadoEnvio.ENTREGADO, hoyStr);
        
        List<Envio> todosLosEnvios = envioRepository.findAll();
        long totalEnvios = todosLosEnvios.size();
        long incidencias = todosLosEnvios.stream().filter(e -> e.getEstado() == EstadoEnvio.INCIDENTE_REPORTADO).count();
        
        double tasaIncidencias = 0.0;
        if (totalEnvios > 0) {
            tasaIncidencias = Math.round(((double) incidencias / totalEnvios) * 100.0 * 10.0) / 10.0;
        }

        long pendientesHoy = todosLosEnvios.stream().filter(e -> e.getEstado() == EstadoEnvio.PENDIENTE).count();
        long transitoHoy = todosLosEnvios.stream().filter(e -> e.getEstado() == EstadoEnvio.EN_TRANSITO).count();
        
        List<Map<String, Object>> volumenEnvios = new ArrayList<>();
        
        Map<String, Object> barraPendiente = new HashMap<>();
        barraPendiente.put("estado", "Pendiente");
        barraPendiente.put("cantidad", pendientesHoy);
        volumenEnvios.add(barraPendiente);

        Map<String, Object> barraTransito = new HashMap<>();
        barraTransito.put("estado", "En Tránsito");
        barraTransito.put("cantidad", transitoHoy);
        volumenEnvios.add(barraTransito);

        Map<String, Object> barraEntregado = new HashMap<>();
        barraEntregado.put("estado", "Entregado");
        barraEntregado.put("cantidad", entregadosHoy);
        volumenEnvios.add(barraEntregado);

        Map<String, String> mapaClientesTipos = clienteRepository.findAll().stream()
                .filter(c -> c.getTipoEstablecimiento() != null)
                .collect(Collectors.toMap(
                        Cliente::getNombre, 
                        c -> c.getTipoEstablecimiento().name(), 
                        (existente, reemplazo) -> existente
                ));

        String tipoMasRetira = todosLosEnvios.stream()
                .map(Envio::getRemitente)
                .map(r -> mapaClientesTipos.getOrDefault(r, "DESCONOCIDO"))
                .filter(t -> !t.equals("DESCONOCIDO"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Ninguno");

        String tipoMasRecibe = todosLosEnvios.stream()
                .map(Envio::getDestinatario)
                .map(d -> mapaClientesTipos.getOrDefault(d, "DESCONOCIDO"))
                .filter(t -> !t.equals("DESCONOCIDO"))
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("Ninguno");

        List<Map<String, Object>> top5Medicamentos = todosLosEnvios.stream()
                .flatMap(e -> e.getDetalles().stream())
                .filter(d -> d.getMedicamento() != null && d.getCantidad() != null)
                .collect(Collectors.groupingBy(d -> d.getMedicamento().getNombre(), Collectors.summingLong(d -> d.getCantidad())))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nombre", entry.getKey());
                    item.put("cantidad", entry.getValue());
                    return item;
                }).collect(Collectors.toList());

        Map<String, List<Envio>> enviosPorCliente = todosLosEnvios.stream()
                .filter(e -> e.getDestinatario() != null)
                .collect(Collectors.groupingBy(Envio::getDestinatario));

        long totalClientesUnicosPedidos = enviosPorCliente.keySet().size();

        List<Map<String, Object>> top3Clientes = enviosPorCliente.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()))
                .limit(3)
                .map(entry -> {
                    String clienteNombre = entry.getKey();
                    long cantidadPedidos = entry.getValue().size();
                    
                    double porcentaje = totalClientesUnicosPedidos > 0 
                            ? Math.round((1.0 / totalClientesUnicosPedidos) * 100.0 * 10.0) / 10.0 
                            : 0.0;

                    String medMasPedido = entry.getValue().stream()
                            .flatMap(e -> e.getDetalles().stream())
                            .filter(d -> d.getMedicamento() != null && d.getCantidad() != null)
                            .collect(Collectors.groupingBy(d -> d.getMedicamento().getNombre(), Collectors.summingLong(d -> d.getCantidad())))
                            .entrySet().stream().max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey).orElse("Ninguno");

                    Map<String, Object> item = new HashMap<>();
                    item.put("nombre", clienteNombre);
                    item.put("pedidos", cantidadPedidos);
                    item.put("porcentaje", porcentaje);
                    item.put("medicamentoTop", medMasPedido);
                    return item;
                }).collect(Collectors.toList());

        return new DashboardKpiDTO(volumenEnvios, entregadosHoy, tasaIncidencias, tipoMasRetira, tipoMasRecibe, top5Medicamentos, top3Clientes);
    }
}