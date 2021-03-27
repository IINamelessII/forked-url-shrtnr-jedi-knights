package url.shortener.server.service.impl;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.io.File;
import javax.inject.Inject;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import url.shortener.server.TestUtils;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.dto.UserCreateDto;

@MicronautTest
public class UserServiceImplTestPropertyBased {

  private final static int PBT_REPEATS = 1000;

  @Inject
  UserServiceImpl userService;

  @AfterEach
  void removeDBFolder() {
    TestUtils.purgeDirectory(new File("BigTable"));
  }

  @Test
  void shouldAuthorizeUser_propertyBased() {
    final MockNeat mock = MockNeat.threadLocal();

    UserCreateDto failedValue = null;

    for (int i = 0; i < PBT_REPEATS && failedValue == null; ++i) {
      String email = mock.emails().get();
      String password = mock.strings().size(mock.ints().range(8, 16)).get();

      var userDto = new UserCreateDto().setEmail(email).setPassword(password);

      try {
        userService.createUser(userDto);

        userService.logInUser(userDto);
      } catch (BusinessException exc) {
        System.out.printf("BusinessException: %s", exc.getMessage());
        failedValue = userDto;
      }

    }

    Assertions.assertNull(failedValue);
  }
}
