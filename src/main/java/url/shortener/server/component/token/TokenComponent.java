package url.shortener.server.component.token;

import javax.validation.constraints.NotBlank;

public interface TokenComponent {

  String createToken(@NotBlank String id);

  void removeToken(@NotBlank String token);

  void checkValid(String token);
}
