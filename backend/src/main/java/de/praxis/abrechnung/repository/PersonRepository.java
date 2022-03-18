package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Person;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends Neo4jRepository<Person, String> {

  List<Person> findByName(@Param("name") String name);

  @Query("MATCH(p:Person) WHERE p.name =~ '(?i).*'+$find+'.*' OR p.vorname =~ '(?i).*'+$find+'.*' "
      + "OPTIONAL MATCH(p) -[wr:WOHNT_IN]-(a:Adresse) "
      + "OPTIONAL MATCH(a) -[or:IN_ORT]-(o:Ort) "
      + "RETURN p,collect(wr),collect(a),collect(or),collect(o)")
  List<Person> findByPartialName(@Param("find") String find);

  @Query("MATCH(p:Person) WHERE p.name = $name AND p.vorname = $vorname AND p.geburtstag = $geb  OPTIONAL MATCH(p) -[wr:WOHNT_IN]-(a:Adresse) OPTIONAL MATCH(a) -[or:IN_ORT]-(o:Ort) RETURN p,collect(wr),collect(a),collect(or),collect(o)")
  Optional<Person> findPerson(@Param("vorname") String patient_vorname,@Param("name") String patient_name,@Param("geb") LocalDate patient_geburtstag);
}
