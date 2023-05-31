package de.praxis.abrechnung.service;

import de.praxis.abrechnung.model.Nutzer;
import de.praxis.abrechnung.repository.NutzerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class NutzerService {

  private final NutzerRepository nutzerRepository;

  public Nutzer getNutzer(String nutzername, String password)
  {
    return nutzerRepository.getNutzer(nutzername,password);
  }

}
