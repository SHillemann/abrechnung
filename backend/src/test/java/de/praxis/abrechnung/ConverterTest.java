package de.praxis.abrechnung;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import de.praxis.abrechnung.model.TherapieDetails;
import de.praxis.abrechnung.rest.TherapieRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ConverterTest {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();


  @Test
   void testRegex() throws MethodArgumentNotValidException {
    var expectations = List.of("24  M  2  H","2.4","2h45m","2.75","1","0.0166","0.75","0.75","195","3.25","2h","2","1.3","1.3","45m 2h","2.75","11m","0.1833");
    for(int i = 0;i<expectations.size();i+=2)
    {
      assertEquals(new BigDecimal(expectations.get(i+1)), TherapieDetails.converterToRepoGebucht(expectations.get(i)));
      assertValidation(expectations.get(i));
    }
  }

  @Test
  void testRegexNegative()
  {
    var expectations = List.of("0..,0","0.2.2","1,5,5","166,,","0","0.0","0,0");
    for(int i = 0;i<expectations.size();++i)
    {
        assertNegativeValidation(expectations.get(i));
    }
  }
  private void assertValidation(String expect) {
    TherapieRequest request = TherapieRequest.builder().gebucht(expect).build();
    Set<ConstraintViolation<TherapieRequest>> violations = validator.validate(request);
    for (ConstraintViolation<TherapieRequest> violation : violations) {
        if(violation.getPropertyPath().toString().equals("gebucht"))
        {
          fail("validation failed for gebucht: "+expect);
        }
    }
  }

  private void assertNegativeValidation(String expect) {
    TherapieRequest request = TherapieRequest.builder().gebucht(expect).build();
    Set<ConstraintViolation<TherapieRequest>> violations = validator.validate(request);
    for (ConstraintViolation<TherapieRequest> violation : violations) {
      if(violation.getPropertyPath().toString().equals("gebucht"))
      {
        return;
      }
    }
    fail("validation not failed for gebucht: "+expect);
  }
}
