package de.praxis.abrechnung.model;


import de.praxis.abrechnung.rest.TherapieRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.validation.ValidationException;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.MethodParameter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Data
@Builder
@Node("TherapieDetails")
public class TherapieDetails {

  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  private String id;

  @NonNull
  private LocalDate buchungsdatum;

  @NonNull
  private BigDecimal gebucht;

  @NonNull
  private Integer reihenfolge;

  private String beschreibung;

  private Integer hausbesuch;

  public static BigDecimal converterToRepoGebucht(String gebucht){
    BigDecimal sixty = new BigDecimal("60");
    var input = gebucht.replaceAll("\\s","").replaceAll(",",".").toLowerCase();
    var matcher = Pattern.compile("(\\d{1,3})(\\.?+)([hm]{0,1})").matcher(input);
    BigDecimal output = null;
    var decimal = false;
    //first
    if(matcher.find())
    {
      //single
      if(Strings.isNotEmpty(matcher.group(1)))
      {
        output = new BigDecimal(matcher.group(1)).divide(sixty,4,RoundingMode.FLOOR);
      }
      //with point
      if(Strings.isNotEmpty(matcher.group(2)))
      {
        decimal = true;
      }
      //with h or m
      if(Strings.isNotEmpty(matcher.group(3)))
      {
        if(Objects.equals(matcher.group(3), "h"))
        {
          output = new BigDecimal(matcher.group(1));
        }
      }
    }
    //second
    if(matcher.find())
    {
      //digit
      if(Strings.isNotEmpty(matcher.group(1)) && decimal)
      {
        output = new BigDecimal(input);
      }
      //h or m
      if(Strings.isNotEmpty(matcher.group(3)))
      {
        if(Objects.equals(matcher.group(3), "m"))
        {
          output = output.add(new BigDecimal(matcher.group(1)).divide(sixty,4,RoundingMode.FLOOR));
        }
        if(Objects.equals(matcher.group(3), "h"))
        {
          output = output.add(new BigDecimal(matcher.group(1)));
        }
      }
    }
    //error more as expected
    if(matcher.find())
    {
      throw new RuntimeException();
    }
    return output.stripTrailingZeros();
  }
}
