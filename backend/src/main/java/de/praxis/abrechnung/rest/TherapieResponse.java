package de.praxis.abrechnung.rest;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TherapieResponse {
  String buchungstag;
  String gebuchteZeit;
  String vorname;
  String name;
  String geburtsdatum;
  String strasse;
  String plz;
  String ort;
  Integer hausbesuch;
}
