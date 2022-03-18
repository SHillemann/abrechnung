package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Vertrag;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface VertragRepository extends Neo4jRepository<Vertrag, String> {

}
