package url.shortener.server.repository.impl;

import java.util.List;
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

  @Override
  public List<ShortenedUrl> findAll(Set<String> aliases) {
    return null;
  }

  @Override
  public Optional<ShortenedUrl> findById(String alias) {
    return Optional.empty();
  }

  @Override
  public boolean save(ShortenedUrl shortenedUrl) {
    return false;
  }

  @Override
  public void deleteById(String alias) {

  }

  @Override
  public boolean existsById(String alias) {
    return false;
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
