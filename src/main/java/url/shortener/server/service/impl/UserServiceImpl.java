package url.shortener.server.service.impl;

import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.encryption.HashingComponent;
import url.shortener.server.component.token.TokenComponent;
import url.shortener.server.dto.TokenDto;
import url.shortener.server.dto.UserCreateDto;
import url.shortener.server.entity.User;
import url.shortener.server.mapper.UserMapper;
import url.shortener.server.repository.UserRepository;
import url.shortener.server.service.UserService;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final HashingComponent hashingComponent;
  private final TokenComponent tokenComponent;

  @Override
  public void createUser(UserCreateDto userCreateDto) {

    if (userRepository.existsById(userCreateDto.getEmail())) {
      throw new RuntimeException("User with such email already exists 409");
    }
    User userToSave = userMapper.to(userCreateDto)
        .setPassword(hashingComponent.hash(userCreateDto.getPassword()));

    log.info("Save user with id '{}'", userCreateDto.getEmail());
    userRepository.save(userToSave);
  }

  @Override
  public TokenDto logInUser(UserCreateDto userCreateDto) {
    return userRepository.findById(userCreateDto.getEmail())
        .filter(user -> hashingComponent.match(user.getPassword(), userCreateDto.getPassword()))
        .map(user -> tokenComponent.createToken(user.getEmail()))
        .map(token -> new TokenDto().setToken(token))
        .orElseThrow(() -> new RuntimeException("Unauthorized 401"));
  }

  @Override
  public void logOutUserByToken(String authToken) {
    checkTokenNotBlank(authToken);
    tokenComponent.removeToken(authToken);
  }

  @Override
  public Optional<String> authorizeUser(String authToken) {
    checkTokenNotBlank(authToken);
    return tokenComponent.validateToken(authToken);
  }

  private void checkTokenNotBlank(String authToken) {
    if (StringUtils.isBlank(authToken)) {
      //TODO improve exception handling
      throw new RuntimeException("Unauthorized 401");
    }
  }
}
