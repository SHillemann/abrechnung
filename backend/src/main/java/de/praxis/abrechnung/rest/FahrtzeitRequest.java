package de.praxis.abrechnung.rest;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FahrtzeitRequest {

  LocalDate buchungstag;
  Integer reihenfolge;
  String von_strasse;
  String von_ort;
  String von_plz;
  String nach_strasse;
  String nach_ort;
  String nach_plz;

}
