package de.praxis.abrechnung;

import de.praxis.abrechnung.model.Abwesenheit;
import de.praxis.abrechnung.model.Adresse;
import de.praxis.abrechnung.model.Fahrzeit;
import de.praxis.abrechnung.model.Nutzer;
import de.praxis.abrechnung.model.NutzerRolle;
import de.praxis.abrechnung.model.Ort;
import de.praxis.abrechnung.model.Person;
import de.praxis.abrechnung.model.Therapie;
import de.praxis.abrechnung.model.TherapieDetails;
import de.praxis.abrechnung.model.Vertrag;
import de.praxis.abrechnung.repository.AbwesenheitRepository;
import de.praxis.abrechnung.repository.AdressRepository;
import de.praxis.abrechnung.repository.FahrzeitRepository;
import de.praxis.abrechnung.repository.NutzerRepository;
import de.praxis.abrechnung.repository.PersonRepository;
import de.praxis.abrechnung.repository.TherapieRepository;
import de.praxis.abrechnung.repository.VertragRepository;
import de.praxis.abrechnung.service.AbrechnungService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class AbrechnungApplicationTests {

  @Autowired
  private TherapieRepository therapieRepository;
  @Autowired
  private FahrzeitRepository fahrzeitRepository;
  @Autowired
  private AbwesenheitRepository abwesenheitRepository;
  @Autowired
  private VertragRepository vertragRepository;
  @Autowired
  private PersonRepository personRepository;
  @Autowired
  private AdressRepository adressRepository;
  @Autowired
  private AbrechnungService abrechnungService;
  @Autowired
  private NutzerRepository nutzerRepository;


  @Test
  void contextLoads() {
  }
  @Test
  void writeAdresse() {
    var halle = new Ort("06108", "Halle (Saale)");
    var adresse1 = new Adresse("Brüderstr. 13", halle);
    var adresse2 = new Adresse("Kleine Marktstr. 4", halle);
    var adresse3 = new Adresse("Salzstr. 15", halle);

    adressRepository.save(adresse1);
    adressRepository.save(adresse2);
    adressRepository.save(adresse3);
  }



  @Test
  void writeNutzer() {
    var netti = Person.builder()
        .vorname("Jeanette")
        .name("Engel")
        .geburtstag(LocalDate.of(1987, 1, 29)).build();
    var admin = new Nutzer("admin", "password", NutzerRolle.THERAPEUT, netti);
    var vertrag = new Vertrag(LocalDate.of(2000, 1, 29), 30, 5, 24, admin);
    nutzerRepository.save(admin);
    vertragRepository.save(vertrag);
  }

  @Test
  void deleteVertrag() {
    var vertrag = vertragRepository.findById("43d30d4a-d287-4455-98b4-7a745cf4f9d8");
    vertrag.ifPresent(vertragRepository::delete);
  }
  @Test
  void writePatient() {
    var patient = Person.builder()
        .vorname("Sirko")
        .name("Hillemann")
        .geburtstag(LocalDate.of(1985, 8, 22));
    adressRepository.findAdresse("Brüderstr. 13", "06108", "Halle (Saale)")
        .ifPresent(patient::adress);
    adressRepository.findAdresse("Salzstr. 15", "06108", "Halle (Saale)")
        .ifPresent(patient::adress);
    personRepository.save(patient.build());
  }

  @Test
  void writeToNeo4j() {
    nutzerRepository.findById("admin").ifPresent((therapeut) -> {

      var patient = personRepository.findByName("Hillemann").get(0);

      var adresse1 = adressRepository.findAdresse("Brüderstr. 13", "06108", "Halle (Saale)")
          .orElse(null);
      var adresse2 = adressRepository.findAdresse("Kleine Marktstr. 4", "06108", "Halle (Saale)")
          .orElse(null);
      var adresse3 = adressRepository.findAdresse("Salzstr. 15", "06108", "Halle (Saale)")
          .orElse(null);

      var therapie1 = Therapie.builder()
          .patient(patient)
          .adresse(adresse1)
          .therapieDetails(
              TherapieDetails.builder().buchungsdatum(LocalDate.of(2022, 3, 30))
              .gebucht(new BigDecimal("0.75"))
              .beschreibung("Therapie")
              .reihenfolge(0).build())
      .therapeut(therapeut).build();

      var therapie2 = Therapie.builder()
          .patient(patient)
          .adresse(adresse1)
          .therapieDetails(
              TherapieDetails.builder().buchungsdatum(LocalDate.of(2022, 3, 30))
                  .gebucht(new BigDecimal("0.5"))
                  .beschreibung("Therapie")
                  .reihenfolge(1).build())
          .therapeut(therapeut).build();

      var therapie3 = Therapie.builder()
          .patient(patient)
          .adresse(adresse1)
          .therapieDetails(
              TherapieDetails.builder().buchungsdatum(LocalDate.of(2022, 3, 28))
                  .gebucht(new BigDecimal("0.5"))
                  .beschreibung("Therapie")
                  .reihenfolge(0).build())
          .therapeut(therapeut).build();

      var fahrzeit = Fahrzeit.builder().buchungsdatum(LocalDate.of(2022, 3, 30))
          .gebucht(new BigDecimal("0.1")).reihenfolge(2).therapeut(therapeut).vonAdresse(adresse2)
          .nachAdresse(adresse1).praxisauto(false).build();
/*
      var abwesenheit = Abwesenheit.builder().buchungsdatum(LocalDate.of(2022, 3, 29))
          .gebucht(new BigDecimal("8")).reihenfolge(0).therapeut(therapeut)
          .abwesenheit(Abwesenheittyp.URLAUB).build();
*/
      therapieRepository.save(therapie1);
      therapieRepository.save(therapie2);
      therapieRepository.save(therapie3);
      fahrzeitRepository.save(fahrzeit);
     // abwesenheitRepository.save(abwesenheit);
    });
  }
  @Test
  void loadTest(){
    var therapie = therapieRepository.findAllByDate("admin",LocalDate.of(2022,3,28),LocalDate.of(2022,4,3));
    var fahrzeit = fahrzeitRepository.findAllByUserAndDate("admin",LocalDate.of(2022,3,28),LocalDate.of(2022,4,3));
    var abesenheit = abwesenheitRepository.findByUserAndDate("admin",LocalDate.of(2022,3,28),LocalDate.of(2022,4,3));
    System.out.println(therapie);
  }
  @Test
  void rebuild() {
    writeNutzer();
    writeAdresse();
    writePatient();
    writeToNeo4j();
  }
}
