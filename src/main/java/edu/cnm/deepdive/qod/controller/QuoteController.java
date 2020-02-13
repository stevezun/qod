package edu.cnm.deepdive.qod.controller;

import edu.cnm.deepdive.qod.model.entity.Quote;
import edu.cnm.deepdive.qod.service.QuoteRepository;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("quotes")
public class QuoteController {

  private final QuoteRepository repository;

  @Autowired
  public QuoteController(QuoteRepository repository) {
    this.repository = repository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote post(@RequestBody Quote quote) {
    return repository.save(quote);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Quote> get() {
    return repository.getAllByOrderByCreatedDesc();
  }

  @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Quote get(@PathVariable UUID id) {
    return repository.findById(id).get();
  }

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void notFound() {}

}
