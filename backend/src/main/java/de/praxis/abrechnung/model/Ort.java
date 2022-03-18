package de.praxis.abrechnung.model;

import lombok.Value;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Value
@Node("Ort")
public class Ort {

  @Id
  String plz;

  String ort;
}
