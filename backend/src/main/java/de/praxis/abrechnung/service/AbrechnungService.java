package de.praxis.abrechnung.service;

import de.praxis.abrechnung.model.Abwesenheit;
import de.praxis.abrechnung.model.Adresse;
import de.praxis.abrechnung.model.Fahrzeit;
import de.praxis.abrechnung.model.Ort;
import de.praxis.abrechnung.model.Person;
import de.praxis.abrechnung.model.Therapie;
import de.praxis.abrechnung.model.TherapieDetails;
import de.praxis.abrechnung.repository.AbwesenheitRepository;
import de.praxis.abrechnung.repository.AdressRepository;
import de.praxis.abrechnung.repository.FahrzeitRepository;
import de.praxis.abrechnung.repository.NutzerRepository;
import de.praxis.abrechnung.repository.OrtRepository;
import de.praxis.abrechnung.repository.PersonRepository;
import de.praxis.abrechnung.repository.TherapieDetailRepository;
import de.praxis.abrechnung.repository.TherapieRepository;
import de.praxis.abrechnung.rest.AbwesenheitRequest;
import de.praxis.abrechnung.rest.Buchungsblock;
import de.praxis.abrechnung.rest.Buchungstyp;
import de.praxis.abrechnung.rest.FahrtzeitRequest;
import de.praxis.abrechnung.rest.TagResponse;
import de.praxis.abrechnung.rest.TherapieRequest;
import de.praxis.abrechnung.rest.TherapieResponse;
import de.praxis.abrechnung.rest.UpdateTagRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class AbrechnungService {

  public static final DateTimeFormatter DATE_FORMATTER_BUCHUNGSTAG = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd");
  public static final DateTimeFormatter DATE_FORMATTER_GEBURTSTAG = DateTimeFormatter.ofPattern(
      "dd.MM.yyyy");
  private final TherapieRepository therapieRepository;
  private final FahrzeitRepository fahrzeitRepository;
  private final AbwesenheitRepository abwesenheitRepository;
  private final PersonRepository personRepository;
  private final AdressRepository adressRepository;
  private final OrtRepository ortRepository;
  private final NutzerRepository nutzerRepository;
  private final TherapieDetailRepository therapieDetailRepository;



  public List<TagResponse> getDayByName(String name, Long date) {

    var localdate = LocalDate.ofInstant(Instant.ofEpochMilli( date ), ZoneId.systemDefault());
    var startDate = localdate.with(WeekFields.of(Locale.GERMANY).dayOfWeek(), 1L);
    var week = createOneWeek(startDate);
    var therapie = therapieRepository.findAllByDate(name, startDate, startDate.plusDays(6L));
    var fahrzeit = fahrzeitRepository.findAllByUserAndDate(name, startDate, startDate.plusDays(6L));
    var abwesenheit = abwesenheitRepository.findByUserAndDate(name, startDate,
        startDate.plusDays(6L));
    var buchungsblock = mergeAndConvertToBuchung(therapie, fahrzeit, abwesenheit);
    var tage = bookedWeeks(buchungsblock);

    if (tage.size() < 7) {
      for (var tag : week) {
        if (tage.stream().noneMatch(x -> x.getBuchungstag().isEqual(tag.getBuchungstag()))) {
          tage.add(tag);
        }
      }
    }
    tage.sort(Comparator.comparing(TagResponse::getBuchungstag));
    return tage;
  }

  private List<Buchungsblock> mergeAndConvertToBuchung(List<Therapie> therapie,
      List<Fahrzeit> fahrzeit, List<Abwesenheit> abwesenheit) {
    ArrayList<Buchungsblock> result = new ArrayList<>();
    if(therapie != null) {
      result.addAll(therapie.stream().map(
          (buchung) -> Buchungsblock.builder().buchungstyp(Buchungstyp.THERAPIE).id(buchung.getTherapieDetails().getId())
              .buchungstag(buchung.getTherapieDetails().getBuchungsdatum())
              .reihenfolge(buchung.getTherapieDetails().getReihenfolge())
              .titel("<b>"+buchung.getPatient().getName()+"</b>, "+buchung.getPatient().getVorname() +"<br/>"
                  + toMinutesLabel(buchung.getTherapieDetails().getGebucht()))
              .hausbesuch(buchung.getTherapieDetails().getHausbesuch())
              .parentId(buchung.getId())
              .build()).toList());
    }
    if(fahrzeit != null ) {
      result.addAll(fahrzeit.stream().map(
              (buchung) -> Buchungsblock.builder().buchungstyp(Buchungstyp.FAHRZEIT).id(buchung.getId())
                  .buchungstag(buchung.getBuchungsdatum()).reihenfolge(buchung.getReihenfolge())
                  .titel(buchung.getNachAdresse().getStrasse() + " " + buchung.getGebucht()).build())
          .toList());
    }
    if(abwesenheit != null) {
      result.addAll(abwesenheit.stream().map(
              (buchung) -> Buchungsblock.builder().buchungstyp(Buchungstyp.ABWESENHEIT)
                  .id(buchung.getId()).buchungstag(buchung.getBuchungsdatum())
                  .reihenfolge(buchung.getReihenfolge())
                  .titel("<b>Nothing</b><br/>8h").build())
          .toList());
    }
    result.sort(Comparator.comparing(Buchungsblock::getReihenfolge));
    return result;
  }

  private String toMinutesLabel(BigDecimal gebucht) {
    return BigDecimal.valueOf(60).multiply(gebucht).setScale(0, RoundingMode.UP) +" min";
  }

  private List<TagResponse> bookedWeeks(List<Buchungsblock> buchungsblocks) {
    var result = new HashMap<LocalDate, TagResponse>();
    for (var zeitblock : buchungsblocks) {
      result.merge(zeitblock.getBuchungstag(), TagResponse.builder().tag(
                  zeitblock.getBuchungstag().getDayOfWeek()
                      .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("de-DE")))
              .buchungstag(zeitblock.getBuchungstag()).buchungsblock(zeitblock).build(),
          (oldTag, newTag) -> TagResponse.builder().tag(zeitblock.getBuchungstag().getDayOfWeek()
                  .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("de-DE")))
              .buchungstag(zeitblock.getBuchungstag()).buchungsblocks(oldTag.getBuchungsblocks())
              .buchungsblock(zeitblock).build());
    }
    return new ArrayList<>(result.values());
  }

  private List<TagResponse> createOneWeek(LocalDate startDate) {
    var result = new ArrayList<TagResponse>();
    for (int i = 0; i < 7; i++) {
      result.add(TagResponse.of(startDate.plusDays(i)));
    }
    return result;
  }

  public List<Person> getSurname(String surname) {
    return personRepository.findByPartialName(surname);
  }
  public Buchungsblock therapiePersist(TherapieRequest request) {
    var gebucht = TherapieDetails.converterToRepoGebucht(request.getGebucht());
    AtomicReference<Therapie> therapie = new AtomicReference<>();
    nutzerRepository.findById("admin").ifPresent((therapeut) -> {
      var ort = ortRepository.findById(request.getPatient_plz())
          .orElse(new Ort(request.getPatient_plz(), request.getPatient_ort()));

      var adresse = adressRepository.findAdresse(request.getPatient_strasse(),
              request.getPatient_plz(), request.getPatient_ort())
          .orElse(new Adresse(request.getPatient_strasse(), ort));

      var patient_geburtsdatum = LocalDate.parse(request.getPatient_geburtstag(),
          DATE_FORMATTER_GEBURTSTAG);
      var patient = personRepository.findPerson(request.getPatient_vorname(),
          request.getPatient_name(), patient_geburtsdatum).orElse(
          Person.builder().vorname(request.getPatient_vorname()).name(request.getPatient_name())
              .geburtstag(patient_geburtsdatum).adress(adresse).build());

      if (!patient.getAdresses().contains(adresse)) {
        patient.addAdress(adresse);
      }

      therapie.set(Therapie.builder()
          .patient(patient)
          .adresse(adresse)
          .therapieDetails(
              TherapieDetails.builder().buchungsdatum(request.getBuchungstag())
                  .gebucht(gebucht)
                  .reihenfolge(request.getReihenfolge())
                  .hausbesuch(request.getHausbesuch())
                  .build())
          .therapeut(therapeut).build());

      therapieRepository.save(therapie.get());
    });
    return mergeAndConvertToBuchung(List.of(therapie.get()),null,null).get(0);
  }

  public Integer therapieDelete(String id) {

    var detail = therapieRepository.findById(id).map(Therapie::getTherapieDetails).orElseThrow();
    var reihenfolge = detail.getReihenfolge();
    therapieDetailRepository.deleteById(detail.getId());
    therapieRepository.deleteById(id);
    return reihenfolge;
  }


  public void updateTag(UpdateTagRequest request) {
    var reihenfolge = new AtomicInteger();
    var date = LocalDate.parse(request.getBuchungstag(), DATE_FORMATTER_BUCHUNGSTAG);
    var list = request.getItems().stream().map(
        (item)->{
         var therapieDetail = therapieDetailRepository.findById(item.getId());
          therapieDetail.ifPresent(
             (detail)->{
               detail.setBuchungsdatum(date);
               detail.setReihenfolge(reihenfolge.getAndIncrement());
             }
         );
          return therapieDetail.orElseThrow();
        }).toList();
    therapieDetailRepository.saveAll(list);
  }
  public void fahrtzeitPersist(FahrtzeitRequest request) {
    nutzerRepository.findById("admin").ifPresent((therapeut) -> {

      var von_adresse = adressRepository.findAdresse(request.getVon_strasse(), request.getVon_plz(),
          request.getVon_ort()).orElse(new Adresse(request.getVon_strasse(),
          ortRepository.findById(request.getVon_plz())
              .orElse(new Ort(request.getVon_plz(), request.getVon_ort()))));

      var nach_adresse = adressRepository.findAdresse(request.getNach_strasse(),
          request.getNach_plz(), request.getNach_ort()).orElse(
          new Adresse(request.getNach_strasse(), ortRepository.findById(request.getNach_plz())
              .orElse(new Ort(request.getNach_plz(), request.getNach_ort()))));

      var fahrtzeit = Fahrzeit.builder().buchungsdatum(request.getBuchungstag())
          .vonAdresse(von_adresse).nachAdresse(nach_adresse).gebucht(new BigDecimal("2"))
          .praxisauto(false).reihenfolge(request.getReihenfolge()).therapeut(therapeut).build();
      fahrzeitRepository.save(fahrtzeit);
    });
  }

  public Buchungsblock abwesenheitPersist(AbwesenheitRequest request) {
    var abwesenheit = new AtomicReference<Abwesenheit>();
    nutzerRepository.findById("admin").ifPresent((therapeut) -> {
      var gebucht = "0";
      if (request.getAbwesenheit() == 1) {
        gebucht = "8";
      }
      if (request.getAbwesenheit() == 2) {
        gebucht = "4";
      }
      if (request.getAbwesenheit() == 3) {
        gebucht = "8";
      }
      abwesenheit.set(Abwesenheit.builder().buchungsdatum(request.getBuchungsdatum())
          .reihenfolge(request.getReihenfolge()).therapeut(therapeut)
          .abwesenheit(request.getAbwesenheit()).gebucht(new BigDecimal(gebucht)).build());
      abwesenheitRepository.save(abwesenheit.get());
    });
    return mergeAndConvertToBuchung(null,null, List.of(abwesenheit.get())).get(0);
  }


  public TherapieResponse getTherapieById(String id) {
    var optTherapie = therapieRepository.findById(id);
    if(optTherapie.isPresent())
    {
      var therapie = optTherapie.get();
      return TherapieResponse.builder()
          .buchungstag(therapie.getTherapieDetails().getBuchungsdatum().format(DATE_FORMATTER_BUCHUNGSTAG))
          .gebuchteZeit(BigDecimal.valueOf(60).multiply(therapie.getTherapieDetails().getGebucht()).setScale(0, RoundingMode.UP)+"m")
          .vorname(therapie.getPatient().getVorname())
          .name(therapie.getPatient().getName())
          .geburtsdatum(therapie.getPatient().getGeburtstag().format(DATE_FORMATTER_GEBURTSTAG))
          .strasse(therapie.getAdresse().getStrasse())
          .plz(therapie.getAdresse().getOrt().getPlz())
          .ort(therapie.getAdresse().getOrt().getOrt())
          .hausbesuch(therapie.getTherapieDetails().getHausbesuch()).build();
    }
    throw new RuntimeException("no id found");
  }
}
