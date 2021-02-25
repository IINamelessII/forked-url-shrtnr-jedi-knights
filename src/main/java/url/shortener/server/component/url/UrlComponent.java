package url.shortener.server.component.url;

import java.net.URI;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UrlComponent {

  //TODO make more universal
  @SneakyThrows
  public URI createLocationUri(String alias) {
    return new URIBuilder()
        .setScheme("http")
        .setHost("localhost")
        .setPort(8080)
        .setFragment("/r/{alias}")
        .addParameter("alias", alias)
        .build();
  }
}
