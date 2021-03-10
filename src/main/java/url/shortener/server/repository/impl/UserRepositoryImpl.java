package url.shortener.server.repository.impl;

import java.util.Objects;
import java.util.Optional;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.entity.User;
import url.shortener.server.repository.UserRepository;

@Singleton
public class UserRepositoryImpl implements UserRepository {

  private final BigTable userTable;

  public UserRepositoryImpl(@Named("userTable") BigTable userTable) {
    this.userTable = userTable;
  }

  @Override
  public Optional<User> findById(@NotNull String id) {
    Objects.requireNonNull(id);

    return userTable.findByKey(id)
        .map(value ->
            new User()
                .setEmail(id)
                .setPassword(value)
        );
  }

  @Override
  public boolean existsById(@NotNull String id) {
    Objects.requireNonNull(id);

    return userTable.containsKey(id);
  }

  @Override
  public void deleteById(@NotNull String id) {

    userTable.deleteByKey(id);
  }

  @Override
  public boolean save(@NotNull User user) {
    Objects.requireNonNull(user);

    return userTable.put(user.getEmail(), user.getPassword());
  }
}
