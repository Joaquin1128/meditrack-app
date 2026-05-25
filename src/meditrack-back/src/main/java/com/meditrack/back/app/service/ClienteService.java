package com.meditrack.back.app.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meditrack.back.app.dto.ActualizarClienteRequest;
import com.meditrack.back.app.dto.CrearClienteRequest;

import com.meditrack.back.app.model.Cliente;
import com.meditrack.back.app.model.TipoEstablecimiento;
import com.meditrack.back.app.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente obtenerPorId(String id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Cliente crear(CrearClienteRequest datos, String usuario) {
        Cliente nuevo = new Cliente();
        nuevo.setNombre(datos.getNombre().trim());
        nuevo.setCuit(datos.getCuit().trim());
        nuevo.setGln(datos.getGln().trim());
        nuevo.setTelefono(datos.getTelefono().trim());
        nuevo.setEmail(datos.getEmail().trim());
        nuevo.setDireccion(datos.getDireccion().trim());
        nuevo.setTipoEstablecimiento(TipoEstablecimiento.valueOf(datos.getTipoEstablecimiento()));
        nuevo.setUsuarioResponsable(usuario);
        if (datos.getLatitud() != null)
            nuevo.setLatitud(BigDecimal.valueOf(datos.getLatitud()));
        if (datos.getLongitud() != null)
            nuevo.setLongitud(BigDecimal.valueOf(datos.getLongitud()));
        if (datos.getPlaceId() != null)
            nuevo.setPlaceId(datos.getPlaceId());
        return clienteRepository.save(nuevo);
    }

    public Cliente actualizar(String id, ActualizarClienteRequest datos, String usuario) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (datos.tieneNombre())
            cliente.setNombre(datos.getNombre().trim());
        if (datos.tieneCuit())
            cliente.setCuit(datos.getCuit().trim());
        if (datos.tieneGln())
            cliente.setGln(datos.getGln().trim());
        if (datos.tieneTelefono())
            cliente.setTelefono(datos.getTelefono().trim());
        if (datos.tieneEmail())
            cliente.setEmail(datos.getEmail().trim());
        if (datos.tieneDireccion())
            cliente.setDireccion(datos.getDireccion().trim());
        if (datos.tieneTipoEstablecimiento())
            cliente.setTipoEstablecimiento(TipoEstablecimiento.valueOf(datos.getTipoEstablecimiento()));
        if (datos.getLatitud() != null)
            cliente.setLatitud(BigDecimal.valueOf(datos.getLatitud()));
        if (datos.getLongitud() != null)
            cliente.setLongitud(BigDecimal.valueOf(datos.getLongitud()));
        if (datos.getPlaceId() != null)
            cliente.setPlaceId(datos.getPlaceId());
        cliente.setUsuarioResponsable(usuario);

        return clienteRepository.save(cliente);
    }

    public Cliente cambiarEstado(String id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.setEstadoActivo(!cliente.isEstadoActivo());
        return clienteRepository.save(cliente);
    }
    
}