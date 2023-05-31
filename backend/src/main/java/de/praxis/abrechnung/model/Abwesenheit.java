package de.praxis.abrechnung.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Data
@Builder
@Node("Abwesenheit")
public class Abwesenheit {

  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  private String id;

  @NonNull
  private LocalDate buchungsdatum;

  @NonNull
  private BigDecimal gebucht;

  @NonNull
  private Integer reihenfolge;

  @NonNull
  @Relationship(type = "THERAPEUT", direction = Direction.INCOMING)
  private Nutzer therapeut;

  private Integer abwesenheit;
}
