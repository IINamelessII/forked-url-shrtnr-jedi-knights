package url.shortener.server.component.encryption;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface HashingComponent {

  String hash(@NotBlank String plainText);

  boolean match(@NotBlank String hashedValue, @NotNull String plainText);
}
