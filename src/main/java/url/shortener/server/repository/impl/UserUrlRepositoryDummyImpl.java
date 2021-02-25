package url.shortener.server.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import url.shortener.server.entity.UserUrl;
import url.shortener.server.repository.UserUrlRepository;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserUrlRepositoryDummyImpl implements UserUrlRepository {

  private final Map<String, List<UserUrl>> userUrls = new HashMap<>();

  @Override
  public List<UserUrl> findAll(String userId) {
    return userUrls.getOrDefault(userId, Collections.emptyList());
  }

  @Override
  public boolean save(UserUrl userUrl) {
    List<UserUrl> urls = this.userUrls.get(userUrl.getUserId());
    if (urls == null) {
      urls = new ArrayList<>();
    }
    urls.add(userUrl);
    userUrls.put(userUrl.getUserId(), urls);
    return true;
  }

  @Override
  public void delete(UserUrl userUrl) {
    List<UserUrl> urls = this.userUrls.get(userUrl.getUserId());
    if (urls == null || urls.isEmpty()) {
      return;
    }
    urls.remove(userUrl);
  }
}
