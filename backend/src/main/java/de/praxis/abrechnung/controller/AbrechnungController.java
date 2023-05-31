package de.praxis.abrechnung.controller;

import de.praxis.abrechnung.model.Person;
import de.praxis.abrechnung.rest.AbwesenheitRequest;
import de.praxis.abrechnung.rest.Buchungsblock;
import de.praxis.abrechnung.rest.DayRequest;
import de.praxis.abrechnung.rest.FahrtzeitRequest;
import de.praxis.abrechnung.rest.TagResponse;
import de.praxis.abrechnung.rest.TherapieRequest;
import de.praxis.abrechnung.rest.TherapieResponse;
import de.praxis.abrechnung.rest.UpdateTagRequest;
import de.praxis.abrechnung.service.AbrechnungService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/abrechnung")
@RequiredArgsConstructor
public class AbrechnungController {

  private final AbrechnungService abrechnungService;


  @PostMapping("/day")
  public ResponseEntity<List<TagResponse>> getDayByName(@RequestBody DayRequest day) {
    return ResponseEntity.ok(abrechnungService.getDayByName(day.getName(), day.getDate()));
  }
  @GetMapping("/name/{surname}")
  public ResponseEntity<List<Person>> getByName(@PathVariable String surname) {
    return ResponseEntity.ok(abrechnungService.getSurname(surname));
  }
  @PostMapping("/save/therapie")
  public ResponseEntity<Buchungsblock> saveTherapie(@Valid @RequestBody TherapieRequest request) {
    if(request.getId() != null) {
      request.setReihenfolge(abrechnungService.therapieDelete(request.getId()));
    }
    return ResponseEntity.ok(abrechnungService.therapiePersist(request));
  }
  @PostMapping("/save/abwesend")
  public ResponseEntity<Buchungsblock> saveAbwesend(@Valid @RequestBody AbwesenheitRequest request) {
    return ResponseEntity.ok(abrechnungService.abwesenheitPersist(request));
  }
  @GetMapping("/delete/therapie/{id}")
  public ResponseEntity<Integer> deleteTherapie(@PathVariable String id)
  {
    return ResponseEntity.ok(abrechnungService.therapieDelete(id));
  }

  @PostMapping("/save/fahrtzeit")
  public ResponseEntity<String> saveFahrtweg(@RequestBody FahrtzeitRequest request)
  {
    abrechnungService.fahrtzeitPersist(request);
    return ResponseEntity.ok("saved");
  }

  @PostMapping("/save/abwesenheit")
  public ResponseEntity<String> saveFahrtweg(@RequestBody AbwesenheitRequest request)
  {
    abrechnungService.abwesenheitPersist(request);
    return ResponseEntity.ok("saved");
  }
  @PostMapping("/updateTag")
  public  ResponseEntity<String> updateTag(@RequestBody UpdateTagRequest request)
  {
    abrechnungService.updateTag(request);
    return ResponseEntity.ok("updated");
  }

  @GetMapping("/therapie/{id}")
  public ResponseEntity<TherapieResponse> getTherapie(@PathVariable String id)
  {
    return ResponseEntity.ok(abrechnungService.getTherapieById(id));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    errors.put("error", "true");
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

}
