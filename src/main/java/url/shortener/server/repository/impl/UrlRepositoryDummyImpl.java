package url.shortener.server.repository.impl;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import url.shortener.server.entity.ShortenedUrl;
import url.shortener.server.repository.UrlRepository;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UrlRepositoryDummyImpl implements UrlRepository {

  private final Map<String, String> database = new HashMap<>();


  @Override
  public List<ShortenedUrl> findAll(Set<String> aliases) {
    return Collections.emptyList();
  }

  @Override
  public Optional<ShortenedUrl> findById(String alias) {
    String originUrl = database.get(alias);
    if (originUrl == null) {
      return Optional.empty();
    }
    ShortenedUrl shortenedUrl = new ShortenedUrl()
      .setAlias(alias)
      .setOriginalUrl(URI.create(originUrl));
    return Optional.of(shortenedUrl);
  }

  @Override
  public boolean save(ShortenedUrl shortenedUrl) {
    database.put(shortenedUrl.getAlias(), shortenedUrl.getOriginalUrl().toString());
    return true;
  }

  @Override
  public void deleteById(String alias) {
    database.remove(alias);
  }

  @Override
  public boolean existsById(String alias) {
    return database.containsKey(alias);
  }

  @Override
  public String nextAvailableAlias() {
    return null;
  }

  @Override
  public List<String> findAvailableAliases(int count) {
    return null;
  }
}
