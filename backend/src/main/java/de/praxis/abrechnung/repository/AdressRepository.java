package de.praxis.abrechnung.repository;

import de.praxis.abrechnung.model.Adresse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface AdressRepository extends Neo4jRepository<Adresse, String> {
  @Query("MATCH(a:Adresse) -[wr:WOHNT_IN]-(p:Person) WHERE p.name =~ '(?i).*'+$find+'.*' OPTIONAL MATCH(a) -[or:IN_ORT]-(o:Ort) RETURN a,collect(wr),collect(p),collect(or),collect(o)")
  List<Adresse> findByPartialName(@Param("find") String find);


  @Query("MATCH(a:Adresse) -[or:IN_ORT]-(o:Ort) WHERE a.strasse = $strasse AND o.ort = $ort AND o.plz = $plz RETURN a,collect(or),collect(o)")
  Optional<Adresse> findAdresse(@Param("strasse")String patient_strasse,@Param("plz") String patient_plz,@Param("ort") String patient_ort);
}
