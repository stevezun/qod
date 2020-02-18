package edu.cnm.deepdive.qod.controller.rest;

import edu.cnm.deepdive.qod.controller.SearchTermTooShortException;
import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.model.entity.Source;
import edu.cnm.deepdive.qod.service.QuoteRepository;
import edu.cnm.deepdive.qod.service.SourceRepository;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/quotes")
@ExposesResourceFor(Quote.class)
public class QuoteController {

  private final QuoteRepository quoteRepository;
  private final SourceRepository sourceRepository;

  @Autowired
  public QuoteController(QuoteRepository quoteRepository, SourceRepository sourceRepository) {
    this.quoteRepository = quoteRepository;
    this.sourceRepository = sourceRepository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Quote> post(@RequestBody Quote quote) {
    quoteRepository.save(quote);
    return ResponseEntity.created(quote.getHref()).body(quote);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Quote> get() {
    return quoteRepository.getAllByOrderByCreatedDesc();
  }

  @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Quote> search(@RequestParam("q") String fragment) {
    if (fragment.length() < 3) {
      throw new SearchTermTooShortException();
    }
    return quoteRepository.getAllByTextContainsOrderByTextAsc(fragment);
  }

  @GetMapping(value = "/random", produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote getRandom() {
    return quoteRepository.getRandom().get();
  }

  @GetMapping(value = "/random", produces = MediaType.TEXT_PLAIN_VALUE)
  public String getRandomPlain() {
    return getRandom().getText();

  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote get(@PathVariable UUID id) {
    return quoteRepository.findById(id).get();
  }

  @PutMapping(value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote put(@PathVariable UUID id, @RequestBody Quote modifiedQuote) {
    Quote quote = get(id);
    quote.setText(modifiedQuote.getText());
    return quoteRepository.save(quote);
  }

  @PutMapping(value = "/{id}/text",
      consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String put(@PathVariable UUID id, @RequestBody String modifiedQuote) {
    Quote quote = get(id);
    quote.setText(modifiedQuote);
    quoteRepository.save(quote);
    return quote.getText();
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    // Code below throws NoSuchElementException if id is not in database.
//    Quote quote = get(id);
//    quoteRepository.delete(quote);
    quoteRepository.findById(id).ifPresent(quoteRepository::delete);
  }

  @PutMapping(value = "/{quoteId}/source/{sourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote attach(@PathVariable UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = get(quoteId);
    Source source = sourceRepository.findById(sourceId).get();
    if (!source.equals(quote.getSource())) {
      quote.setSource(source);
      quoteRepository.save(quote);
    }
    return quote;
  }

  @DeleteMapping(value = "/{quoteId}/source/{sourceId}")
  public Quote detach(@PathVariable UUID quoteId, @PathVariable UUID sourceId) {
    Quote quote = get(quoteId);
    Source source = sourceRepository.findById(sourceId).get();
    if (source.equals(quote.getSource())) {
      quote.setSource(null);
      quoteRepository.save(quote);
    }
    return quote;
  }

  @DeleteMapping(value = "/{quoteId}/source")
  public Quote clearSource(@PathVariable UUID quoteId) {
    Quote quote = get(quoteId);
    quote.setSource(null);
    return quoteRepository.save(quote);
  }

}
