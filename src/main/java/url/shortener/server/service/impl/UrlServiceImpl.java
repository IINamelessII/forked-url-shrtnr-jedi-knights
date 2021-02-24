package url.shortener.server.service.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.synonym.SynonymsSearchComponent;
import url.shortener.server.config.exception.NotUniqueIdException;
import url.shortener.server.config.properties.UrlProperties;
import url.shortener.server.dto.UrlCreateDto;
import url.shortener.server.dto.UrlsListDto;
import url.shortener.server.entity.ShortenedUrl;
import url.shortener.server.entity.UserUrl;
import url.shortener.server.mapper.UserUrlMapper;
import url.shortener.server.repository.UrlRepository;
import url.shortener.server.repository.UserUrlRepository;
import url.shortener.server.service.UrlService;

@Singleton
public class UrlServiceImpl implements UrlService {

  private final UrlRepository urlRepository;
  private final UserUrlRepository userUrlRepository;
  private final SynonymsSearchComponent synonymsComponent;
  private final UserUrlMapper userUrlMapper;
  private final int proposalCount;

  @Inject
  public UrlServiceImpl(
      UrlRepository urlRepository,
      UserUrlRepository userUrlRepository,
      SynonymsSearchComponent synonymsComponent,
      UserUrlMapper userUrlMapper,
      UrlProperties urlProperties
  ) {
    this.urlRepository = urlRepository;
    this.userUrlRepository = userUrlRepository;
    this.synonymsComponent = synonymsComponent;
    this.userUrlMapper = userUrlMapper;
    this.proposalCount = urlProperties.getProposalCount();
  }

  @Override
  public String createUrl(String userId, UrlCreateDto urlCreateDto) {
    String alias = urlCreateDto.getAlias();
    if (StringUtils.isBlank(alias)) {
      alias = saveWithGeneratedAlias(urlCreateDto.getUri());
    } else {
      saveWithGivenAlias(alias, urlCreateDto.getUri());
    }
    userUrlRepository.save(
        new UserUrl()
            .setUserId(userId)
            .setAlias(alias)
    );
    return alias;
  }

  private String saveWithGeneratedAlias(URI uri) {
    boolean isSaved = false;
    String availableAlias = "";

    while (!isSaved) {
      availableAlias = urlRepository.nextAvailableAlias();
      ShortenedUrl shortenedUrl = new ShortenedUrl()
          .setAlias(availableAlias)
          .setOriginalUrl(uri);
      isSaved = urlRepository.save(shortenedUrl);
    }
    return availableAlias;
  }

  private void saveWithGivenAlias(String alias, URI uri) {
    if (!urlRepository.existsById(alias)) {
      urlRepository.save(
          new ShortenedUrl()
              .setAlias(alias)
              .setOriginalUrl(uri)
      );
      return;
    }
    throw new NotUniqueIdException(
        generateProposal(alias)
    );
  }

  private List<String> generateProposal(String alias) {
    List<String> synonyms = new ArrayList<>();
    if (synonymsComponent.isSearchable(alias)) {
      synonymsComponent.retrieveSynonyms(alias, proposalCount)
          .stream()
          .filter(synonym -> !urlRepository.existsById(synonym))
          .forEach(synonyms::add);
    }
    if (synonyms.size() < proposalCount) {
      List<String> availableAliases = urlRepository
          .findAvailableAliases(proposalCount - synonyms.size());
      synonyms.addAll(availableAliases);
    }
    return synonyms;
  }

  @Override
  public UrlsListDto getUserUrls(String userId) {
    Set<String> aliases = userUrlRepository.findAll(userId)
        .stream()
        .map(UserUrl::getAlias)
        .collect(Collectors.toSet());

    return userUrlMapper.toListDto(
        urlRepository.findAll(aliases)
    );
  }

  @Override
  public void deleteUserUrl(String userId, String alias) {
    UserUrl userUrl = new UserUrl()
        .setUserId(userId)
        .setAlias(alias);

    urlRepository.deleteById(alias);
    userUrlRepository.delete(userUrl);
  }

  @Override
  public URI getOriginalUrl(String alias) {
    return urlRepository.findById(alias)
        .map(ShortenedUrl::getOriginalUrl)
        .orElseThrow(() -> new RuntimeException("Not found"));
  }
}
