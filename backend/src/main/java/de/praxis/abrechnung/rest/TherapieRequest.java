package de.praxis.abrechnung.rest;

import java.time.LocalDate;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class TherapieRequest {

  String id;
  @NotNull
  LocalDate buchungstag;
  @Pattern(regexp = "^(\\d{1,3}[\\.,])?[1-9]{1,3}\\s*[HhMm]?\\s*\\d{0,3}\\s*[HhMm]?$", message = "Sollte gültige Zeit sein 2h 45m, 0,75, 0.75 45 od 45m")
  @NotBlank(message = "darf nicht leer sein")
  String gebucht;
  @Min(value = 0, message = "The value must be positive")
  Integer reihenfolge;
  @NotBlank
  String therapeut;
  @NotBlank(message = "darf nicht leer sein")
  String patient_name;
  @NotBlank(message = "darf nicht leer sein")
  String patient_vorname;
  @NotBlank(message = "darf nicht leer sein")
  @Pattern(regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}$", message = "Muss im Format dd.mm.yyyy ISO 99999 sein.")
  String patient_geburtstag;
  @NotBlank(message = "darf nicht leer sein")
  String patient_strasse;
  @NotBlank(message = "darf nicht leer sein")
  String patient_ort;
  @NotBlank(message = "darf nicht leer sein")
  String patient_plz;
  @NotNull
  Integer hausbesuch;
}