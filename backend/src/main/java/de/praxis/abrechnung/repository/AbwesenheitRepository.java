package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Abwesenheit;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface AbwesenheitRepository extends Neo4jRepository<Abwesenheit, String> {
  @Query("MATCH(a:Abwesenheit) -[tr:THERAPEUT]-(n:Nutzer{nutzerName: $user}) "
      + "WHERE a.buchungsdatum >= $start AND a.buchungsdatum <= $end "
      + "RETURN a,collect(tr),collect(n)")
  List<Abwesenheit> findByUserAndDate(@Param("user") String user,@Param("start") LocalDate start,@Param("end") LocalDate end);
}
