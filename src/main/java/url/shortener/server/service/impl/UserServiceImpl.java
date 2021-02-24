package url.shortener.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.encryption.HashingComponent;
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

  @Override
  public void createUser(UserCreateDto userCreateDto) {
    User userToSave = userMapper.to(userCreateDto);

    if (userRepository.existsById(userToSave.getEmail())) {
      throw new RuntimeException("User with such email already exists 409");
    }
    log.info("Save user with id '{}'", userCreateDto.getEmail());
    userRepository.save(userToSave);
  }

  @Override
  public TokenDto logInUser(UserCreateDto userCreateDto) {
    return userRepository.findById(userCreateDto.getEmail())
        .filter(user -> hashingComponent.match(user.getPassword(), userCreateDto.getPassword()))
        .map(user -> new TokenDto()
            .setToken(
                RandomStringUtils.random(25)
            ))
        .orElseThrow(() -> new RuntimeException("Unauthorized 401"));
  }

  @Override
  public void logOutUserByToken(String authToken) {
    if (StringUtils.isBlank(authToken)) {
      throw new RuntimeException("Unauthorized 401");
    }
  }
}
