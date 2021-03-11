package url.shortener.server.bigtable.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Disabled to do not disturb file system each time when build project")
class MultiValueTableImplTest {

  private static final String TABLE_NAME = "multivaluetest";
  private static final int BUCKETS_NUMBER = 3;
  private static final int KEY_LENGTH = 5;

  private static final MultiValueTableImpl INSTANCE = new MultiValueTableImpl(
    TABLE_NAME, BUCKETS_NUMBER, KEY_LENGTH
  );

  @Test
  void saveAndGetExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue = "TestValueSuccess";

    boolean saveResult = INSTANCE.put(testKey, testValue);
    List<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(saveResult).isTrue();
    assertThat(searchResult).containsExactlyInAnyOrder(testValue);
  }

  @Test
  void getInvalidExpectEmpty() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);

    List<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(searchResult).isEmpty();
  }

  @Test
  void saveSameKeyExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue1 = "TestValueSameKey1";
    String testValue2 = "TestValueSameKey2";

    boolean saveFirstResult = INSTANCE.put(testKey, testValue1);
    boolean saveSecondResult = INSTANCE.put(testKey, testValue2);
    List<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(saveFirstResult).isTrue();
    assertThat(saveSecondResult).isTrue();
    assertThat(searchResult).contains(testValue1, testValue2);
  }

  @Test
  void deleteByKeyExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue = "TestValueDeleteSuccess";

    boolean saveResult = INSTANCE.put(testKey, testValue);
    boolean deleteResult = INSTANCE.deleteByKey(testKey, testValue);
    List<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(saveResult).isTrue();
    assertThat(deleteResult).isTrue();
    assertThat(searchResult).isEmpty();
  }

  @Test
  void deleteByKeyWhenSeveralValuesExpectSuccess() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue1 = "TestValue1DeleteSuccess";
    String testValue2 = "TestValue2DeleteSuccess";

    boolean saveResult1 = INSTANCE.put(testKey, testValue1);
    boolean saveResult2 = INSTANCE.put(testKey, testValue2);
    boolean deleteResult = INSTANCE.deleteByKey(testKey, testValue1);
    List<String> searchResult = INSTANCE.findByKey(testKey);

    assertThat(saveResult1).isTrue();
    assertThat(saveResult2).isTrue();
    assertThat(deleteResult).isTrue();
    assertThat(searchResult).containsExactlyInAnyOrder(testValue2);
  }

  @Test
  void deleteByKeyWhenKeyNotFoundExpectFailure() {
    String testKey = RandomStringUtils.randomAlphanumeric(1, KEY_LENGTH);
    String testValue = "TestValueDeleteFailure";

    boolean deleteResult = INSTANCE.deleteByKey(testKey, testValue);

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
