package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Therapie;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface TherapieRepository extends Neo4jRepository<Therapie, String> {
  @Query("MATCH(t:Therapie) -[tr:THERAPEUT]-(n:Nutzer{nutzerName: $user}) "
      + "MATCH(t) -[td:THERAPIE_DETAILS]-(d:TherapieDetails) "
      + "WHERE d.buchungsdatum >= $start AND d.buchungsdatum <= $end "
      + "MATCH(t) -[pr:PATIENT]-(p:Person) "
      + "MATCH(t) -[ti:THERAPIE_IN]-(a:Adresse) "
      + "MATCH(a) -[io:IN_ORT]-(o:Ort) "
      + "RETURN t,collect(tr),collect(n),collect(td),collect(d),collect(pr),collect(p),collect(ti),collect(a),collect(io),collect(o)")
  List<Therapie> findAllByDate(@Param("user") String user,@Param("start") LocalDate start,@Param("end") LocalDate end);

  @Query("MATCH(t:Therapie) RETURN t")
  List<Therapie> findAllTherapie();
}
