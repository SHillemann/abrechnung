package de.praxis.abrechnung.rest;

import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AbwesenheitRequest {

  @NotNull
  LocalDate buchungsdatum;
  @Min(value = 0, message = "The value must be positive")
  Integer reihenfolge;
  @NotNull
  Integer abwesenheit;
}
