package com.meditrack.back.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.meditrack.back.app.model.Role;
import com.meditrack.back.app.model.Usuario;
import com.meditrack.back.app.repository.UsuarioRepository;

@Component
public class DataSeed implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DataSeed(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("admin@meditrack.com", "Admin Principal", "12156236", "admin123", Role.ADMINISTRADOR);
            Usuario supervisor = new Usuario("supervisor@meditrack.com", "Admin MediTrack", "30156256", "1234", Role.SUPERVISOR);
            Usuario operador = new Usuario("operador@meditrack.com", "Carlos Ruiz", "42156236", "1234", Role.OPERADOR);
            Usuario repartidor = new Usuario("repartidor@meditrack.com", "Diego Torres", "41156236", "1234", Role.REPARTIDOR);

            usuarioRepository.save(admin);
            usuarioRepository.save(supervisor);
            usuarioRepository.save(operador);
            usuarioRepository.save(repartidor);

            System.out.println("✅ Usuarios de prueba sembrados en la base de datos con éxito.");
        } else {
            System.out.println("ℹ️ La base de datos ya tiene usuarios. Se omite el sembrado.");
        }
    }
}