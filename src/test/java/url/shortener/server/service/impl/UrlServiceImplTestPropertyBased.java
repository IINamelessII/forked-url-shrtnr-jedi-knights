package url.shortener.server.service.impl;

import static net.andreinc.mockneat.unit.types.Ints.ints;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
  UrlServiceImpl urlService;

  final static int REPEATS = 1000;
  final static String DUMMY_USER_ID = "0";
  final static String DUMMY_URI = "https://duckduckgo.com/";

  static void getStatisticsOnPBT(String methodName, int successCounter) {
    System.out.printf(
        "[%s]: %s of %s tests passed (%.2f%%)%n",
        methodName,
        successCounter,
        REPEATS,
        (float) successCounter / REPEATS * 100
    );
  }

//  @BeforeEach
// void createDummyUser() {
//   UserCreateDto userDTO = new UserCreateDto()
//       .setEmail("dummy@gmail.com")
//       .setPassword("hardOne123");
//
//   userService.createUser(userDTO);
// }

  @AfterEach
  void removeDBFolder() {
    TestUtils.purgeDirectory(new File("BigTable"));
  }

  @Test
  void createUrlPBT() {
    final MockNeat mock = MockNeat.threadLocal();
    boolean isSuccessfulPBT = true;
    int createUrlSuccessCounter = 0;

    for (int i = 0; i < REPEATS; i++) {
      String mockAlias = mock.strings().size(ints().range(5, 10)).get();

      try {
        UrlCreateDto urlDTO = new UrlCreateDto()
            .setUri(new URI("https://www.google.com/"))
            .setAlias(mockAlias);

        if (!StringUtils.isBlank(mockAlias) && !mockAlias.matches("[A-Za-z0-9]+")) {
          System.out.printf("[REGEX MATCH] Alias: \"%s\"%n", mockAlias);
          isSuccessfulPBT = false;
        }

        try {
          urlService.createUrl(DUMMY_USER_ID, urlDTO);
        } catch (NotUniqueAliasException notUniqueAliasException) {
          System.out.printf("[NOT UNIQUE ALIAS] Alias: \"%s\"%n", mockAlias);
          isSuccessfulPBT = false;
        }
      } catch (URISyntaxException uriSyntaxException) {
        System.out.printf(
            "[URI SYNTAX EXCEPTION] Message: \"%s\"%n",
            uriSyntaxException.getMessage()
        );
        isSuccessfulPBT = false;
      }

      createUrlSuccessCounter++;
    }

    getStatisticsOnPBT("createUrlPBT", createUrlSuccessCounter);

    Assertions.assertTrue(isSuccessfulPBT);
  }

  @Test
  void getOriginalUrlPBT() {
    final MockNeat mock = MockNeat.threadLocal();
    boolean isSuccessfulPBT = true;
    int getOriginalUrlSuccessCounter = 0;

    for (int i = 0; i < REPEATS; i++) {
      String mockAlias = mock.strings().size(ints().range(5, 10)).get();

      try {
        UrlCreateDto urlDTO = new UrlCreateDto().setUri(new URI(DUMMY_URI)).setAlias(mockAlias);

        try {
          urlService.createUrl(DUMMY_USER_ID, urlDTO);

          try {
            String dummyURIFromDB = urlService.getOriginalUrl(mockAlias).toString();

            if (!dummyURIFromDB.equals(DUMMY_URI)) {
              System.out.printf(
                  "[URI EQUALITY] URI \"%s\" is not equal to \"%s\" for alias \"%s\"%n",
                  dummyURIFromDB,
                  DUMMY_URI,
                  mockAlias
              );
              isSuccessfulPBT = false;
            }
          } catch (BusinessException businessException) {
            System.out.printf(
                "[BUSINESS EXCEPTION] Message: \"%s\"%n",
                businessException.getMessage()
            );
            isSuccessfulPBT = false;
          }
        } catch (NotUniqueAliasException notUniqueAliasException) {
          System.out.printf("[NOT UNIQUE ALIAS] Alias: \"%s\"%n", mockAlias);
          isSuccessfulPBT = false;
        }
      } catch (URISyntaxException uriSyntaxException) {
        System.out.printf(
            "[URI SYNTAX EXCEPTION] Message: \"%s\"%n",
            uriSyntaxException.getMessage()
        );
        isSuccessfulPBT = false;
      }

      getOriginalUrlSuccessCounter++;
    }

    getStatisticsOnPBT("getOriginalUrl", getOriginalUrlSuccessCounter);

    Assertions.assertTrue(isSuccessfulPBT);
  }

  @Test
  void deleteUserUrlPBT() {
    final String dummyId = "0";

    qt().forAll(
      strings().basicLatinAlphabet().ofLengthBetween(0, 10)
      , strings().basicLatinAlphabet().ofLengthBetween(0, 10)
    ).check((alias, dummyURIs) -> {
      try {
        UrlCreateDto urlCreateDto = new UrlCreateDto()
          .setUri(new URI(dummyURIs))
          .setAlias(alias);

        urlService.createUrl(dummyId, urlCreateDto);

        try {
          urlService.deleteUserUrl(dummyId, alias);

        } catch (BusinessException businessException) {
          return false;
        }
      } catch (URISyntaxException uriSyntaxException) {
        return false;
      }

      return true;
    });
  }
}
