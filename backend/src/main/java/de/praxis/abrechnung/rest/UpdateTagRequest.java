package de.praxis.abrechnung.rest;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateTagRequest {

  String buchungstag;
  List<Buchungsblock> items;

}
