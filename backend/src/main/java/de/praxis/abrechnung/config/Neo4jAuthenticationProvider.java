package de.praxis.abrechnung.config;

import de.praxis.abrechnung.service.NutzerService;
import java.util.ArrayList;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class Neo4jAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private NutzerService nutzerService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName();
    String password = authentication.getCredentials().toString();

    var nutzer = nutzerService.getNutzer(name,password);

      if (Objects.isNull(nutzer)) {
        return null;
      }
      // Possible to add more information from user
      var authorities = new ArrayList<GrantedAuthority>();
      authorities.add(new SimpleGrantedAuthority(nutzer.getNutzerRolle().toString()));
      final var principal = new User(name, password, authorities);

      return new UsernamePasswordAuthenticationToken(principal, password, authorities);
    }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
