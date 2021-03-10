package url.shortener.server.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import javax.inject.Named;
import javax.inject.Singleton;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.bigtable.MultiValueTable;
import url.shortener.server.bigtable.impl.BigTableImpl;
import url.shortener.server.bigtable.impl.MultiValueTableImpl;

@Factory
public class RepositoryFactory {

  @Bean
  @Singleton
  @Named("tokenTable")
  public BigTable tokenTable() {
    return new BigTableImpl(
        "token",
        5,
        40
    );
  }

  @Bean
  @Singleton
  @Named("urlTable")
  public BigTable urlTable() {
    return new BigTableImpl(
        "url",
        5,
        20
    );
  }

  @Bean
  @Singleton
  @Named("userTable")
  public BigTable userTable() {
    return new BigTableImpl(
        "user",
        2,
        64
    );
  }

  @Bean
  @Singleton
  @Named("userUrlTable")
  public MultiValueTable userUrlTable() {
    return new MultiValueTableImpl(
        "userUrl",
        5,
        64
    );
  }
}
