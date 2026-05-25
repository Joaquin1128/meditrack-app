package com.meditrack.back.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.meditrack.back.app.model.Mail;
import com.meditrack.back.app.model.Medicamento;
import com.meditrack.back.app.model.Role;
import com.meditrack.back.app.model.Usuario;
import com.meditrack.back.app.repository.MailRepository;
import com.meditrack.back.app.repository.MedicamentoRepository;
import com.meditrack.back.app.repository.UsuarioRepository;

@Component
public class DataSeed implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final MailRepository mailRepository;

    public DataSeed(UsuarioRepository usuarioRepository, MedicamentoRepository medicamentoRepository, MailRepository mailRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.mailRepository = mailRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        generateUsers();
        generateMedicamentos();
        generateMails();
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

    private void generateMails() {
        if (mailRepository.count() == 0) {
            Mail mail1 = new Mail();

            mail1.setId("MAIL001");
            mail1.setAsunto("Nuevo envío registrado");
            mail1.setRemitente("sistema@meditrack.com");
            mail1.setDestinatario("cliente@hospital.com");
            mail1.setContenido("El envío ENV-0001 fue registrado correctamente en el sistema.");
            mail1.setEstado("Enviado");
            mail1.setFechaCreacion("2026-05-25");
            mail1.setFechaEnvio("2026-05-25 10:30");
            mail1.setHoraCreacion("10:30");
            mail1.setUsuarioCreacion("Admin MediTrack");

            Mail mail2 = new Mail();

            mail2.setId("MAIL002");
            mail2.setAsunto("Incidente reportado");
            mail2.setRemitente("incidentes@meditrack.com");
            mail2.setDestinatario("admin@meditrack.com");
            mail2.setContenido("Se reportó un incidente durante la entrega del envío ENV-0002.");
            mail2.setEstado("Pendiente");
            mail2.setFechaCreacion("2026-05-25");
            mail2.setFechaEnvio("2026-05-25 11:45");
            mail2.setHoraCreacion("11:45");
            mail2.setUsuarioCreacion("Carlos Ruiz");

            Mail mail3 = new Mail();

            mail3.setId("MAIL003");
            mail3.setAsunto("Entrega completada");
            mail3.setRemitente("logistica@meditrack.com");
            mail3.setDestinatario("farmacia@hospital.com");
            mail3.setContenido("La entrega del envío ENV-0003 fue completada exitosamente.");
            mail3.setEstado("Error");
            mail3.setFechaCreacion("2026-05-24");
            mail3.setFechaEnvio("2026-05-24 16:20");
            mail3.setHoraCreacion("16:20");
            mail3.setUsuarioCreacion("Diego Torres");

            mailRepository.save(mail1);
            mailRepository.save(mail2);
            mailRepository.save(mail3);

            System.out.println("✅ Mails de prueba sembrados con éxito.");

        } else {
            System.out.println("ℹ️ La base de datos ya tiene mails. Se omite el sembrado.");
        }
    }

}
