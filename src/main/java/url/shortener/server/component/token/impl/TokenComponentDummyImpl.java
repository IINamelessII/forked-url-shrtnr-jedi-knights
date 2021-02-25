package url.shortener.server.component.token.impl;

import io.micronaut.context.annotation.Requires;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.token.TokenComponent;

@Slf4j
@Singleton
@Requires(property = "token.dummy.enabled", value = "true", defaultValue = "true")
public class TokenComponentDummyImpl implements TokenComponent {

  private final Map<String, String> database = new HashMap<>();

  @Override
  public String createToken(@NotBlank String id) {
    String token = UUID.randomUUID().toString();
    database.put(token, id);
    return token;
  }

  @Override
  public void removeToken(@NotBlank String token) {
    database.remove(token);
  }

  @Override
  public Optional<String> validateToken(String token) {
    if (StringUtils.isBlank(token) || !database.containsKey(token)) {
      log.error("User with token '{}' is not authorized", token);
      return Optional.empty();
    }
    return Optional.ofNullable(database.get(token));
  }
}
