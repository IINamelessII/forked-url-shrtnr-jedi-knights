package url.shortener.server.controller;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import java.net.URI;
import javax.inject.Inject;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import url.shortener.server.component.url.UrlComponent;
import url.shortener.server.dto.UrlCreateDto;
import url.shortener.server.dto.UrlsListDto;
import url.shortener.server.service.UrlService;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UrlController {

  private final UrlComponent urlComponent;
  private final UrlService urlService;

  @Post(value = "urls/shorten", consumes = APPLICATION_JSON)
  public HttpResponse<Void> createUrlAlias(
      @Valid @Body UrlCreateDto urlCreateDto
  ) {
    String alias = urlService.createUrl("test_user", urlCreateDto);

    return HttpResponse.created(
        urlComponent.createLocationUri(alias)
    );
  }

  @Get(value = "urls", produces = APPLICATION_JSON)
  public UrlsListDto getAllUserUrls() {
    return urlService.getUserUrls("test_user");
  }

  @Delete("urls/{alias}")
  public HttpResponse<Void> deleteUrl(
      @PathVariable("alias") String alias
  ) {
    urlService.deleteUserUrl("test_user", alias);
    return HttpResponse.status(HttpStatus.NO_CONTENT);
  }

  @Get("r/{alias}")
  public HttpResponse<Void> redirect(
      @PathVariable("alias") String alias
  ) {
    URI originalUrl = urlService.getOriginalUrl(alias);
    return HttpResponse.redirect(originalUrl);
  }
}
