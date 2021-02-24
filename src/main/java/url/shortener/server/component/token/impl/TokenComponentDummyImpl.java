package url.shortener.server.component.token.impl;

import io.micronaut.context.annotation.Requires;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.token.TokenComponent;

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
  public void checkValid(String token) {
    if (StringUtils.isBlank(token) || !database.containsKey(token)) {
      throw new RuntimeException("Unauthorized 401");
    }
  }
}
