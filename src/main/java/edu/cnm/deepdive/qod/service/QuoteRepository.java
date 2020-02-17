package edu.cnm.deepdive.qod.service;

import edu.cnm.deepdive.qod.model.entity.Quote;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

  Iterable<Quote> getAllByOrderByCreatedDesc();

  Iterable<Quote> getAllByTextContainsOrderByTextAsc(String fragment);

}
