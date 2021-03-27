package url.shortener.server.service.impl;

import static net.andreinc.mockneat.unit.types.Ints.ints;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.StringUtils;
import url.shortener.server.TestUtils;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.config.exception.NotUniqueAliasException;
import url.shortener.server.dto.UrlCreateDto;


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

  @AfterAll
  static void removeDBFolder() {
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
        final MockNeat mock = MockNeat.threadLocal();
        boolean isSuccessfulPBT = true;

        for (int i = 0; i < REPEATS; i++) {
            String mockAlias = mock.strings().size(ints().range(3, 10)).get();
            String mockURI = mock.strings().size(ints().range(3, 10)).get();

            try {
                UrlCreateDto urlDTO = new UrlCreateDto().setUri(new URI(mockURI)).setAlias(mockAlias);

                try {
                    urlService.createUrl(DUMMY_USER_ID, urlDTO);

                    try {
                        urlService.deleteUserUrl(DUMMY_USER_ID, mockAlias);
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
        }
        Assertions.assertTrue(isSuccessfulPBT);
    }

}
