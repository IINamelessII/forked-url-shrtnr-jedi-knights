package url.shortener.server.component.token.impl;

import java.util.Optional;
import java.util.UUID;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.component.token.TokenComponent;

@Slf4j
@Singleton
public class TokenComponentImpl implements TokenComponent {

  private final BigTable tokenTable;

  public TokenComponentImpl(@Named("tokenTable") BigTable tokenTable) {
    this.tokenTable = tokenTable;
  }

  @Override
  public String createToken(@NotBlank String id) {
    String token = UUID.randomUUID().toString();
    tokenTable.put(token, id);
    return token;
  }

  @Override
  public void removeToken(@NotBlank String token) {
    tokenTable.deleteByKey(token);
  }

  @Override
  public Optional<String> validateToken(String token) {
    if (StringUtils.isBlank(token)) {
      return Optional.empty();
    }
    return tokenTable.findByKey(token);
  }
}
