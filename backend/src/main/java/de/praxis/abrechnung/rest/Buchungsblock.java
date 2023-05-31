package de.praxis.abrechnung.rest;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;


@Builder
@Value
public class Buchungsblock {
  String id;
  LocalDate buchungstag;
  Buchungstyp buchungstyp;
  String titel;
  Integer reihenfolge;
  Integer hausbesuch;

  String parentId;
}
