package de.praxis.abrechnung.model;


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
@Node("Therapie")
public class Therapie {

  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  private String id;
  @NonNull
  @Relationship(type = "THERAPEUT", direction = Direction.INCOMING)
  private Nutzer therapeut;

  @NonNull
  @Relationship(type = "PATIENT", direction = Direction.INCOMING)
  private Person patient;

  @NonNull
  @Relationship(type = "THERAPIE_IN", direction = Direction.OUTGOING)
  Adresse adresse;

  @NonNull
  @Relationship(type = "THERAPIE_DETAILS", direction = Direction.INCOMING)
  TherapieDetails therapieDetails;

}
