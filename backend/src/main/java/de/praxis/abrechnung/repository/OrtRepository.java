package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Ort;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface OrtRepository extends Neo4jRepository<Ort, String> {
 }
