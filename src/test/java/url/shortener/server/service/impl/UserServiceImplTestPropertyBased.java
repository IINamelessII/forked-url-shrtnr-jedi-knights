package url.shortener.server.service.impl;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.io.File;
import javax.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import url.shortener.server.TestUtils;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.dto.UserCreateDto;

@Disabled("Disabled due to being flaky (fails upon creation of a user with an already existing id, which is actually an expected behaviour)")
@MicronautTest
public class UserServiceImplTestPropertyBased {

  @Inject
  UserServiceImpl userService;

  @AfterEach
  void removeDBFolder() {
    TestUtils.purgeDirectory(new File("BigTable"));
  }

  @Test
  void shouldAuthorizeUser_propertyBased() {
    qt().forAll(
        strings().basicLatinAlphabet().ofLengthBetween(1, 10),
        strings().basicLatinAlphabet().ofLengthBetween(1, 10)
    ).check((email, password) -> {
      if (!email.matches("[A-z].+")) {
        return true;
      }

      var userDto = new UserCreateDto().setEmail(email).setPassword(password);

      try {
        userService.createUser(userDto);

        userService.logInUser(userDto);
      } catch (BusinessException exc) {
        return false;
      }

      return true;
    });
  }

}
