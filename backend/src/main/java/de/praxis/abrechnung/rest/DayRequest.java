package de.praxis.abrechnung.rest;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class DayRequest {

  @NonNull
  String name;
  @NonNull
  Long date;
}
