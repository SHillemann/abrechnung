package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Nutzer;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface NutzerRepository extends Neo4jRepository<Nutzer, String> {

  @Query("MATCH (n:Nutzer) WHERE n.nutzerName = $name AND n.password = $password RETURN n")
  Nutzer getNutzer(@Param("name")String name,@Param("password") String password);
}
