package de.praxis.abrechnung.rest;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class TagResponse {

  LocalDate buchungstag;

  String tag;

  @Singular
  List<Buchungsblock> buchungsblocks;

  public static TagResponse of(LocalDate days)
  {
    return new TagResponse(days,days.getDayOfWeek().getDisplayName(
        TextStyle.FULL,
        Locale.forLanguageTag("de-DE")),new ArrayList<>());
  }
}
