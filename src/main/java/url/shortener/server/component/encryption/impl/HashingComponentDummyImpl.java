package url.shortener.server.component.encryption.impl;

import io.micronaut.context.annotation.Requires;
import java.util.Objects;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import url.shortener.server.component.encryption.HashingComponent;

@Singleton
@Requires(property = "hashing.dummy.enabled", value = "true", defaultValue = "true")
public class HashingComponentDummyImpl implements HashingComponent {

  @Override
  public String hash(@NotBlank String plainText) {
    return plainText;
  }

  @Override
  public boolean match(@NotBlank String hashedValue, @NotNull String plainText) {
    return Objects.equals(hashedValue, plainText);
  }
}
