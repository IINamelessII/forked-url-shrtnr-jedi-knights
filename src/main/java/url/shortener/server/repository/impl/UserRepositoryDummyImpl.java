package url.shortener.server.repository.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import url.shortener.server.entity.User;
import url.shortener.server.repository.UserRepository;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserRepositoryDummyImpl implements UserRepository {

  private final Map<String, String> users = new HashMap<>();

  @Override
  public Optional<User> findById(@NotNull String id) {
    Objects.requireNonNull(id);

    return Optional.ofNullable(users.get(id))
        .map(
            value -> new User()
                .setEmail(id)
                .setEmail(value)
        );
  }

  @Override
  public boolean existsById(@NotNull String id) {
    Objects.requireNonNull(id);
    return users.containsKey(id);
  }

  @Override
  public void deleteById(@NotNull String id) {
    Objects.requireNonNull(id);
    users.remove(id);
  }

  @Override
  public boolean save(@NotNull User user) {
    Objects.requireNonNull(user);
    users.put(user.getEmail(), user.getPassword());
    return true;
  }
}
