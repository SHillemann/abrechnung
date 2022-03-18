package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Fahrzeit;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface FahrzeitRepository extends Neo4jRepository<Fahrzeit, String> {
  @Query("MATCH(f:Fahrzeit) -[tr:THERAPEUT]-(n:Nutzer{nutzerName: $user}) "
      + "WHERE f.buchungsdatum >= $start AND f.buchungsdatum <= $end "
      + "MATCH(f) -[fv:FAHRT_VON]-(av:Adresse) "
      + "MATCH(a) -[iov:IN_ORT]-(ov:Ort) "
      + "MATCH(f) -[fn:FAHRT_NACH]-(an:Adresse) "
      + "MATCH(a) -[ion:IN_ORT]-(on:Ort) "
      + "RETURN f,collect(tr),collect(n),collect(fv),collect(av),collect(iov),collect(ov),collect(fn),collect(an),collect(ion),collect(on)")
  List<Fahrzeit> findAllByUserAndDate(@Param("user") String user,@Param("start") LocalDate start,@Param("end") LocalDate end);
}
