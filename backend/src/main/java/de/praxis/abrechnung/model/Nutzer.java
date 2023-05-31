package de.praxis.abrechnung.model;

import lombok.Value;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

@Value
@Node("Nutzer")
public class Nutzer {

  @Id
  String nutzerName;

  String password;

  NutzerRolle nutzerRolle;

  @Relationship(type = "IST_PERSON", direction = Direction.OUTGOING)
  Person person;
}
