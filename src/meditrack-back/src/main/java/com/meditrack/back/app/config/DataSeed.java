package com.meditrack.back.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.meditrack.back.app.model.Medicamento;
import com.meditrack.back.app.model.Role;
import com.meditrack.back.app.model.Usuario;
import com.meditrack.back.app.repository.MedicamentoRepository;
import com.meditrack.back.app.repository.UsuarioRepository;

@Component
public class DataSeed implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;

    public DataSeed(UsuarioRepository usuarioRepository, MedicamentoRepository medicamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        generateUsers();
        generateMedicamentos();
    }

    private void generateUsers() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("admin@meditrack.com", "Admin Principal", "12156236", "admin123",
                    Role.ADMINISTRADOR);
            Usuario supervisor = new Usuario("supervisor@meditrack.com", "Admin MediTrack", "30156256", "1234",
                    Role.SUPERVISOR);
            Usuario operador = new Usuario("operador@meditrack.com", "Carlos Ruiz", "42156236", "1234", Role.OPERADOR);
            Usuario repartidor = new Usuario("repartidor@meditrack.com", "Diego Torres", "41156236", "1234",
                    Role.REPARTIDOR);

            usuarioRepository.save(admin);
            usuarioRepository.save(supervisor);
            usuarioRepository.save(operador);
            usuarioRepository.save(repartidor);

            System.out.println("✅ Usuarios de prueba sembrados en la base de datos con éxito.");
        } else {
            System.out.println("ℹ️ La base de datos ya tiene usuarios. Se omite el sembrado.");
        }
    }

    private void generateMedicamentos() {
        if (medicamentoRepository.count() == 0) {

            // Ibuprofeno
            Medicamento ibuprofeno = new Medicamento();
            ibuprofeno.setGtin("07790001000011");
            ibuprofeno.setNombre("Ibuprofeno 400mg");
            ibuprofeno.setMonodroga("Ibuprofeno");
            ibuprofeno.setLaboratorio("Bayer");
            ibuprofeno.setPresentacion("Comprimidos");
            ibuprofeno.setDescripcion("Antiinflamatorio no esteroideo");
            ibuprofeno.setCantidad(500);
            ibuprofeno.setUnidadMedida("mg");
            ibuprofeno.setCadenaFrio(false);
            ibuprofeno.setEsFragil(false);
            ibuprofeno.setEsControlado(false);
            ibuprofeno.setVolumen(120.0);
            ibuprofeno.setImagenUrl("/uploads/6a84ff89-02b6-49a4-b722-5a55a3a7b175_actron_400_1.jpg");

            Medicamento amoxicilina = new Medicamento();
            amoxicilina.setGtin("07790001000022");
            amoxicilina.setNombre("Amoxicilina 500mg");
            amoxicilina.setMonodroga("Amoxicilina");
            amoxicilina.setLaboratorio("Pfizer");
            amoxicilina.setPresentacion("Cápsulas");
            amoxicilina.setDescripcion("Antibiótico de amplio espectro");
            amoxicilina.setCantidad(200);
            amoxicilina.setUnidadMedida("mg");
            amoxicilina.setCadenaFrio(false);
            amoxicilina.setEsFragil(false);
            amoxicilina.setEsControlado(false);
            amoxicilina.setVolumen(80.0);
            amoxicilina.setImagenUrl("/uploads/c3521887-b0a1-4af3-918a-4b1a22a28108_F_000001106329.jpg");

            Medicamento insulina = new Medicamento();
            insulina.setGtin("07790001000033");
            insulina.setNombre("Insulina Glargina 100 UI/ml");
            insulina.setMonodroga("Insulina glargina");
            insulina.setLaboratorio("Sanofi");
            insulina.setPresentacion("Solución oral");
            insulina.setDescripcion("Insulina de acción prolongada");
            insulina.setCantidad(100);
            insulina.setUnidadMedida("UI/ml");
            insulina.setCadenaFrio(true);
            insulina.setTemperaturaMinima(2.0);
            insulina.setTemperaturaMaxima(8.0);
            insulina.setEsFragil(true);
            insulina.setEsControlado(false);
            insulina.setVolumen(50.0);

            medicamentoRepository.save(ibuprofeno);
            medicamentoRepository.save(amoxicilina);
            medicamentoRepository.save(insulina);

            System.out.println("✅ Medicamentos de prueba sembrados con éxito.");
        } else {
            System.out.println("ℹ️ La base de datos ya tiene medicamentos. Se omite el sembrado.");
        }
    }

}
