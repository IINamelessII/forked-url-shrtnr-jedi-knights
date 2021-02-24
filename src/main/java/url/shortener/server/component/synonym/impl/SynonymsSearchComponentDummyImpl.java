package url.shortener.server.component.synonym.impl;

import io.micronaut.context.annotation.Requires;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import url.shortener.server.component.synonym.SynonymsSearchComponent;

@Singleton
@Requires(property = "synonyms.dummy.enabled", value = "true", defaultValue = "true")
public class SynonymsSearchComponentDummyImpl implements SynonymsSearchComponent {

  @Override
  public boolean isSearchable(@NotBlank String alias) {
    return false;
  }

  @Override
  public List<String> retrieveSynonyms(@NotBlank String alias, int proposalCount) {
    return Collections.emptyList();
  }
}
