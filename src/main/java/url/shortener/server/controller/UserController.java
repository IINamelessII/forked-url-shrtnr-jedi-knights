package url.shortener.server.controller;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import javax.inject.Inject;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import url.shortener.server.dto.TokenDto;
import url.shortener.server.dto.UserCreateDto;
import url.shortener.server.service.UserService;

@Controller("/users")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserController {

  private final UserService userService;

  @Post(value = "signup", consumes = APPLICATION_JSON)
  public void createUser(@Valid @Body UserCreateDto userCreateDto) {
    userService.createUser(userCreateDto);
  }

  @Post(value = "signin", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
  public TokenDto loginUser(
      @Valid @Body UserCreateDto userCreateDto
  ) {
    return userService.logInUser(userCreateDto);
  }

  @Post("signout")
  public void logOutUser(
      @Header(value = "Authorization", defaultValue = "") String authToken
  ) {
    //TODO check is authorized
    userService.logOutUserByToken(authToken);
  }
}
