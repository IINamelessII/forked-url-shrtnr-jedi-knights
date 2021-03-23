package url.shortener.server.service.impl;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import url.shortener.server.TestUtils;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.config.exception.NotUniqueAliasException;
import url.shortener.server.dto.UrlCreateDto;
import url.shortener.server.dto.UserCreateDto;

@MicronautTest
public class UrlServiceImplTestPropertyBased {
  @Inject
  UserServiceImpl userService;
  @Inject
  UrlServiceImpl urlServiceImpl;

  final static String dummyURI = "https://duckduckgo.com/";

  @BeforeEach
  void createDummyUser() {
    UserCreateDto userCreateDto = new UserCreateDto()
        .setEmail("dummy@gmail.com")
        .setPassword("hardOne123");

    userService.createUser(userCreateDto);
  }

  @AfterEach
  void removeDBFolder() {
    TestUtils.purgeDirectory(new File("BigTable"));
  }

  @Test
  void createUrlPBT() {
    final String dummyId = "0";

    qt().forAll(
        strings().basicLatinAlphabet().ofLengthBetween(0, 10)
    ).check(alias -> {
      try {
        UrlCreateDto urlCreateDto = new UrlCreateDto()
            .setUri(new URI("https://www.google.com/"))
            .setAlias(alias);

        if (!StringUtils.isBlank(alias) && !alias.matches("[A-Za-z0-9]+")) {
          // System.out.printf("[REGEX MATCH] Alias: \"%s\"%n", alias);
          return false;
        }

        try {
          urlServiceImpl.createUrl(dummyId, urlCreateDto);
        } catch (NotUniqueAliasException notUniqueAliasException) { // should be thrown in the future
          // System.out.printf("[NOT UNIQUE ALIAS] Alias: \"%s\"%n", alias);
          return false;
        }
      } catch (URISyntaxException uriSyntaxException) {
        /* System.out.printf(
            "[URI SYNTAX EXCEPTION] Message: \"%s\"%n",
            uriSyntaxException.getMessage()
        ); */
        return false;
      };

      return true;
    });
  }

  @Test
  void getOriginalUrlPBT() {
    final String dummyId = "0";

    qt().forAll(
        strings().basicLatinAlphabet().ofLengthBetween(0, 10)
    ).check(alias -> {
      try {
        UrlCreateDto urlCreateDto = new UrlCreateDto()
            .setUri(new URI(dummyURI))
            .setAlias(alias);

          urlServiceImpl.createUrl(dummyId, urlCreateDto);

          try {
            final String dummyURIFromDB = urlServiceImpl.getOriginalUrl(alias).toString();

            if (dummyURIFromDB.equals(dummyURI)) {
              /* System.out.printf(
                  "[URI EQUALITY] URI \"%s\" is not equal to \"%s\" for alias \"%s\"%n",
                  dummyURIFromDB,
                  dummyURI,
                  alias
              ); */
              return false;
            }
          } catch (BusinessException businessException) {
            /* System.out.printf(
                "[BUSINESS EXCEPTION] Message: \"%s\"%n",
                businessException.getMessage()
            ); */
            return false;
          }
      } catch (URISyntaxException uriSyntaxException) {
        /* System.out.printf(
            "[URI SYNTAX EXCEPTION] Message: \"%s\"%n",
            uriSyntaxException.getMessage()
        ); */
        return false;
      };

      return true;
    });
  }
}
