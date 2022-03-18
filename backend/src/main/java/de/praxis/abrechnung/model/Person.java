package de.praxis.abrechnung.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

/**
 * Ein Therapeut
 */
@Data
@Builder
@Node("Person")
public class Person {

  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  String id;

  @NonNull
  String vorname;
  @NonNull
  String name;
  @NonNull
  LocalDate geburtstag;

  @Singular
  @Relationship(type = "WOHNT_IN", direction = Direction.OUTGOING)
  List<Adresse> adresses;

  public void addAdress(Adresse adresse)
  {
    adresses.add(adresse);
  }
}
