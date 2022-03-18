package de.praxis.abrechnung.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

/**
 * Arbeitzeit
 */
@Data
@Node("Vertrag")
public class Vertrag {

  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  String id;
  @NonNull
  LocalDate geltenAb;
  @NonNull
  Integer stundenWoche;
  @NonNull
  Integer tageProWoche;
  @NonNull
  Integer urlaubsanspruch;

  @Relationship(type = "HAT_VERTRAG", direction = Direction.INCOMING)
  @NonNull
  Nutzer nutzer;
}

