package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Therapie;
import de.praxis.abrechnung.model.TherapieDetails;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TherapieDetailRepository extends Neo4jRepository<TherapieDetails, String> {

}
