package de.praxis.abrechnung.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Data
@Node("Adresse")
public class Adresse {

  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  String id;
  @NonNull
  private String strasse;

  private String beschreibung;

  @NonNull
  @Relationship(type = "IN_ORT", direction = Direction.OUTGOING)
  private Ort ort;
}
