package url.shortener.server.bigtable.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Disabled to do not disturb file system each time when build project")
class BigTableImplTest {

  private static final String TABLE_NAME = "test";
  private static final int BUCKETS_NUMBER = 3;
  private static final int KEY_LENGTH = 5;

  private static final BigTableImpl INSTANCE = new BigTableImpl(
    TABLE_NAME, BUCKETS_NUMBER, KEY_LENGTH
  );

  @Test
  void saveAndGetExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue = "TestValueSuccess";

    boolean saveResult = INSTANCE.put(testKey, testValue);
    Optional<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(saveResult).isTrue();
    assertThat(searchResult).contains(testValue);
  }

  @Test
  void getInvalidExpectEmpty() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);

    Optional<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(searchResult).isEmpty();
  }

  @Test
  void saveSameKeyExpectFailure() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue1 = "TestValueSameKey1";
    String testValue2 = "TestValueSameKey2";

    boolean saveFirstResult = INSTANCE.put(testKey, testValue1);
    boolean saveSecondResult = INSTANCE.put(testKey, testValue2);
    Optional<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(saveFirstResult).isTrue();
    assertThat(saveSecondResult).isFalse();
    assertThat(searchResult).contains(testValue1);
  }

  @Test
  void containsKeyWhenKeyExistsExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue = "TestValueContainsSuccess";

    boolean saveResult = INSTANCE.put(testKey, testValue);
    boolean containsResult = INSTANCE.containsKey(testKey);

    assertThat(saveResult).isTrue();
    assertThat(containsResult).isTrue();
  }

  @Test
  void containsKeyWhenKeyNotExistsExpectFailure() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);

    boolean containsResult = INSTANCE.containsKey(testKey);

    assertThat(containsResult).isFalse();
  }

  @Test
  void deleteByKeyExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue = "TestValueDeleteSuccess";

    boolean saveResult = INSTANCE.put(testKey, testValue);
    boolean deleteResult = INSTANCE.deleteByKey(testKey);
    boolean containsResult = INSTANCE.containsKey(testKey);

    assertThat(saveResult).isTrue();
    assertThat(deleteResult).isTrue();
    assertThat(containsResult).isFalse();
  }

  @Test
  void deleteByKeyWhenKeyNotFoundExpectFailure() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);

    boolean deleteResult = INSTANCE.deleteByKey(testKey);

    assertThat(deleteResult).isFalse();
  }

  @AfterAll
  static void cleanUp() throws IOException {
    System.out.println(MessageFormat.format("Start DB '{0}' cleanup", TABLE_NAME));
    Files.walk(INSTANCE.getDatabaseRoot().toPath())
      .sorted(Comparator.reverseOrder())
      .map(Path::toFile)
      .forEach(File::deleteOnExit);
    System.out.println(MessageFormat.format("DB '{}' cleanup finished", TABLE_NAME));
  }
}
